
#include <iostream>

#include <assert.h>
#include <string.h>

#include "LightEngineDocument.h"
#include "LightPalettes.h"
#include <libxml/xmlstring.h>
#include "FlairCast.h"
#include "FlairEngine.h"



FlairEngine::FlairEngine(uint32_t led_count, uint32_t data_pin, uint32_t clock_pin):
	m_LedCount(led_count), m_LightOutput(data_pin, clock_pin), m_Palette(NULL), m_Chain(NULL),
	m_RenderBuffer(led_count)
{
	m_Palette = new LightPaletteSolid(led_count);
	m_RenderBuffer.fast_fill_buffer(0x00000000);
}

LightPalette*
FlairEngine::get_Palette()
{
	return this->m_Palette;
}

void
FlairEngine::set_Palette(LightPalette* palette)
{
	LightPalette* old_palette = this->m_Palette;

	this->m_Palette = palette;
	this->m_Palette->set_MotionSource(&m_MotionSource);

	if (old_palette != NULL)
		delete old_palette;
}

void
FlairEngine::ChainEffect(LightEffect* effect)
{
	if (m_Chain == NULL) {
		m_Chain = effect;
	}
	else {
		m_Chain->ChainEffect(effect);
	}
}

int32_t
FlairEngine::LoadConfiguration(const char* configfile)
{
	int32_t rtn_status = 0;

	assert(configfile);

	/* Load XML document */
	xmlDocPtr xdoc = xmlParseFile(configfile);
	if (xdoc != NULL)
	{
		xmlNode *root_element = xmlDocGetRootElement(xdoc);
		if (root_element != NULL)
		{
			if (xmlStrcmp(root_element->name, LECONFIG_ELEMENT_FLAIRENGINE) == 0)
			{
				rtn_status = this->load_render_stack(root_element);
			}
			else
			{
				std::cerr << "Error (LoadConfiguration): invalid document root element '%s'." << root_element->name << std::endl;
				rtn_status = -1;
			}
		}
		else
		{
			std::cerr << "Error (LoadConfiguration): unable to get the root xml document element." << configfile << std::endl;
			rtn_status = -1;
		}

		xmlFreeDoc(xdoc);
	}
	else
	{
		std::cerr << "Error (LoadConfiguration): unable to parse file \"%s\"" << configfile << std::endl;
		rtn_status = -1;
	}

	return rtn_status;
}

void
FlairEngine::Propagate()
{
	if (m_Chain != NULL)
	{
		// Detect events and update the event state in the capture
		m_MotionSource.CaptureAndDetect(&m_Capture, false);

		MotionConsumer* next_consumer = m_Chain;
		while(next_consumer != NULL)
		{
			next_consumer->MotionEvent(&m_Capture);
			next_consumer = next_consumer->NextConsumer();
		}
	}
}

void
FlairEngine::RemoveEffects()
{
	if (m_Chain != NULL)
	{
		m_Chain->RemoveEffects();
		delete m_Chain;
		m_Chain = NULL;
	}
}

void
FlairEngine::Render()
{
	LightPalette* palette = this->m_Palette;
	DotStarBuffer* render_buffer = palette->RenderBuffer();

	uint8_t* buffer = render_buffer->buffer.byte_buffer;
	uint32_t buffer_len = render_buffer->byte_len;

	memcpy(m_RenderBuffer.buffer.byte_buffer, buffer, buffer_len);

	if (m_Chain != NULL) {
		m_Chain->ApplyTransforms(m_LedCount, m_RenderBuffer.buffer.byte_buffer);
	}

	m_LightOutput.RenderBuffer(m_RenderBuffer.buffer.byte_buffer, m_RenderBuffer.byte_len);
}

void
FlairEngine::SampleMotion(int64_t samples)
{
	m_MotionSource.StartMulticast(FLAIR_MULTICAST_GROUP, FLAIR_MULTICAST_DATACHANNEL_A_PORT, samples);
}

int32_t
FlairEngine::load_effect(xmlNode *effect_element, LightEffect** effect)
{
	int32_t rtn_status = 0;
	xmlChar *effect_type = xmlGetProp(effect_element, LECONFIG_ATTR_TYPE);

	std::cerr << "Loading effect " << effect_type << "..." << std::endl;

	if (xmlStrcmp(effect_type, EFFECT_TYPE_FALL) == 0)
	{
		*effect = new LightEffectFall(2000);
		(*effect)->LoadXml(effect_element);
	}
	else if (xmlStrcmp(effect_type, EFFECT_TYPE_STOMP) == 0)
	{
		*effect = new LightEffectStomp(800);
		(*effect)->LoadXml(effect_element);
	}
	else
	{
		std::cerr << "Error (load_effect): unsupported light palette type '%s' in FlairEngine render file." << effect_type << std::endl;
		rtn_status = -1;
	}

	return rtn_status;
}

int32_t
FlairEngine::load_effect_stack(xmlNode *effect_chain_element)
{
	int32_t rtn_status = 0;

	xmlNode *next_node = effect_chain_element->children;
	while(next_node != NULL)
	{
		if (next_node->type == XML_ELEMENT_NODE)
		{
			if (xmlStrcmp(next_node->name, LECONFIG_ELEMENT_EFFECT) == 0)
			{
				LightEffect* effect;
				rtn_status = this->load_effect(next_node, &effect);
				if (rtn_status == 0)
				{
					this->ChainEffect(effect);
				}
				else
				{
					std::cerr << "Error (load_effect_stack): problem loading effect node '"<< next_node->name <<"'." << std::endl;
				}
			}
			else
			{
				std::cerr << "Error (load_effect_stack): unknown element encountered '"<< next_node->name <<"' in FlairEngine render file." << std::endl;
				rtn_status = -1;
			}
		}

		if (rtn_status != 0) {
			break;
		}

		next_node = next_node->next;
	}

	return rtn_status;
}

int32_t
FlairEngine::load_render_stack(xmlNode *root_element)
{
	int32_t rtn_status = 0;

	xmlNode *next_node = root_element->children;
	while(next_node != NULL)
	{
		if (next_node->type == XML_ELEMENT_NODE)
		{
			if (xmlStrcmp(next_node->name, LECONFIG_ELEMENT_EFFECTCHAIN) == 0)
			{
				rtn_status = this->load_effect_stack(next_node);
			}
			else if (xmlStrcmp(next_node->name, LECONFIG_ELEMENT_MOTION) == 0)
			{
				rtn_status = m_MotionSource.LoadXml(next_node);
			}
			else if (xmlStrcmp(next_node->name, LECONFIG_ELEMENT_PALETTE) == 0)
			{
				rtn_status = this->load_palette(next_node);
			}
			else
			{
				std::cerr << "Error (load_render_stack): unknown element encountered '"<< next_node->name <<"' in FlairEngine render file." << std::endl;
				rtn_status = -1;
			}
		}

		if (rtn_status != 0) {
			break;
		}

		next_node = next_node->next;
	}

	return rtn_status;
}

int32_t
FlairEngine::load_palette(xmlNode *palette_element)
{
	int32_t rtn_status = 0;

	xmlChar *palette_type = xmlGetProp(palette_element, LECONFIG_ATTR_TYPE);
	LightPalette* palette = NULL;

	std::cerr << "Loading palette " << palette_type << "..." << std::endl;

	if (xmlStrcmp(palette_type, PALETTE_TYPE_SOLID) == 0)
	{
		palette = new LightPaletteSolid(this->m_LedCount);
	}
	else if (xmlStrcmp(palette_type, PALETTE_TYPE_WHEEL) == 0)
	{
		palette = new LightPaletteWheel(this->m_LedCount);
	}
	else if (xmlStrcmp(palette_type, PALETTE_TYPE_COMPASS) == 0)
	{
		palette = new LightPaletteCompass(this->m_LedCount);
	}
	else if (xmlStrcmp(palette_type, PALETTE_TYPE_DJ) == 0)
	{
		palette = new LightPaletteDJ(this->m_LedCount);
	}
	else
	{
		std::cerr << "Error (load_palette): unsupported light palette type '%s' in FlairEngine render file." << palette_type << std::endl;
		rtn_status = -1;
	}

	if(palette != NULL)
	{
		rtn_status = this->load_palette_activate(palette_element, palette_type, palette);
	}

	return rtn_status;
}

int32_t
FlairEngine::load_palette_activate(xmlNode *palette_element, xmlChar *palette_type, LightPalette* palette)
{
	int32_t rtn_status = palette->LoadXml(palette_element);
	if (rtn_status == 0)
	{
		this->set_Palette(palette);
	}
	else
	{
		std::cerr << "Error (load_palette_activate): Light palette '%s' failed to load xml parameters." << palette_type << std::endl;
		delete palette;
	}

	return rtn_status;
}


