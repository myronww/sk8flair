/*
 * DotStar.h
 *
 *  Created on: Jun 9, 2015
 *      Author: Myron W. Walker
 *  Copyright (c) 2015 Myron W Walker
 */

#ifndef DOT_STAR_INCLUDED_H
#define DOT_STAR_INCLUDED_H

#include "mraa.hpp"

#include <unistd.h>
#include <stdint.h>
#include <time.h>

#define PACK_COLOR_WORD(rval, gval, bval) \
	((uint32_t)rval << 24) | ((uint32_t)gval << 16) | ((uint32_t)bval << 8) | 0x000000FF

#define PACK_COLOR_WORD(rval, gval, bval) \
	((uint32_t)rval << 24) | ((uint32_t)gval << 16) | ((uint32_t)bval << 8) | 0x000000FF

#define SET_BRIGHTNESS(cword, percent) \
	cword = (cword & 0xFFFFFFE0) | (uint32_t)(0x0000001F * 1)

#define MAX_BRIGHTNESS 0x1F

#define DOTSTAR_DATA_PIN 35
#define DOTSTAR_CLOCK_PIN 26

typedef union {
	uint32_t word;
	struct {
		uint8_t red;
		uint8_t green;
		uint8_t blue;
		uint8_t header;
	} comp;
	struct {
		uint8_t red;
		uint8_t green;
		uint8_t blue;
		uint8_t start : 3;
		uint8_t brightness : 5;
	} breakout;
} dot_color;

#define SET_LED_HEADER (lc) \
	(uint32_t)lc |= 0xE0

#define SET_LED_BRIGHTNESS (lc, bval) \
	((uint32_t)lc = ((uint32_t)lc & 0xF8) | bval)

class DotStar {
public:
	DotStar(int32_t data_pin=DOTSTAR_DATA_PIN, int32_t clock_pin=DOTSTAR_CLOCK_PIN);
	~DotStar();

	void RenderBuffer(uint8_t* buffer, uint32_t buffer_len);
private:
	mraa::Gpio* m_DataPin;
	mraa::Gpio* m_ClockPin;

	timespec dummytime;

	void write_buffer(uint8_t* buffer, uint32_t buffer_len);

	inline void clock_high();
	inline void clock_low();

	inline void counter_cycle();

	inline void write_bit(uint8_t);
	inline void write_frame_start();
	inline void write_frame_stop(uint32_t buffer_len);
};

class DotStarBuffer {
public:
	DotStarBuffer(int32_t led_count);
	~DotStarBuffer();

	void fast_fill_buffer(uint32_t fill_word);

	uint32_t word_len;
	uint32_t byte_len;
	union {
	    uint32_t* word_buffer;
	    uint8_t* byte_buffer;
	} buffer;
};

#endif //DOT_STAR_INCLUDED_H
