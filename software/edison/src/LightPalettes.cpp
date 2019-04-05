
#include "mraa.hpp"

#include <iostream>
#include <string.h>

#include "ColorTables.h"
#include "LightEngineDocument.h"
#include "LightPalettes.h"
#include "Utilities.h"


LightPalette::LightPalette(): m_MotionSource(NULL)
{

}

void
LightPalette::set_MotionSource(MotionSource* motion)
{
	m_MotionSource = motion;
}

LightPaletteSolid::LightPaletteSolid(uint32_t led_count) :
    m_LedCount(led_count), m_RenderBuffer(led_count)
{
	this->ClearPixels();
}

LightPaletteSolid::~LightPaletteSolid()
{
}

void
LightPaletteSolid::ClearPixels()
{
	this->FillPixels(0, 0, 0);
}

void
LightPaletteSolid::FillPixels(uint8_t red, uint8_t green, uint8_t blue)
{
	uint32_t fill_val = PACK_COLOR_WORD(red, green, blue);
	uint32_t* word_buffer = m_RenderBuffer.buffer.word_buffer;

	uint32_t word_len = m_RenderBuffer.word_len;
	for (uint32_t ledidx=0; ledidx < word_len; ledidx++)
	{
		//Probably need to check this for endian correctness
		word_buffer[ledidx] = fill_val;
	}
}

void
LightPaletteSolid::SetPixel(uint32_t pindex, uint8_t red, uint8_t green, uint8_t blue)
{
	uint32_t word_len = m_RenderBuffer.word_len;
	if (pindex >= word_len) {
		std::cerr << "Invalid pixel index=" << pindex << " for led count=" << word_len << "." << std::endl;
		exit(MRAA_ERROR_UNSPECIFIED);
	}
	uint32_t set_val = PACK_COLOR_WORD(red, green, blue);
	m_RenderBuffer.buffer.word_buffer[pindex] = set_val;
}

int32_t
LightPaletteSolid::LoadXml(xmlNode *palette_node)
{
	int32_t rtn_status = 0;

	uint8_t red = 0;
	uint8_t green = 0;
	uint8_t blue = 0;

	xmlNode *next_node = palette_node->children;
	while(next_node != NULL)
	{
		if (next_node->type == XML_ELEMENT_NODE)
		{
			if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_BLUE) == 0)
			{
				const xmlChar* nval_str = next_node->children->content;
				blue = atoi((const char *)nval_str);
			}
			else if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_GREEN) == 0)
			{
				const xmlChar* nval_str = next_node->children->content;
				green = atoi((const char *)nval_str);
			}
			else if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_RED) == 0)
			{
				const xmlChar* nval_str = next_node->children->content;
				red = atoi((const char *)nval_str);
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
		this->FillPixels(red, green, blue);
	}

	return rtn_status;
}



LightPaletteWheel::LightPaletteWheel(uint32_t led_count, float brightness): m_LedCount(led_count),
	m_Brightness(brightness), m_RotationStart(0), m_RotationRate(0), m_RotationStep(0)
{
}

LightPaletteWheel::~LightPaletteWheel()
{
	for (uint32_t degree = 0; degree < LP_WHEEL_STEP_MAX; degree++)
	{
		delete m_RenderBuffers[degree];
	}
}

int32_t
LightPaletteWheel::LoadXml(xmlNode *palette_node)
{
	int32_t rtn_status = 0;

	uint32_t spin = LP_WHEEL_DEFAULT_SPIN;
	float brightness = LP_WHEEL_DEFAULT_BRIGHTNESS;

	xmlNode *next_node = palette_node->children;
	while(next_node != NULL)
	{
		if (next_node->type == XML_ELEMENT_NODE)
		{
			if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_SPINRATE) == 0)
			{
				const xmlChar* nval_str = next_node->children->content;
				spin = atoi((const char *)nval_str);
			}
			else if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_BRIGHTNESS) == 0)
			{
				const xmlChar* nval_str = next_node->children->content;
				brightness = atof((const char *)nval_str);
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
		m_Brightness = brightness;

		this->Spin(spin);
	}

	return rtn_status;
}

DotStarBuffer*
LightPaletteWheel::RenderBuffer()
{
	uint32_t buff_index = this->get_position();
	return m_RenderBuffers[buff_index];
}

void
LightPaletteWheel::Spin(uint32_t ms_rev)
{
	this->initialize_wheel();

	uint32_t us_rate = ms_rev * 1000;
	m_RotationStart = 0; //Start time is always zero
	m_RotationRate = us_rate;
	m_RotationStep = (uint32_t)(us_rate / LP_WHEEL_STEP_MAX);
}

uint32_t LightPaletteWheel::get_position()
{
	uint32_t position = 0;
	if (m_RotationRate > 0)
	{
		uint64_t time_now = get_time_us();
		uint64_t time_pos = (uint64_t)(time_now % m_RotationRate);
		position = ((uint64_t)(time_pos / m_RotationStep)) % LP_WHEEL_STEP_MAX;
	}
	return position;
}

void
LightPaletteWheel::initialize_wheel() {
	for (uint32_t step = 0; step < LP_WHEEL_STEP_MAX; step++) {
		m_RenderBuffers[step] = new DotStarBuffer(m_LedCount);
		uint32_t deg_color = RGBCOMPOSITE_1440STEP_TABLE[step];
		m_RenderBuffers[step]->fast_fill_buffer(deg_color);
	}
}



LightPaletteCompass::LightPaletteCompass(uint32_t led_count, float brightness):
	m_LedCount(led_count), m_Brightness(brightness), m_MinVal(LP_COMPASS_VALUE_MIN), m_MaxVal(LP_COMPASS_VALUE_MAX),
	m_MaxScale(0), m_NextSample(0)
{
	initialize_compass();
}

LightPaletteCompass::~LightPaletteCompass()
{
	for (uint32_t degree = 0; degree < LP_COMPASS_STEP_MAX; degree++)
	{
		delete m_RenderBuffers[degree];
	}
}

int32_t
LightPaletteCompass::LoadXml(xmlNode *palette_node)
{
	int32_t rtn_status = 0;

	float brightness = LP_COMPASS_DEFAULT_BRIGHTNESS;

	xmlNode *next_node = palette_node->children;
	while(next_node != NULL)
	{
		if (next_node->type == XML_ELEMENT_NODE)
		{
			if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_BRIGHTNESS) == 0)
			{
				const xmlChar* nval_str = next_node->children->content;
				brightness = atof((const char *)nval_str);
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
		m_Brightness = brightness;
	}

	return rtn_status;
}

DotStarBuffer*
LightPaletteCompass::RenderBuffer()
{
	uint32_t buff_index = this->get_position();
	return m_RenderBuffers[buff_index];
}

uint32_t
LightPaletteCompass::get_position()
{
	uint32_t position = 0;

	if (m_MotionSource != NULL)
	{
		m_MotionCapture.parts[0] = 0;
		m_MotionCapture.parts[1] = 0;
		m_MotionCapture.parts[2] = 0;

		// Get the last motion capture data
		m_MotionSource->Capture(&m_MotionCapture, false);

		int16_t raw_mag_val = m_MotionCapture.event.my;

		if (raw_mag_val < m_MinVal) {
			m_MinVal = raw_mag_val;
		}

		if (raw_mag_val > m_MaxVal) {
			m_MaxVal = raw_mag_val;
		}

		uint32_t adj_mag_val = raw_mag_val;

		if (m_MinVal < 0) {
			uint16_t abs_val_min = (m_MinVal * -1);
			m_MaxScale = abs_val_min + m_MaxVal;
			adj_mag_val = raw_mag_val + abs_val_min;
		}
		else {
			m_MaxScale = m_MaxVal - m_MinVal;
			adj_mag_val = raw_mag_val - m_MinVal;
		}

		m_Samples[m_NextSample] = adj_mag_val;
		m_NextSample = (m_NextSample + 1) % LP_COMPASS_SAMPLE_WINDOW_SIZE;

		uint64_t sample_avg = 0;
		for (int sindex = 0; sindex < LP_COMPASS_SAMPLE_WINDOW_SIZE; sindex++) {
			sample_avg = sample_avg + m_Samples[sindex];
		}

		sample_avg = sample_avg / LP_COMPASS_SAMPLE_WINDOW_SIZE;

		//std::cout << "Min (" << m_MinVal << ") Max(" << m_MaxVal << ")" << "Scale (" << m_MaxScale << ")" << std::endl;

		float position_factor = (float)(LP_COMPASS_STEP_MAX) / ((float)m_MaxScale);

		position = (uint32_t)(sample_avg * position_factor) % LP_COMPASS_STEP_MAX;

		//std::cout << "Compass (" << position << ")..." << std::endl;

	}

	return position;
}

void
LightPaletteCompass::initialize_compass() {
	for (uint32_t step = 0; step < LP_WHEEL_STEP_MAX; step++) {
		m_RenderBuffers[step] = new DotStarBuffer(m_LedCount);
		uint32_t deg_color = RGBCOMPOSITE_1440STEP_TABLE[step];
		m_RenderBuffers[step]->fast_fill_buffer(deg_color);
	}
}



LightPaletteDJ::LightPaletteDJ(uint32_t led_count, float brightness): m_LedCount(led_count), m_Brightness(brightness)
{
}

LightPaletteDJ::~LightPaletteDJ()
{
	delete m_RenderBuffer;
}

int32_t
LightPaletteDJ::LoadXml(xmlNode *palette_node)
{
	int32_t rtn_status = 0;

	float brightness = LP_WHEEL_DEFAULT_BRIGHTNESS;

	xmlNode *next_node = palette_node->children;
	while(next_node != NULL)
	{
		if (next_node->type == XML_ELEMENT_NODE)
		{
			if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_BRIGHTNESS) == 0)
			{
				const xmlChar* nval_str = next_node->children->content;
				brightness = atof((const char *)nval_str);
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
		m_Brightness = brightness;
	}

	return rtn_status;
}

DotStarBuffer*
LightPaletteDJ::RenderBuffer()
{
	return m_RenderBuffer;
}

void
LightPaletteDJ::initialize_dj() {
	uint32_t deg_color = RGBCOMPOSITE_1440STEP_TABLE[0];
	m_RenderBuffer = new DotStarBuffer(m_LedCount);
	m_RenderBuffer->fast_fill_buffer(deg_color);
}
