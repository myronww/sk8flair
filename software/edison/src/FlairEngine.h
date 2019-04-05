
#ifndef FLAIRENGINE_INCLUDED_H
#define FLAIRENGINE_INCLUDED_H

#include <unistd.h>
#include <stdint.h>

#include "DotStar.h"
#include "LightEffects.h"
#include "LightPalettes.h"
#include <libxml/tree.h>
#include <libxml/parser.h>
#include <libxml/xmlstring.h>
#include "FlairMotion.h"

//# GP130 (clock) - 35
//# GP131 (data) - 26


class FlairEngine
{
public:
	FlairEngine(uint32_t led_count, uint32_t data_pin=DOTSTAR_DATA_PIN, uint32_t clock_pin=DOTSTAR_CLOCK_PIN);

	LightPalette* get_Palette();

	void set_Palette(LightPalette* palette);

	void ChainEffect(LightEffect* effect);
	void RemoveEffects();

	int32_t LoadConfiguration(const char* configfile);

	void Propagate();
	void Render();

	void SampleMotion(int64_t samples);

private:
	uint32_t m_LedCount;
	DotStar m_LightOutput;
	LightPalette* m_Palette;
	FlairMotion m_MotionSource;
	LightEffect* m_Chain;

	DotStarBuffer m_RenderBuffer;

	motion_capture m_Capture;

	int32_t load_effect(xmlNode *effect_element, LightEffect** effect);
	int32_t load_effect_stack(xmlNode *root_element);
	int32_t load_palette(xmlNode *palette_element);
	int32_t load_palette_activate(xmlNode *palette_element, xmlChar *palette_type, LightPalette* palette);
	int32_t load_render_stack(xmlNode *root_element);
};

#endif //FLAIRENGINE_INCLUDED_H
