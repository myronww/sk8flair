
#ifndef LIGHT_PALETTES_INCLUDED_H
#define LIGHT_PALETTES_INCLUDED_H

#include <sys/time.h>
#include <stdint.h>
#include <unistd.h>

#include "DotStar.h"
#include <libxml/parser.h>
#include <libxml/tree.h>
#include "MotionInterface.h"

class LightPalette
{
public:
	LightPalette();
	virtual ~LightPalette() {};

	virtual void set_MotionSource(MotionSource* motion);

	virtual int32_t LoadXml(xmlNode *palette_node) = 0;
	virtual DotStarBuffer* RenderBuffer() = 0;
protected:
	MotionSource* m_MotionSource;
};

class LightPaletteSolid: public LightPalette
{
public:
	LightPaletteSolid(uint32_t led_count);
	virtual ~LightPaletteSolid();

	void ClearPixels();
	void FillPixels(uint8_t red, uint8_t green, uint8_t blue);
	void SetPixel(uint32_t pindex, uint8_t red, uint8_t green, uint8_t blue);

	virtual int32_t LoadXml(xmlNode *palette_node);
	virtual DotStarBuffer* RenderBuffer() { return &m_RenderBuffer; }
private:
	uint32_t m_LedCount;

	DotStarBuffer m_RenderBuffer;
};

#define LP_WHEEL_DEFAULT_BRIGHTNESS 1
#define LP_WHEEL_DEFAULT_SPIN 5000
#define LP_WHEEL_DEFAULT_QUADRANTSIZE 50
#define LP_WHEEL_STEP_MAX 1440

class LightPaletteWheel: public LightPalette
{
public:
	LightPaletteWheel(uint32_t led_count, float brightness=LP_WHEEL_DEFAULT_BRIGHTNESS);
	virtual ~LightPaletteWheel();

	void Spin(uint32_t rate=LP_WHEEL_DEFAULT_SPIN);

	virtual int32_t LoadXml(xmlNode *palette_node);
	virtual DotStarBuffer* RenderBuffer();

private:
	uint32_t m_LedCount;
	float m_Brightness;
	uint64_t m_RotationStart;
	uint32_t m_RotationRate;
	uint32_t m_RotationStep;

	uint32_t* m_QuadrantTables[3];

	DotStarBuffer* m_RenderBuffers[LP_WHEEL_STEP_MAX];

	uint32_t get_position();

	void initialize_quadrant_tables();
	void initialize_wheel();
};

#define LP_COMPASS_DEFAULT_BRIGHTNESS 1
#define LP_COMPASS_VALUE_MIN -30
#define LP_COMPASS_VALUE_MAX 30
#define LP_COMPASS_DEFAULT_QUADRANTSIZE 100
#define LP_COMPASS_SAMPLE_WINDOW_SIZE 5
#define LP_COMPASS_STEP_MAX 1440

class LightPaletteCompass: public LightPalette
{
public:
	LightPaletteCompass(uint32_t led_count, float brightness=LP_COMPASS_DEFAULT_BRIGHTNESS);
	virtual ~LightPaletteCompass();

	virtual int32_t LoadXml(xmlNode *palette_node);
	virtual DotStarBuffer* RenderBuffer();
private:
	uint32_t m_LedCount;
	float m_Brightness;

	int16_t m_MinVal;
	int16_t m_MaxVal;

	uint32_t m_MaxScale;

	DotStarBuffer* m_RenderBuffers[LP_COMPASS_STEP_MAX];

	motion_capture m_MotionCapture;

	uint32_t m_NextSample;
	uint32_t m_Samples[LP_COMPASS_SAMPLE_WINDOW_SIZE];
	uint32_t m_ColorTable[LP_COMPASS_STEP_MAX];

	uint32_t get_position();

	void initialize_compass();
};

#define LP_DJ_DEFAULT_BRIGHTNESS 1
#define LP_DJ_STEP_MAX 1440

class LightPaletteDJ: public LightPalette
{
public:
	LightPaletteDJ(uint32_t led_count, float brightness=LP_DJ_DEFAULT_BRIGHTNESS);
	virtual ~LightPaletteDJ();

	virtual int32_t LoadXml(xmlNode *palette_node);
	virtual DotStarBuffer* RenderBuffer();
private:
	uint32_t m_LedCount;
	float m_Brightness;

	DotStarBuffer* m_RenderBuffer;

	void initialize_dj();
};

#endif //LIGHT_PALETTES_INCLUDED_H
