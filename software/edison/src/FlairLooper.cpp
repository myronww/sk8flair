
#include "FlairLooper.h"

#include <iostream>
#include <stdint.h>
#include <unistd.h>

#include <libxml/parser.h>
#include <libxml/tree.h>

#include "FlairEngine.h"

FlairLooper::FlairLooper(): m_ThreadState(THREAD_STATE_INITIAL), m_ThreadId(0), m_FlairEngine(NULL)
{

}

int
FlairLooper::join()
{
	return pthread_join(m_ThreadId, NULL);
}

void
FlairLooper::set_FlairEngine(FlairEngine* engine)
{
	m_FlairEngine = engine;
}

void
FlairLooper::start()
{
	m_ThreadState = THREAD_STATE_RUNNING;
	pthread_create(&m_ThreadId, NULL, sk8flair_render_entry, (void*)this);
}

void
FlairLooper::stop()
{
	m_ThreadState = THREAD_STATE_STOPPING;
}

void*
FlairLooper::sk8flair_render_entry(void* arg)
{
	FlairLooper* inst_ptr = (FlairLooper*)arg;

	timespec sleep_time;

	// Refresh the LEDs every 5ms
	sleep_time.tv_sec = 0;
	sleep_time.tv_nsec = 5000000;

	uint32_t base_ticker = 0;

	while(inst_ptr->m_ThreadState == THREAD_STATE_RUNNING)
	{
		if (inst_ptr->m_FlairEngine != NULL) {
			// The frequency of propagation for events is a harmonic of the
			// render frequency
			if (base_ticker == 0) {
				inst_ptr->m_FlairEngine->Propagate();
			}
			inst_ptr->m_FlairEngine->Render();
		}

		nanosleep(&sleep_time, NULL);
		base_ticker =  (base_ticker + 1) % PROPAGATION_HARMONIC;
	}

	inst_ptr->m_ThreadState = THREAD_STATE_TERMINATED;

	return NULL;
}
