
#include <iostream>

#include <string.h>

#include <arpa/inet.h>
#include <sys/socket.h>

#include "FlairMotion.h"
#include "LightEngineDocument.h"
#include "Utilities.h"

FlairMotion::FlairMotion() : m_MulticastSock(0), m_MulticastSamples(-1), m_MotionSensor(MODE_I2C, DEFAULT_GYRO_ADDRESS, DEFAULT_XM_ADDRESS),
	m_SensorMode(MS_MODE_NORMAL), m_SenseDomain(MS_DOMAIN_NONE),
	m_FallDetectTime(TIME_MSEC_TO_USEC(MOTION_FALL_DETECT_TIME_MS)), m_RecoverDetectTime(TIME_MSEC_TO_USEC(MOTION_RECOVER_DETECT_TIME_MS)),
	m_FallCheckTime(0), m_RecoverCheckTime(0), m_StompCheckTime(0),
	m_StompTimeout(TIME_MSEC_TO_USEC(MOTION_STOMP_TIMEOUT_MS)),
	m_FallThreshold(MOTION_FALL_THRESHOLD), m_StompImpactThreshold(MOTION_STOMP_IMPACT_THRESHOLD), m_StompRiseThreshold(MOTION_STOMP_RISE_THRESHOLD),
	m_StompEvent(-1)

{
	memset(&m_MulticastAddr,0,sizeof(m_MulticastAddr));

	m_MulticastSample.header.payload_size = sizeof(flair_data_sample) + sizeof(motion_capture);
	m_MulticastSample.header.payload_type = FLAIR_SAMPLEDATA_TYPE_IMU;

	LSM9DS0::gyro_scale g_scale = LSM9DS0::G_SCALE_245DPS;
	LSM9DS0::accel_scale a_scale = LSM9DS0::A_SCALE_2G;
	LSM9DS0::mag_scale m_scale = LSM9DS0::M_SCALE_2GS;

	LSM9DS0::gyro_odr g_odr = LSM9DS0::G_ODR_95_BW_125;
	LSM9DS0::accel_odr a_odr = LSM9DS0::A_ODR_50;
	LSM9DS0::mag_odr m_odr = LSM9DS0::M_ODR_50;

	m_MotionSensor.begin(g_scale, a_scale, m_scale, g_odr, a_odr, m_odr);
}

FlairMotion::~FlairMotion()
{
}

void
FlairMotion::Capture(motion_capture* capture, bool calibrated)
{
	this->_Capture(&capture->event, calibrated);
}

void
FlairMotion::CaptureAndDetect(motion_capture* capture, bool calibrated)
{
	if(m_SensorMode == MS_MODE_NORMAL)
	{
		this->_Capture(&capture->event, calibrated);

		if(m_SenseDomain == MS_DOMAIN_FALL)
		{
			capture->event.mag_event = ME_FALL_DETECTED;

			//std::cout << "Fall check (mx=" << capture->event.mx << ") (ft=" << m_FallThreshold << ")..." << std::endl;
			if (capture->event.mx > m_FallThreshold)
			{
				if (m_RecoverCheckTime > 0) {
					uint64_t now = get_time_us();
					uint64_t elapsed = now - m_RecoverCheckTime;

					if (elapsed > m_RecoverDetectTime) {
						m_FallCheckTime = 0;
						m_SenseDomain = MS_DOMAIN_NONE;
						capture->event.mag_event = 0;
					}
				}
				else {
					m_RecoverCheckTime = get_time_us();
				}
			}
			else
			{
				m_RecoverCheckTime = 0;
			}
		}
		else if(m_SenseDomain == MS_DOMAIN_STOMP)
		{
			if (m_StompEvent < 0)
			{
				uint64_t now = get_time_us();
				uint64_t time_in_stomp = now - m_StompCheckTime;
				if (time_in_stomp > m_StompTimeout)
				{
					//std::cout << "Stomp timeout..." << std::endl;
					m_SenseDomain = MS_DOMAIN_NONE;
				}
				else if (capture->event.az >= m_StompImpactThreshold)
				{
					capture->event.acc_event = ME_FOOT_STOMP;
					m_StompEvent = 1;
					//std::cout << "Stomp detected..." << std::endl;
				}
			}
			else if (m_StompEvent > 0)
			{
				capture->event.acc_event = ME_FOOT_STOMP;
				m_StompEvent -= 1;
			}
			else
			{
				m_StompCheckTime = 0;
				m_SenseDomain = MS_DOMAIN_NONE;
			}
		}
		else if(m_SenseDomain == MS_DOMAIN_SPIN)
		{

		}
		else
		{
			bool clear_fallcheck = true;

			if (capture->event.mx < m_FallThreshold)
			{
				//std::cout << "Fall detected (" << capture->event.mx << ")..." << std::endl;
				clear_fallcheck = false;

				if (m_FallCheckTime > 0) {
					uint64_t now = get_time_us();
					uint64_t elapsed = now - m_FallCheckTime;

					if (elapsed > m_FallDetectTime) {
						m_RecoverCheckTime = 0;
						m_SenseDomain = MS_DOMAIN_FALL;
						//capture->event.mag_event = ME_FALL_DETECTED;
					}
				}
				else {
					m_FallCheckTime = get_time_us();
				}
			}
			else if (capture->event.az <= m_StompRiseThreshold)
			{
				m_SenseDomain = MS_DOMAIN_STOMP;
				m_StompCheckTime = get_time_us();
				m_StompEvent = -1;
				//std::cout << "Rise detected...(" << capture->event.az << ") <= ("<< m_StompRiseThreshold << ")" << std::endl;
			}

			if (clear_fallcheck)
				m_FallCheckTime = 0;

		}
	}
	else
	{
		// Entering the code to perform a motion sensor calibration sequence

	}

}

int32_t
FlairMotion::LoadXml(xmlNode* motion_element)
{
	int32_t rtn_status = 0;

	uint32_t fall_detect_time = m_FallDetectTime;
	int32_t fall_threshold = m_FallThreshold;
	uint32_t recover_detect_time = m_RecoverDetectTime;
	int32_t stomp_impact = m_StompImpactThreshold;
	int32_t stomp_rise = m_StompRiseThreshold;
	uint32_t stomp_timeout = m_StompTimeout;

	xmlNode *next_node = motion_element->children;
	while(next_node != NULL)
	{
		if (next_node->type == XML_ELEMENT_NODE)
		{
			if (xmlStrcmp(next_node->name, LECONFIG_ELEMENT_FALLDETECTTIME) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				fall_detect_time = strtol((const char *)nval_str, &end_str, 10);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (FlairMotion::LoadXml): error processing duration. '" << nval_str << "'" << std::endl;
					rtn_status = -1;
				}
			}
			else if (xmlStrcmp(next_node->name, LECONFIG_ELEMENT_FALLTHRESHOLD) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				fall_threshold = strtof((const char *)nval_str, &end_str);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (FlairMotion::LoadXml): error processing duration. '" << nval_str << "'" << std::endl;
					rtn_status = -1;
				}
			}
			else if (xmlStrcmp(next_node->name, LECONFIG_ELEMENT_RECOVERDETECTTIME) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				recover_detect_time = strtol((const char *)nval_str, &end_str, 10);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (FlairMotion::LoadXml): error processing duration. '" << nval_str << "'" << std::endl;
					rtn_status = -1;
				}
			}
			else if (xmlStrcmp(next_node->name, LECONFIG_ELEMENT_STOMPIMPACTTHRESHOLD) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				stomp_impact = strtof((const char *)nval_str, &end_str);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (FlairMotion::LoadXml): error processing duration. '" << nval_str << "'" << std::endl;
					rtn_status = -1;
				}
			}
			else if (xmlStrcmp(next_node->name, LECONFIG_ELEMENT_STOMPRISETHRESHOLD) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				stomp_rise = strtof((const char *)nval_str, &end_str);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (FlairMotion::LoadXml): error processing duration. '" << nval_str << "'" << std::endl;
					rtn_status = -1;
				}
			}
			else if (xmlStrcmp(next_node->name, LECONFIG_ELEMENT_STOMPTIMEOUT) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				stomp_timeout = strtol((const char *)nval_str, &end_str, 10);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (FlairMotion::LoadXml): error processing duration. '" << nval_str << "'" << std::endl;
					rtn_status = -1;
				}
			}
			else
			{
				std::cerr << "Error (load_motion): unknown element encountered '"<< next_node->name <<"' in FlairEngine render file." << std::endl;
				rtn_status = -1;
			}
		}

		if (rtn_status != 0) {
			break;
		}

		next_node = next_node->next;
	}

	if (rtn_status == 0)
	{
		m_FallDetectTime = TIME_MSEC_TO_USEC(fall_detect_time);
		m_FallThreshold = fall_threshold;
		m_RecoverDetectTime = TIME_MSEC_TO_USEC(recover_detect_time);
		m_StompImpactThreshold = stomp_impact;
		m_StompRiseThreshold = stomp_rise;
		m_StompTimeout = TIME_MSEC_TO_USEC(stomp_timeout);
	}

	return rtn_status;
}

void
FlairMotion::ScaleCapture(f_motion_capture* f_capture, bool calibrated)
{

}

void
FlairMotion::ScaleCaptureAndDetect(f_motion_capture* f_capture, bool calibrated)
{

}

void
FlairMotion::StartMulticast(const char* group, uint32_t port, int64_t samples)
{
	m_MulticastSamples = samples;

	if(port == 0) {
		perror("FlairMotion: multicast port cannot be 0.");
		exit(1);
	}

	/* create what looks like an ordinary UDP socket */
	if ((m_MulticastSock=socket(AF_INET,SOCK_DGRAM,0)) < 0) {
		perror("FlairMotion: unable to open socket for multicast");
		exit(1);
	}

	/* set up destination address */
	memset(&m_MulticastAddr,0,sizeof(m_MulticastAddr));
	m_MulticastAddr.sin_family=AF_INET;
	m_MulticastAddr.sin_addr.s_addr=inet_addr(group);
	m_MulticastAddr.sin_port=htons(port);

}

void
FlairMotion::StopMulticast()
{
	close(m_MulticastSock);
	m_MulticastSock = -1;
	m_MulticastSamples = -1;
}

void
FlairMotion::_Capture(motion_event* sample_event, bool calibrated)
{
	// Read all the motion data from the sensor for this motion detection
	// event, Clear all the motion data
	memset(sample_event, 0, sizeof(motion_event));

	m_MotionSensor.readMotion(sample_event, false);
}

void
FlairMotion::_MultiCastMotion(motion_capture* capture, bool calibrated)
{
	if (m_MulticastSamples > 0) {
		m_MulticastSamples--;
		if(m_MulticastSamples == 0) {
			StopMulticast();
		}

		if (m_MulticastSock > -1) {

			memcpy(&m_MulticastSample.capture, capture, sizeof(motion_capture));

			int result = sendto(m_MulticastSock, (const void *)&m_MulticastSample, sizeof(m_MulticastSample), 0, (struct sockaddr *) &m_MulticastAddr, sizeof(m_MulticastAddr));
			if (result < 0) {
				perror("FlairMotion: sendto failure");
				exit(1);
			}
		}
	}
}

void
FlairMotion::_ScaleCapture(f_motion_event* f_sample_event, bool calibrated)
{
	// Read all the motion data from the sensor for this motion detection
	// event, Clear all the motion data
	motion_event sample_event;

	_Capture(&sample_event, calibrated);

	m_MotionSensor.calcMotion(&sample_event, f_sample_event);
}

