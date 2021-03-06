
#ifndef FLAIR_INCLUDED_H
#define FLAIR_INCLUDED_H

#define FLAIR_MULTICAST_GROUP "224.0.0.241"

#define FLAIR_MULTICAST_DISCOVERY_PORT        240 //0xF0    DISCOVERY
#define FLAIR_MULTICAST_DATACHANNEL_A_PORT    241 //0xF1    DATACHANNEL A
#define FLAIR_MULTICAST_DATACHANNEL_B_PORT    242 //0xF2    DATACHANNEL B
#define FLAIR_MULTICAST_DATACHANNEL_C_PORT    243 //0xF3    DATACHANNEL C
#define FLAIR_MULTICAST_DATACHANNEL_D_PORT    244 //0xF4    DATACHANNEL D
#define FLAIR_MULTICAST_DJCAST_PORT           245 //0xF5    DJCAST

#define FLAIR_SAMPLEDATA_TYPE_IMU    1

typedef struct _flair_data_sample {
	uint32_t payload_size;
	uint32_t payload_type;
	char payload[];
} flair_data_sample;

#endif //FLAIR_INCLUDED_H
