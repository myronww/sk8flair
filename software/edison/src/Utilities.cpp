
#include "Utilities.h"

#include <sys/time.h>

uint64_t get_time_us()
{
	timeval now_tv;
	gettimeofday(&now_tv, NULL);
	uint64_t time_us = (now_tv.tv_sec * 1000000) + now_tv.tv_usec;
	return time_us;
}
