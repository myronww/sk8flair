/*
 * DotStar.cpp
 *
 *  Created on: Jun 9, 2015
 *      Author: Myron W. Walker
 *  Copyright (c) 2015 Myron W Walker
 */

#include <iostream>

#include <time.h>

#include "DotStar.h"

#define NOOP (void)0


DotStar::DotStar(int32_t data_pin, int32_t clock_pin)
{
	m_DataPin = new mraa::Gpio(data_pin);
	if (m_DataPin == NULL) {
		std::cerr << "Can't create mraa::Gpio object for data_pin=" << data_pin << ", exiting" << std::endl;
		exit(MRAA_ERROR_UNSPECIFIED);
	}

	if (m_DataPin->dir(mraa::DIR_OUT) != MRAA_SUCCESS) {
		std::cerr << "Can't set data pin to output mode" << std::endl;
		exit(MRAA_ERROR_UNSPECIFIED);
	}

	m_ClockPin = new mraa::Gpio(clock_pin);
	if (m_ClockPin == NULL) {
		std::cerr << "Can't create mraa::Gpio object for clock_pin=" << clock_pin << ", exiting" << std::endl;
		exit(MRAA_ERROR_UNSPECIFIED);
	}

	if (m_ClockPin->dir(mraa::DIR_OUT) != MRAA_SUCCESS) {
		std::cerr << "Can't set clock pin to output mode" << std::endl;
		exit(MRAA_ERROR_UNSPECIFIED);
	}

	clock_low();
}

DotStar::~DotStar()
{
}

void
DotStar::RenderBuffer(uint8_t* buffer, uint32_t buffer_len)
{
	write_buffer(buffer, buffer_len);
};

void
DotStar::clock_high()
{
	m_ClockPin->write(1);
}

void
DotStar::clock_low()
{
	m_ClockPin->write(0);
}

void
DotStar::counter_cycle()
{
	// Get time has a very regular period.
	m_ClockPin->write(1);

	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);

	// Four get times is equivalent to appx one gpio call
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);

	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);

	m_ClockPin->write(0);
}

void
DotStar::write_bit(uint8_t out_val)
{
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);
	m_DataPin->write(out_val);
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &dummytime);
}

void
DotStar::write_buffer(uint8_t* buffer, uint32_t buffer_len)
{
	//Write out the start frame
	write_frame_start();

	//Write all the pixels out
	for (uint32_t i=0; i<buffer_len; i++)
	{
		register uint8_t out_val;
		register uint8_t next_byte = buffer[i];

		out_val = (next_byte & 0x80) ? 1: 0;
		write_bit(out_val);
		counter_cycle();

		out_val = (next_byte & 0x40) ? 1: 0;
		write_bit(out_val);
		counter_cycle();

		out_val = (next_byte & 0x20) ? 1: 0;
		write_bit(out_val);
		counter_cycle();

		out_val = (next_byte & 0x10) ? 1: 0;
		write_bit(out_val);
		counter_cycle();

		out_val = (next_byte & 0x08) ? 1: 0;
		write_bit(out_val);
		counter_cycle();

		out_val = (next_byte & 0x04) ? 1: 0;
		write_bit(out_val);
		counter_cycle();

		out_val = (next_byte & 0x02) ? 1: 0;
		write_bit(out_val);
		counter_cycle();

		out_val = (next_byte & 0x01) ? 1: 0;
		write_bit(out_val);
		counter_cycle();
	}

	//Write out the stop frame
	uint32_t clock_count = buffer_len >> 3;
	write_frame_stop(clock_count);
}

void
DotStar::write_frame_start()
{
	/*
		A minimum of 32 zeroes are required to initiate an update. Increasing the number of zeroes does not have any
		impact. The LED frame is identified by the first one bit following the start frame.

		The LED output color is updated immediately after the first valid LED frame. This is quite interesting, since
		it means that almost arbitrary update rates of the APA102 are possible. However, this may lead to a “staggered”
		update for longer strings, where the first LEDs in a string are updated earlier than the later ones. The best
		way to work around this is to use a sufficiently high SPI clock rate.
	 */
	for (int index = 0; index < 32; index++)
	{
		write_bit(0x00);
		counter_cycle();
	}
}

void
DotStar::write_frame_stop(uint32_t clock_count)
{
	/*
		As we have learned above, the only function of the “End frame” is to supply more clock pulses to the string until
		the data has permeated to the last LED. The number of clock pulses required is exactly half the total number of
		LEDs in the string. The recommended end frame length of 32 is only sufficient for strings up to 64 LEDs. This was
		first pointed out by Bernd in a comment. It should not matter, whether the end frame consists of ones or zeroes.
		Just don’t mix them.

		Furthermore, omitting the end frame will not mean that data from the update is discarded. Instead it will be loaded
		in to the PWM registers at the start of the next update.
	 */
	for (uint32_t index = 0; index < clock_count; index++)
	{
		write_bit(0x00);
		counter_cycle();
	}
}


DotStarBuffer::DotStarBuffer(int32_t led_count)
{
	// The strip expects the colors to be sent in BGR order.  They are sent in a buffer
	// with as a set of 4 byts per pixel ( 0xFF/B/G/R )
	byte_len = led_count * 4;
	word_len = led_count;
	buffer.word_buffer = new uint32_t[led_count];
}

DotStarBuffer::~DotStarBuffer()
{
	delete[] buffer.word_buffer;
}

void
DotStarBuffer::fast_fill_buffer(uint32_t fill_word)
{
	for (uint32_t windex=0; windex < word_len; windex++)
	{
		buffer.word_buffer[windex] = fill_word;
	}
}


