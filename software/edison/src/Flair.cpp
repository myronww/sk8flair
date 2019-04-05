/*
 * Flair.cpp
 *
 *  Created on: Jun 9, 2015
 *      Author: Myron W. Walker
 *  Copyright (c) 2015 Myron W Walker
 */

#include "mraa.hpp"

#include <iostream>
#include <libgen.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>

#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/un.h>

#include <libxml/parser.h>
#include <libxml/tree.h>

#include "FlairEngine.h"
#include "FlairLooper.h"
#include "LightPalettes.h"

#define BLUETOOTH_SERVICE_PORT 1

FlairLooper* looper = NULL;

#define CLIENT_REQUEST_BUFFER_SIZE 4096
#define RESPONSE_BUFFER_SIZE 1024

const char *RESPONSE_TEMPLATE_INVALID_REQUEST_FORMAT = "Invalid request format '%s'.";
const char *RESPONSE_TEMPLATE_PROFILE_NOT_FOUND = "Profile '%s' was not found.";

const char *RESPONSE_SUCCESS = "SUCCESS";

const char *SVC_KEYWORD_CALIBRATE = "CALIBRATE";
#define SVC_KEYWORD_CALIBRATE_LEN 9
const char *SVC_KEYWORD_ECHO = "ECHO";
#define SVC_KEYWORD_ECHO_LEN 4
const char *SVC_KEYWORD_PROFILE = "PROFILE";
#define SVC_KEYWORD_PROFILE_LEN 7
const char *SVC_KEYWORD_RECORD = "RECORD";
#define SVC_KEYWORD_RECORD_LEN 6

#define MAX_PATH 1024

char FLAIR_FILE[MAX_PATH];
char FLAIR_DIR[MAX_PATH];
char FLAIR_PROFILES_DIR[MAX_PATH];
char FLAIR_CURRENT_PROFILE[MAX_PATH];
char FLAIR_RELAY_FILE[MAX_PATH];
char FLAIR_PROFILE_NAME[MAX_PATH];
const char* FLAIR_PID_FILE = "/tmp/Flair.pid";

void pid_file_delete()
{
	struct stat sb;
	if (stat(FLAIR_PID_FILE, &sb) > -1)
	{
		unlink(FLAIR_PID_FILE);
	}
}

void pid_file_create()
{
	pid_file_delete();

	char proc_pid_str[20];
	memset(proc_pid_str, 0, sizeof(proc_pid_str));

	pid_t proc_pid = getpid();
	sprintf(proc_pid_str, "%d\n", proc_pid);
	size_t proc_pid_str_len = strlen(proc_pid_str);

	FILE* pid_fd = fopen(FLAIR_PID_FILE, "w");
	fwrite(proc_pid_str, sizeof(char), proc_pid_str_len, pid_fd);
	fclose(pid_fd);
}

void
error(const char *msg)
{
    perror(msg);
    exit(1);
}

bool
file_exists(const char* fname)
{
	bool exists = false;

	if( access(fname, F_OK ) != -1 ) {
		exists = true;
	}

	return exists;
}

void
store_current_profile(const char* current_profile)
{
	size_t prof_len = strlen(current_profile);
	FILE* cp_fd = fopen(FLAIR_CURRENT_PROFILE, "w");
	fwrite(current_profile, sizeof(char), prof_len, cp_fd);
	fclose(cp_fd);
}

void
read_current_profile(char* current_profile_file, size_t len)
{
	std::cout << "Trying to read the name of the profile from '" << FLAIR_CURRENT_PROFILE << "'" << std::endl;

	if (!file_exists(FLAIR_CURRENT_PROFILE))
	{
		store_current_profile("compass.xml");
	}

	FILE* cp_fd = fopen(FLAIR_CURRENT_PROFILE, "r");
	fread(current_profile_file, sizeof(char), len, cp_fd);
	fclose(cp_fd);
}

void
load_profile(FlairEngine* flair_engine, const char* profile)
{
	char profile_file[MAX_PATH];
	memset(profile_file, 0, MAX_PATH);
	sprintf(profile_file, "%s/%s", FLAIR_PROFILES_DIR, profile);

	std::cout << "Loading settings from '" << profile_file << "'" << std::endl;

	int32_t load_result = flair_engine->LoadConfiguration(profile_file);
	if(load_result != 0) {
		std::cout << "Error loading configuration file." << std::endl;
		LightPaletteSolid* palette = new LightPaletteSolid(7);
		palette->FillPixels(255, 0, 0);
		flair_engine->set_Palette(palette);
	}
}

void
update_profile(const char* profile, const char* profile_content)
{
	unsigned int content_len = strlen(profile_content);

	char profile_file[MAX_PATH];
	memset(profile_file, 0, MAX_PATH);
	sprintf(profile_file, "%s/%s", FLAIR_PROFILES_DIR, profile);

	FILE* cp_fd = fopen(profile_file, "w");
	fwrite(profile_content, sizeof(char), content_len, cp_fd);
	fclose(cp_fd);
}

bool has_profile(const char* profile)
{
	char profile_file[MAX_PATH];
	memset(profile_file, 0, MAX_PATH);
	sprintf(profile_file, "%s/%s", FLAIR_PROFILES_DIR, profile);

	std::cout << "Checking for profile='" << profile_file << "'." << std::endl;
	bool exists = file_exists(profile_file);
	return exists;
}

int main()
{
	pid_file_create();

	/* Init libxml */
	xmlInitParser();
	LIBXML_TEST_VERSION;

	std::cout << "Starting up application." << std::endl;
	memset(FLAIR_FILE, 0, MAX_PATH);
	memset(FLAIR_DIR, 0, MAX_PATH);
	memset(FLAIR_RELAY_FILE, 0, MAX_PATH);
	memset(FLAIR_PROFILES_DIR, 0, MAX_PATH);
	memset(FLAIR_CURRENT_PROFILE, 0, MAX_PATH);
	memset(FLAIR_PROFILE_NAME, 0, MAX_PATH);

	readlink("/proc/self/exe", FLAIR_FILE, MAX_PATH);

	char* sk8flair_dir = dirname(FLAIR_FILE);
	strcpy(FLAIR_DIR, (const char*)sk8flair_dir);

	sprintf(FLAIR_RELAY_FILE, "%s/%s", FLAIR_DIR, "sk8flair_relay.sock");
	sprintf(FLAIR_CURRENT_PROFILE, "%s/%s", FLAIR_DIR, "current_profile.config");
	sprintf(FLAIR_PROFILES_DIR, "%s/%s", FLAIR_DIR, "profiles");

	unlink(FLAIR_RELAY_FILE);

	std::cout << "Setting up relay service." << std::endl;

	char request_buffer[CLIENT_REQUEST_BUFFER_SIZE];
	char response_buffer[RESPONSE_BUFFER_SIZE];

	struct sockaddr_un serv_addr;

	// Allocate the service socket
	int svc_socket = 0;
	svc_socket = socket(AF_UNIX, SOCK_STREAM, 0);

	if(svc_socket < 0) {
		error("Error creating service socket.");
	}

	// Bind the socket to the unix file
	serv_addr.sun_family = AF_UNIX;
	strcpy(serv_addr.sun_path, FLAIR_RELAY_FILE);

	if (bind(svc_socket, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0) {
		error("Error binding service socket.");
	}

	// Put the socket into listen mode so we will be able to accept connections, with a backlog of
	// 1 client for the moment
	listen(svc_socket, 1);

	FlairEngine* flair_engine = new FlairEngine(16);

	read_current_profile(FLAIR_PROFILE_NAME, MAX_PATH);

	load_profile(flair_engine, FLAIR_PROFILE_NAME);

	while(true)
	{
		// If the render object is NULL then we need to startup a render object.  This could be due
		// to the fact that we have not started one yet, or it could be that a change was made to the
		// render stack xml configuration file and we need to load the new render stack configuration.
		if (looper == NULL)
		{
			looper = new FlairLooper();
			looper->set_FlairEngine(flair_engine);
			looper->start();
		}

		// Clear out the client detail and request buffers
		memset(request_buffer, 0, CLIENT_REQUEST_BUFFER_SIZE);
		memset(response_buffer, 0, RESPONSE_BUFFER_SIZE);

		// Wait for a client to connect
		int client_socket = accept(svc_socket, NULL, NULL);
		if (client_socket >= 0)
		{
			std::cout << "Accepted connection on relay socket." << std::endl;

			// Read the request form the client
			int bytes_read = read(client_socket, request_buffer, CLIENT_REQUEST_BUFFER_SIZE);
			if (bytes_read > 0)
			{
				std::cout << "Received:"<< std::endl << request_buffer << std::endl;

				if(strncmp(request_buffer, SVC_KEYWORD_CALIBRATE, SVC_KEYWORD_CALIBRATE_LEN) == 0)
				{

				}
				else if(strncmp(request_buffer, SVC_KEYWORD_ECHO, SVC_KEYWORD_ECHO_LEN) == 0)
				{
					if ((strlen(request_buffer) > SVC_KEYWORD_RECORD_LEN) &&
						(request_buffer[SVC_KEYWORD_RECORD_LEN] == ' ')) {
						strcpy(response_buffer, (const char*)&request_buffer[SVC_KEYWORD_RECORD_LEN + 1]);
						size_t resp_len = strlen(response_buffer);
						write(client_socket, response_buffer, resp_len);
					}
					else {
						sprintf(response_buffer, RESPONSE_TEMPLATE_INVALID_REQUEST_FORMAT, SVC_KEYWORD_ECHO);
						size_t resp_len = strlen(response_buffer);
						write(client_socket, response_buffer, resp_len);
					}
				}
				else if(strncmp(request_buffer, SVC_KEYWORD_RECORD, SVC_KEYWORD_RECORD_LEN) == 0)
				{
					int samples = 1000;

					if ((strlen(request_buffer) > SVC_KEYWORD_RECORD_LEN) &&
					    (request_buffer[SVC_KEYWORD_RECORD_LEN] == ' ')) {
						samples = atoi(&request_buffer[SVC_KEYWORD_RECORD_LEN + 1]);
						flair_engine->SampleMotion(samples);
					}
					else {
						sprintf(response_buffer, RESPONSE_TEMPLATE_INVALID_REQUEST_FORMAT, SVC_KEYWORD_RECORD);
						size_t resp_len = strlen(response_buffer);
						write(client_socket, response_buffer, resp_len);
					}
				}
				else if(strncmp(request_buffer, SVC_KEYWORD_PROFILE, SVC_KEYWORD_PROFILE_LEN) == 0)
				{
					if ((strlen(request_buffer) > SVC_KEYWORD_PROFILE_LEN + 1) &&
						(request_buffer[SVC_KEYWORD_PROFILE_LEN] == ' ')) {
						char* profile_name_ptr = &request_buffer[SVC_KEYWORD_PROFILE_LEN + 1];
						size_t profile_name_len = 0;
						char* profile_content_ptr = strchr(profile_name_ptr, ' ');
						if (profile_content_ptr != NULL) {
							profile_name_len = profile_content_ptr - profile_name_ptr;
							profile_content_ptr += 1;
						}
						else {
							profile_name_len = strlen(profile_name_ptr);
						}

						char profile_name[MAX_PATH];
						memset(profile_name, 0, MAX_PATH);

						memcpy(profile_name, profile_name_ptr, profile_name_len);
						memcpy(&profile_name[profile_name_len], ".xml", 4);
						if (has_profile(profile_name))
						{
							if (profile_content_ptr != NULL) {
								update_profile(profile_name, profile_content_ptr);
							}

							strcpy(FLAIR_PROFILE_NAME, profile_name);
							store_current_profile(FLAIR_PROFILE_NAME);

							strcpy(response_buffer, RESPONSE_SUCCESS);

							// Shutdown the render thread delete it an NULL the variable so it will be
							// recreated and load the new render settings
							looper->stop();
							looper->join();
							delete looper;
							looper = NULL;

							delete flair_engine;
							flair_engine = new FlairEngine(16);
							load_profile(flair_engine, FLAIR_PROFILE_NAME);
						}
						else {
							sprintf(response_buffer, RESPONSE_TEMPLATE_PROFILE_NOT_FOUND, profile_name);
						}
					}
					else {
						sprintf(response_buffer, RESPONSE_TEMPLATE_INVALID_REQUEST_FORMAT, SVC_KEYWORD_PROFILE);
					}
					size_t resp_len = strlen(response_buffer);
					write(client_socket, response_buffer, resp_len);
				}
			}
			else if (bytes_read == -1) {
				std::cerr << "ERROR: No bytes read from socket." << std::endl;
			}

			// We have processed the client request so close the client socket so we can go back
			// and listen for another request
			close(client_socket);
		}
		else {
			std::cerr << "ERROR: Bad client socket returned from accept." << std::endl;
		}

	} //end while(true)

	// We are exiting so close the service socket
	close(svc_socket);
	unlink(FLAIR_RELAY_FILE);

	//sdp_close(registry_session);

	if (looper != NULL) {
		looper->join();
		delete looper;
	}

	if (flair_engine != NULL) {
		delete flair_engine;
	}

	/* Shutdown libxml */
	xmlCleanupParser();

	/*
	 * this is to debug memory for regression tests
	 */
	xmlMemoryDump();

	pid_file_delete();

	return MRAA_SUCCESS;
}
