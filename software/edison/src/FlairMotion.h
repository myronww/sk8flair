
#ifndef FLAIRMOTION_INCLUDED_H
#define FLAIRMOTION_INCLUDED_H

#include <netinet/in.h>

#include <pthread.h>

#include "FlairCast.h"
#include "MotionInterface.h"
#include "ThreadState.h"

enum MotionSenseDomain {
	MS_DOMAIN_NONE,
	MS_DOMAIN_FALL,
	MS_DOMAIN_STOMP,
	MS_DOMAIN_SPIN
};

#define MOTION_FALL_THRESHOLD -4000
#define MOTION_FALL_DETECT_TIME_MS 4000
#define MOTION_RECOVER_DETECT_TIME_MS 2000
#define MOTION_STOMP_RISE_THRESHOLD 15000
#define MOTION_STOMP_IMPACT_THRESHOLD -30000
#define MOTION_STOMP_TIMEOUT_MS 2000


class FlairMotion: public MotionSource
{

public:
	FlairMotion();
	virtual ~FlairMotion();

	virtual void Capture(motion_capture* capture, bool calibrated);
	virtual void CaptureAndDetect(motion_capture* capture, bool calibrated);

	virtual void ScaleCapture(f_motion_capture* f_capture, bool calibrated);
	virtual void ScaleCaptureAndDetect(f_motion_capture* f_capture, bool calibrated);

	virtual int32_t LoadXml(xmlNode *effect_node);

	virtual void StartMulticast(const char* group, uint32_t port, int64_t samples=-1);
	virtual void StopMulticast();

private:
	sockaddr_in m_MulticastAddr;

	struct {
		flair_data_sample header;
		motion_capture capture;
	} m_MulticastSample;

	int m_MulticastSock;
	int64_t m_MulticastSamples;

	LSM9DS0 m_MotionSensor;
	MotionSensorMode m_SensorMode;
	MotionSenseDomain m_SenseDomain;

	uint64_t m_FallDetectTime;
	uint64_t m_RecoverDetectTime;

	uint64_t m_FallCheckTime;
	uint64_t m_RecoverCheckTime;
	uint64_t m_StompCheckTime;

	uint64_t m_StompTimeout;

	int32_t m_FallThreshold;
	int32_t m_StompImpactThreshold;
	int32_t m_StompRiseThreshold;

	int32_t m_StompEvent;

	inline void _Capture(motion_event* capture, bool calibrated);
	inline void _MultiCastMotion(motion_capture* capture, bool calibrated);
	inline void _ScaleCapture(f_motion_event* capture, bool calibrated);

};

#endif //FLAIRMOTION_INCLUDED_H
