
#ifndef FLAIRRENDER_INCLUDED_H
#define FLAIRRENDER_INCLUDED_H

#include <pthread.h>

#include "FlairEngine.h"
#include "ThreadState.h"

#define PROPAGATION_HARMONIC 2

class FlairLooper
{
public:
	FlairLooper();

	int join();
	void start();
	void stop();

	void set_FlairEngine(FlairEngine* engine);

private:
	ThreadState m_ThreadState;
	pthread_t m_ThreadId;
	FlairEngine* m_FlairEngine;

	static void* sk8flair_render_entry(void* arg);
};

#endif //FLAIRRENDER_INCLUDED_H
