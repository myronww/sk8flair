
#ifndef UTILITIES_INCLUDED_H
#define UTILITIES_INCLUDED_H

#include <unistd.h>
#include <stdint.h>

#define TIME_MSEC_TO_USEC(tval) (tval * 1000)

uint64_t get_time_us();

#endif // UTILITIES_INCLUDED_H
