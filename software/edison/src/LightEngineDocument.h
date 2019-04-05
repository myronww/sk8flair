
#ifndef LIGHT_ENGINE_DOCUMENT_INCLUDED_H
#define LIGHT_ENGINE_DOCUMENT_INCLUDED_H


#include <libxml/xmlstring.h>
#include <libxml/tree.h>

#include "DotStar.h"

extern const xmlChar* LECONFIG_ELEMENT_EFFECT;
extern const xmlChar* LECONFIG_ELEMENT_EFFECTCHAIN;
extern const xmlChar* LECONFIG_ELEMENT_FLAIRENGINE;
extern const xmlChar* LECONFIG_ELEMENT_MOTION;
extern const xmlChar* LECONFIG_ELEMENT_PALETTE;

extern const xmlChar* LECONFIG_ELEMENT_ACTIVECOLOR;
extern const xmlChar* LECONFIG_ELEMENT_FALLDETECTTIME;
extern const xmlChar* LECONFIG_ELEMENT_DURATION;
extern const xmlChar* LECONFIG_ELEMENT_FALLTHRESHOLD;
extern const xmlChar* LECONFIG_ELEMENT_IDLECOLOR;
extern const xmlChar* LECONFIG_ELEMENT_QUADRANTSIZE;
extern const xmlChar* LECONFIG_ELEMENT_RECOVERDETECTTIME;
extern const xmlChar* LECONFIG_ELEMENT_SPINRATE;
extern const xmlChar* LECONFIG_ELEMENT_STOMPIMPACTTHRESHOLD;
extern const xmlChar* LECONFIG_ELEMENT_STOMPRISETHRESHOLD;
extern const xmlChar* LECONFIG_ELEMENT_STOMPTIMEOUT;

extern const xmlChar* LECONFIG_ELEMENT_BRIGHTNESS;
extern const xmlChar* LECONFIG_ELEMENT_BLUE;
extern const xmlChar* LECONFIG_ELEMENT_GREEN;
extern const xmlChar* LECONFIG_ELEMENT_RED;

extern const xmlChar* LECONFIG_ATTR_TYPE;

extern const xmlChar* EFFECT_TYPE_FALL;
extern const xmlChar* EFFECT_TYPE_STOMP;

extern const xmlChar* PALETTE_TYPE_SOLID;
extern const xmlChar* PALETTE_TYPE_WHEEL;
extern const xmlChar* PALETTE_TYPE_COMPASS;
extern const xmlChar* PALETTE_TYPE_DJ;

int32_t process_led_color_element(xmlNode *effect_node, dot_color* out_color);

#endif //LIGHT_ENGINE_DOCUMENT_INCLUDED_H
