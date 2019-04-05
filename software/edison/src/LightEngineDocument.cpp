
#include <iostream>

#include "LightEngineDocument.h"

const xmlChar* LECONFIG_ELEMENT_EFFECT = (const xmlChar*)"Effect";
const xmlChar* LECONFIG_ELEMENT_EFFECTCHAIN = (const xmlChar*)"EffectChain";
const xmlChar* LECONFIG_ELEMENT_FLAIRENGINE = (const xmlChar*)"FlairEngine";
const xmlChar* LECONFIG_ELEMENT_MOTION = (const xmlChar*)"Motion";
const xmlChar* LECONFIG_ELEMENT_PALETTE = (const xmlChar*)"Palette";

const xmlChar* LECONFIG_ELEMENT_ACTIVECOLOR = (const xmlChar*)"ActiveColor";
const xmlChar* LECONFIG_ELEMENT_DURATION = (const xmlChar*)"Duration";
const xmlChar* LECONFIG_ELEMENT_FALLDETECTTIME = (const xmlChar*)"FallDetectTime";
const xmlChar* LECONFIG_ELEMENT_FALLTHRESHOLD = (const xmlChar*)"FallThreshold";
const xmlChar* LECONFIG_ELEMENT_IDLECOLOR = (const xmlChar*)"IdleColor";
const xmlChar* LECONFIG_ELEMENT_QUADRANTSIZE = (const xmlChar*)"QuadrantSize";
const xmlChar* LECONFIG_ELEMENT_RECOVERDETECTTIME = (const xmlChar*)"RecoverDetectTime";
const xmlChar* LECONFIG_ELEMENT_SPINRATE = (const xmlChar*)"SpinRate";
const xmlChar* LECONFIG_ELEMENT_STOMPIMPACTTHRESHOLD = (const xmlChar*)"StompImpactThreshold";
const xmlChar* LECONFIG_ELEMENT_STOMPRISETHRESHOLD = (const xmlChar*)"StompRiseThreshold";
const xmlChar* LECONFIG_ELEMENT_STOMPTIMEOUT = (const xmlChar*)"StompTimeout";

const xmlChar* LECONFIG_ELEMENT_BRIGHTNESS = (const xmlChar*)"Brightness";
const xmlChar* LECONFIG_ELEMENT_BLUE = (const xmlChar*)"Blue";
const xmlChar* LECONFIG_ELEMENT_GREEN = (const xmlChar*)"Green";
const xmlChar* LECONFIG_ELEMENT_RED = (const xmlChar*)"Red";

const xmlChar* LECONFIG_ATTR_TYPE = (const xmlChar*)"type";

const xmlChar* EFFECT_TYPE_FALL = (const xmlChar*)"LightEffectFall";
const xmlChar* EFFECT_TYPE_STOMP = (const xmlChar*)"LightEffectStomp";

const xmlChar* PALETTE_TYPE_SOLID = (const xmlChar*)"LightPaletteSolid";
const xmlChar* PALETTE_TYPE_WHEEL = (const xmlChar*)"LightPaletteWheel";
const xmlChar* PALETTE_TYPE_COMPASS = (const xmlChar*)"LightPaletteCompass";
const xmlChar* PALETTE_TYPE_DJ = (const xmlChar*)"LightPaletteDJ";


int32_t process_led_color_element(xmlNode *effect_node, dot_color* out_color)
{
	int32_t rtn_status = 0;

	// Defalut the color to 'off' or 'black'
	dot_color temp_color;
	temp_color.word = 0x000000FF;

	xmlNode *next_node = effect_node->children;
	while(next_node != NULL)
	{
		if (next_node->type == XML_ELEMENT_NODE)
		{
			if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_BLUE) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				temp_color.breakout.blue = strtol((const char *)nval_str, &end_str, 10);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (process_led_color_element): error processing '" << next_node->name << "' component of color element. '" << nval_str << "'" << std::endl;
					rtn_status = -1;
				}
			}
			else if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_GREEN) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				temp_color.breakout.green = strtol((const char *)nval_str, &end_str, 10);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (process_led_color_element): error processing '" << next_node->name << "' component of color element. '" << nval_str << "'" << std::endl;
					rtn_status = -1;
				}
			}
			else if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_RED) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				temp_color.breakout.red = strtol((const char *)nval_str, &end_str, 10);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (process_led_color_element): error processing '" << next_node->name << "' component of color element. '" << nval_str << "'" << std::endl;
					rtn_status = -1;
				}
			}
			else if (xmlStrcmp((const unsigned char*)next_node->name, (const unsigned char*)LECONFIG_ELEMENT_BRIGHTNESS) == 0)
			{
				char* nval_str = (char*)next_node->children->content;
				char* end_str = nval_str;
				float pct_brightness = strtof((const char *)nval_str, &end_str);
				temp_color.breakout.brightness = (uint8_t)(pct_brightness * 0x1F);
				if (end_str <= nval_str)
				{
					std::cerr << "Error (process_led_color_element): error processing '" << next_node->name << "' component of color element. '" << nval_str << "'" << std::endl;
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

	if (rtn_status == 0)
	{
		out_color->word = temp_color.word;

		std::cout << "process_led_color_element: 'dot_color' successfully loaded." << std::endl;
	}

	return rtn_status;
}
