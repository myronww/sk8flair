
#include <iostream>
#include <stdlib.h>
#include <string.h>

#include "LightEffects.h"
#include "LightEngineDocument.h"

#include "Utilities.h"


LightEffect::LightEffect(): m_Next(NULL), m_MotionSource(NULL)
{
}

LightEffect::~LightEffect()
{
	if (m_Next != NULL)
	{
		delete m_Next;
		m_Next = NULL;
	}
}

void
LightEffect::set_MotionSource(MotionSource* motion)
{
	m_MotionSource = motion;

	if (m_Next != NULL)
	{
		m_Next->set_MotionSource(motion);
	}
}

void
LightEffect::ApplyTransforms(uint32_t led_count, void* buffer)
{
	Transform(led_count, buffer);

	if (m_Next != NULL)
		m_Next->ApplyTransforms(led_count,buffer);
}

void
LightEffect::ChainEffect(LightEffect* effect)
{
	if (m_Next == NULL)
	{
		m_Next = effect;
	}
	else
	{
		m_Next->ChainEffect(effect);
	}
}

MotionConsumer*
LightEffect::NextConsumer()
{
	return m_Next;
}

void
LightEffect::RemoveEffects()
{
	if (m_Next != NULL)
	{
		m_Next->RemoveEffects();
		delete m_Next;
		m_Next = NULL;
	}
}

LightEffectStomp::LightEffectStomp(uint32_t duration_ms, uint8_t flash_red, uint8_t flash_green, uint8_t flash_blue) :
	m_Active(false), m_Duration(TIME_MSEC_TO_USEC(duration_ms)), m_Start(0), m_Stop(0)
{
	m_ActiveColor.comp.header = 0xFF;
	m_ActiveColor.comp.blue = flash_blue;
	m_ActiveColor.comp.green = flash_green;
	m_ActiveColor.comp.red = flash_red;
}

void
LightEffectStomp::MotionEvent(motion_capture* capture)
{
	if (!m_Active)
	{
		if(capture->event.acc_event == ME_FOOT_STOMP)
		{
			//std::cout << "LightEffectStop::MotionEvent: activating effect." << std::endl;
			m_Active = true;
			m_Start = get_time_us();
			m_Stop = m_Start + m_Duration;
		}
	}
	else
	{
		uint64_t current_time = get_time_us();
		if (current_time > m_Stop)
		{
			//std::cout << "LightEffectStop::MotionEvent: deactivating effect. (" << current_time << ") > (" << m_Stop << ")" << std::endl;
			m_Active = false;
		}
	}
}

void
LightEffectStomp::Transform(uint32_t led_count, void* buffer)
{
	uint32_t* wide_buffer = (uint32_t*)buffer;

	if (m_Active == true)
	{
		for (uint32_t led_index=0; led_index < led_count; led_index++)
		{
			wide_buffer[led_index] = m_ActiveColor.word;
		}
	}
}

int32_t
LightEffectStomp::LoadXml(xmlNode *effect_node)
{
	int32_t rtn_status = 0;

	uint32_t duration = m_Duration;
	dot_color active_color;

	xmlNode *next_node = effect_node->children;
	while(next_node != NULL)
	{
		if (next_node->type == XML_ELEMENT_NODE)
		{
			if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_DURATION) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				duration = strtol((const char *)nval_str, &end_str, 10);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (LightEffectStomp::LoadXml): error processing duration. '" << nval_str << "'" << std::endl;
					rtn_status = -1;
				}
			}
			else if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_ACTIVECOLOR) == 0)
			{
				rtn_status = process_led_color_element(next_node, &active_color);
			}
			else
			{
				std::cerr << "Error (LightEffectStomp::LoadXml): unknown element encountered '" << next_node->name << "' in LightPalette render file." << std::endl;
				rtn_status = -1;
			}
		}

		if (rtn_status != 0) {
			break;
		}

		next_node = next_node->next;
	}

	if(rtn_status == 0)
	{
		m_Duration = TIME_MSEC_TO_USEC(duration);
		m_ActiveColor.word = active_color.word;

		//std::cout << "LightEffectStomp::LoadXml: duration=" << m_Duration << std::endl;
	}

	return rtn_status;
}

LightEffectFall::LightEffectFall(uint32_t duration_ms) :
	m_Active(false), m_Duration(TIME_MSEC_TO_USEC(duration_ms))
{
	m_ActiveColor.comp.header = 0xFF;
	m_ActiveColor.comp.blue = 0xFF;
	m_ActiveColor.comp.green = 0xFF;
	m_ActiveColor.comp.red = 0xFF;

	m_IdleColor.comp.header = 0xFF;
	m_IdleColor.comp.blue = 0x00;
	m_IdleColor.comp.green = 0x00;
	m_IdleColor.comp.red = 0xFF;
}

void
LightEffectFall::MotionEvent(motion_capture* capture)
{
	if(capture->event.mag_event == ME_FALL_DETECTED)
		m_Active = true;
	else
		m_Active = false;
}

void
LightEffectFall::Transform(uint32_t led_count, void* buffer)
{
	uint32_t* wide_buffer = (uint32_t*)buffer;

	if (m_Active == true)
	{
		uint64_t now = get_time_us();
		uint32_t quotient = now / m_Duration;
		uint32_t choice = (quotient % 2);

		uint32_t color_word = m_ActiveColor.word;
		if (choice > 0)
		{
			color_word = m_IdleColor.word;
		}

		for (uint32_t led_index=0; led_index < led_count; led_index++)
		{
			wide_buffer[led_index] = color_word;
		}
	}
}

int32_t
LightEffectFall::LoadXml(xmlNode *effect_node)
{
	int32_t rtn_status = 0;

	uint32_t duration = m_Duration;

	xmlNode *next_node = effect_node->children;
	while(next_node != NULL)
	{
		if (next_node->type == XML_ELEMENT_NODE)
		{
			if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_DURATION) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				duration = strtol((const char *)nval_str, &end_str, 10);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (LightEffectFall::LoadXml): error processing duration. '" << nval_str << "'" << std::endl;
					rtn_status = -1;
				}
			}
			else
			{
				std::cerr << "Error: unknown element encountered '" << next_node->name << "' in LightPalette render file." << std::endl;
				rtn_status = -1;
			}
		}

		if (rtn_status != 0) {
			break;
		}

		next_node = next_node->next;
	}

	if(rtn_status == 0)
	{
		m_Duration = TIME_MSEC_TO_USEC(duration);
	}

	return rtn_status;
}


