
#ifndef LIGHT_EFFECTS_INCLUDED_H
#define LIGHT_EFFECTS_INCLUDED_H

#include <unistd.h>
#include <stdint.h>

#include "DotStar.h"

#include <libxml/tree.h>
#include "MotionInterface.h"

class LightEffect: public MotionConsumer
{
public:
	LightEffect();
	virtual ~LightEffect();

	virtual void set_MotionSource(MotionSource* motion);

	virtual void Transform(uint32_t led_count, void* buffer) = 0;

	void ApplyTransforms(uint32_t led_count, void* buffer);
	void ChainEffect(LightEffect* effect);
	void RemoveEffects();

	virtual void MotionEvent(motion_capture* capture) = 0;
	virtual MotionConsumer* NextConsumer();

	virtual int32_t LoadXml(xmlNode *effect_node) = 0;

protected:
	LightEffect* m_Next;
	MotionSource* m_MotionSource;
};

class LightEffectStomp: public LightEffect
{
public:
	LightEffectStomp(uint32_t ms_duration, uint8_t flash_red=0xFF, uint8_t flash_green=0xFF, uint8_t flash_blue=0xFF);

	void Transform(uint32_t led_count, void* buffer);

	void MotionEvent(motion_capture* capture);

	int32_t LoadXml(xmlNode *effect_node);

private:
	bool m_Active;
	uint32_t m_Duration;

	uint64_t m_Start;
	uint64_t m_Stop;

	dot_color m_ActiveColor;
};

class LightEffectFall: public LightEffect
{
public:
	LightEffectFall(uint32_t ms_duration);

	void Transform(uint32_t led_count, void* buffer);

	void MotionEvent(motion_capture* capture);

	int32_t LoadXml(xmlNode *effect_node);

private:
	bool m_Active;
	uint32_t m_Duration;

	dot_color m_ActiveColor;
	dot_color m_IdleColor;
};

#endif //LIGHT_EFFECTS_INCLUDED_H
