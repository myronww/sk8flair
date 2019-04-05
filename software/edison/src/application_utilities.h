
#ifndef APPLICATION_UTILITIES_INCLUDED_H
#define APPLICATION_UTILITIES_INCLUDED_H

#include <unistd.h>
#include <stdint.h>

#define TIME_MSEC_TO_USEC(tval) (tval * 1000)

void error(const char *msg);

bool file_exists(const char* fname);

uint64_t get_time_us();

void pid_file_create();
void pid_file_delete();

#endif // APPLICATION_UTILITIES_INCLUDED_H
