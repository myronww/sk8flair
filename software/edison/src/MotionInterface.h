
#ifndef MOTION_INTERFACE_INCLUDED_H
#define MOTION_INTERFACE_INCLUDED_H

#include "MRAA_SFE_LSM9DS0.h"

#include <libxml/tree.h>

enum MotionSensorMode {
	MS_MODE_NORMAL,
	MS_MODE_CALIBRATE,
	MS_MODE_RECORD,
};

enum MotionEvent {
	ME_NONE,
	ME_FOOT_STOMP,
	ME_FALL_DETECTED,
	ME_FALL_RECOVERY,
	ME_SPIN_START,
	ME_SPIN_UPDATE,
	ME_SPIN_END
};


typedef union {
	motion_event event;
	uint64_t parts[3];
} motion_capture;

typedef union {
	f_motion_event event;
	uint64_t parts[6];
} f_motion_capture;

class MotionSource
{
public:
	virtual ~MotionSource() {};

	virtual void Capture(motion_capture* capture, bool calibrated) = 0;
	virtual void CaptureAndDetect(motion_capture* capture, bool calibrated) = 0;

	virtual void ScaleCapture(f_motion_capture* f_capture, bool calibrated) = 0;
	virtual void ScaleCaptureAndDetect(f_motion_capture* f_capture, bool calibrated) = 0;

	virtual int32_t LoadXml(xmlNode *effect_node) = 0;

	virtual void StartMulticast(const char* group, uint32_t port, int64_t samples=-1) = 0;
	virtual void StopMulticast() = 0;
};

class MotionConsumer
{
public:
	virtual ~MotionConsumer() {};

	virtual void MotionEvent(motion_capture* capture) = 0;
	virtual MotionConsumer* NextConsumer() = 0;
};

#endif //MOTION_INTERFACE_INCLUDED_H
