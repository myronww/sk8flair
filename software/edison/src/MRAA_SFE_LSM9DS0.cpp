/******************************************************************************
SFE_LSM9DS0.cpp
SFE_LSM9DS0 Library Source File
Jim Lindblom @ SparkFun Electronics
Original Creation Date: February 14, 2014 (Happy Valentines Day!)
https://github.com/sparkfun/LSM9DS0_Breakout

This file implements all functions of the LSM9DS0 class. Functions here range
from higher level stuff, like reading/writing LSM9DS0 registers to low-level,
hardware reads and writes. Both SPI and I2C handler functions can be found
towards the bottom of this file.

Development environment specifics:
	IDE: Arduino 1.0.5
	Hardware Platform: Arduino Pro 3.3V/8MHz
	LSM9DS0 Breakout Version: 1.0

This code is beerware; if you see me (or any other SparkFun employee) at the
local, and you've found our code helpful, please buy us a round!

Distributed as-is; no warranty is given.
******************************************************************************/

#include <iostream>
#include <stdint.h>
#include <unistd.h>

#include "MRAA_SFE_LSM9DS0.h"

#define I2C_BUS 1
#define SPI_BUS 0

#define X_AXIS  0
#define Y_AXIS  1
#define Z_AXIS  2

LSM9DS0::LSM9DS0(interface_mode interface, uint8_t gAddr, uint8_t xmAddr):
    gx(0), gy(0), gz(0), ax(0), ay(0), az(0), mx(0), my(0), mz(0), temperature(0),
	acc_center_bias{0, 0, 0}, gyro_center_bias{0, 0, 0}, mag_center_bias{0, 0, 0},
	mag_scale_bias{1.0, 1.0, 1.0}
{
	// interfaceMode will keep track of whether we're using SPI or I2C:
	interfaceMode = interface;

	// xmAddress and gAddress will store the 7-bit I2C address, if using I2C.
	// If we're using SPI, these variables store the chip-select pins.
	xmAddress = xmAddr;
	gAddress = gAddr;
}

uint16_t LSM9DS0::begin(gyro_scale gScl, accel_scale aScl, mag_scale mScl,
						gyro_odr gODR, accel_odr aODR, mag_odr mODR)
{
	// Store the given scales in class variables. These scale variables
	// are used throughout to calculate the actual g's, DPS,and Gs's.
	gScale = gScl;
	aScale = aScl;
	mScale = mScl;

	// Once we have the scale values, we can calculate the resolution
	// of each sensor. That's what these functions are for. One for each sensor
	calcgRes(); // Calculate DPS / ADC tick, stored in gRes variable
	calcmRes(); // Calculate Gs / ADC tick, stored in mRes variable
	calcaRes(); // Calculate g / ADC tick, stored in aRes variable

	// Now, initialize our hardware interface.s
	if (interfaceMode == MODE_I2C)	// If we're using I2C
		initI2C();					// Initialize I2C
	else if (interfaceMode == MODE_SPI) 	// else, if we're using SPI
		initSPI();							// Initialize SPI

	// To verify communication, we can read from the WHO_AM_I register of
	// each device. Store those in a variable so we can return them.
	uint8_t gTest = gReadByte(WHO_AM_I_G);		// Read the gyro WHO_AM_I
	uint8_t xmTest = xmReadByte(WHO_AM_I_XM);	// Read the accel/mag WHO_AM_I

	// Gyro initialization stuff:
	initGyro();	// This will "turn on" the gyro. Setting up interrupts, etc.
	setGyroODR(gODR); // Set the gyro output data rate and bandwidth.
	setGyroScale(gScale); // Set the gyro range

	// Accelerometer initialization stuff:
	initAccel(); // "Turn on" all axes of the accel. Set up interrupts, etc.
	setAccelODR(aODR); // Set the accel data rate.
	setAccelScale(aScale); // Set the accel range.

	// Magnetometer initialization stuff:
	initMag(); // "Turn on" all axes of the mag. Set up interrupts, etc.
	setMagODR(mODR); // Set the magnetometer output data rate.
	setMagScale(mScale); // Set the magnetometer's range.

	// Once everything is initialized, return the WHO_AM_I registers we read:
	return (xmTest << 8) | gTest;
}

int32_t LSM9DS0::calibrate_axis(uint64_t time_ms, uint64_t period_ms, int16_t pos_center, int16_t pos_lower, int16_t pos_upper,
			int16_t* a_axis, int16_t* g_axis, int16_t* m_axis, int16_t* acc_apa, int16_t* gyro_apa, int16_t* mag_apa, int16_t* avg_rad_axis)
{
	int32_t result = -1;
	uint64_t sample_period = period_ms * 1000;

	uint32_t sample_count = time_ms / sample_period;

	int16_t* a_buff = new int16_t[sample_count];
	int16_t* g_buff = new int16_t[sample_count];
	int16_t* m_buff = new int16_t[sample_count];

	// Perform the motion sampling for the specified period of time
	for(uint32_t sindex = 0; sindex < sample_count; sindex++)
	{
		readMotion();

		a_buff[sindex] = this->ax;
		g_buff[sindex] = this->gx;
		m_buff[sindex] = this->mx;

		usleep(sample_period);
	}

	// Find the min and max magnetometer reading and check to make sure the
	// sample data satisfies the minimum and maximum motion positions
	// for the axis being calibrated
	int16_t min_acc = pos_center;
	int16_t max_acc = pos_center;

	for(uint32_t sindex = 0; sindex < sample_count; sindex++)
	{
		int16_t next_acc = a_buff[sindex];

		if (next_acc < min_acc)
			min_acc = next_acc;
		if (next_acc > max_acc)
			max_acc = next_acc;
	}

	int16_t min_gyro = 0;
	int16_t max_gyro = 0;

	for(uint32_t sindex = 0; sindex < sample_count; sindex++)
	{
		int16_t next_gyro = g_buff[sindex];

		if (next_gyro < min_gyro)
			min_gyro = next_gyro;
		if (next_gyro > min_gyro)
			min_gyro = next_gyro;
	}

	int16_t min_mag = pos_center;
	int16_t max_mag = pos_center;

	for(uint32_t sindex = 0; sindex < sample_count; sindex++)
	{
		int16_t next_mag = m_buff[sindex];

		if (next_mag < min_mag)
			min_mag = next_mag;
		if (next_mag > max_mag)
			max_mag = next_mag;
	}

	//Make sure the min value and max values are less than and greater than the center position
	if ((min_mag < pos_center) && (max_mag > pos_center))
	{
		int16_t zero_adj = pos_center * -1;

		int16_t pos_lower_adjz = pos_lower + zero_adj;
		int16_t pos_upper_adjz = pos_upper + zero_adj;

		int16_t min_mag_adjz = min_mag + zero_adj;
		int16_t max_mag_adjz = max_mag + zero_adj;

		// If the min and max are ok for our bounds then continue with the calibration
		if ((min_mag_adjz < pos_lower_adjz) && (max_mag_adjz > pos_upper_adjz))
		{
			// Compute the hard error adjustments to use for centering the axis
			*acc_apa = (int16_t)((min_acc + max_acc) / 2);
			*gyro_apa = (int16_t)((min_gyro + max_gyro) / 2);
			*mag_apa = (int16_t)((min_mag_adjz + max_mag_adjz) / 2);

			// Convert zero adjusted min and max to absolute values
			int16_t min_mag_abs = min_mag_adjz > 0 ? min_mag_adjz : min_mag_adjz * -1;
			int16_t max_mag_abs = max_mag_adjz > 0 ? max_mag_adjz : max_mag_adjz * -1;

			// Set the average radius for this axis, to use to compute soft error
			// scale adjustments. This computation uses the absolute value of
			// our min an maxes because we want average radias
			*avg_rad_axis = (int16_t)((min_mag_abs + max_mag_abs) / 2);
		}
		else {
			result = -2;
		}
	}

	delete[] a_buff;
	delete[] g_buff;
	delete[] m_buff;

	return result;
}

void LSM9DS0::calibrate_finalize()
{
	float mag_avg_rad = (float)(mag_avg_rad_axis[0] + mag_avg_rad_axis[1] + mag_avg_rad_axis[2]) / 3;

	for (int aindex = 0; aindex < 3; aindex++)
	{
		acc_center_bias[aindex] = acc_avg_pos_axis[aindex];
		gyro_center_bias[aindex] = gyro_avg_pos_axis[aindex];
		mag_center_bias[aindex] = mag_avg_pos_axis[aindex];
		mag_scale_bias[aindex] = mag_avg_rad / mag_avg_rad_axis[aindex];
	}
}

int32_t LSM9DS0::calibrate_x(uint64_t time_ms, uint64_t period_ms, int16_t pos_center, int16_t pos_lower, int16_t pos_upper)
{
	int16_t* a_axis = &this->ax;
	int16_t* g_axis = &this->gx;
	int16_t* m_axis = &this->mx;
	int16_t* acc_apa = &this->acc_avg_pos_axis[X_AXIS];
	int16_t* gyro_apa = &this->gyro_avg_pos_axis[X_AXIS];
	int16_t* mag_apa = &this->mag_avg_pos_axis[X_AXIS];
	int16_t* avg_rad_axis = &this->mag_avg_rad_axis[X_AXIS];

	int32_t result = calibrate_axis(time_ms, period_ms, pos_center, pos_lower, pos_upper, a_axis, g_axis, m_axis, acc_apa, gyro_apa, mag_apa, avg_rad_axis);

	return result;
}

int32_t LSM9DS0::calibrate_y(uint64_t time_ms, uint64_t period_ms, int16_t pos_center, int16_t pos_lower, int16_t pos_upper)
{
	int16_t* a_axis = &this->ay;
	int16_t* g_axis = &this->gy;
	int16_t* m_axis = &this->my;
	int16_t* acc_apa = &this->acc_avg_pos_axis[Y_AXIS];
	int16_t* gyro_apa = &this->gyro_avg_pos_axis[X_AXIS];
	int16_t* mag_apa = &this->mag_avg_pos_axis[X_AXIS];
	int16_t* avg_rad_axis = &this->mag_avg_rad_axis[X_AXIS];

	int32_t result = calibrate_axis(time_ms, period_ms, pos_center, pos_lower, pos_upper, a_axis, g_axis, m_axis, acc_apa, gyro_apa, mag_apa, avg_rad_axis);

	return result;
}

int32_t LSM9DS0::calibrate_z(uint64_t time_ms, uint64_t period_ms, int16_t pos_center, int16_t pos_lower, int16_t pos_upper)
{
	int16_t* a_axis = &this->az;
	int16_t* g_axis = &this->gz;
	int16_t* m_axis = &this->mz;
	int16_t* acc_apa = &this->acc_avg_pos_axis[Z_AXIS];
	int16_t* gyro_apa = &this->gyro_avg_pos_axis[Z_AXIS];
	int16_t* mag_apa = &this->mag_avg_pos_axis[Z_AXIS];
	int16_t* avg_rad_axis = &this->mag_avg_rad_axis[Z_AXIS];

	int32_t result = calibrate_axis(time_ms, period_ms, pos_center, pos_lower, pos_upper, a_axis, g_axis, m_axis, acc_apa, gyro_apa, mag_apa, avg_rad_axis);

	return result;
}

void LSM9DS0::initGyro()
{
	/* CTRL_REG1_G sets output data rate, bandwidth, power-down and enables
	Bits[7:0]: DR1 DR0 BW1 BW0 PD Zen Xen Yen
	DR[1:0] - Output data rate selection
		00=95Hz, 01=190Hz, 10=380Hz, 11=760Hz
	BW[1:0] - Bandwidth selection (sets cutoff frequency)
		 Value depends on ODR. See datasheet table 21.
	PD - Power down enable (0=power down mode, 1=normal or sleep mode)
	Zen, Xen, Yen - Axis enable (o=disabled, 1=enabled)	*/
	gWriteByte(CTRL_REG1_G, 0x0F); // Normal mode, enable all axes

	/* CTRL_REG2_G sets up the HPF
	Bits[7:0]: 0 0 HPM1 HPM0 HPCF3 HPCF2 HPCF1 HPCF0
	HPM[1:0] - High pass filter mode selection
		00=normal (reset reading HP_RESET_FILTER, 01=ref signal for filtering,
		10=normal, 11=autoreset on interrupt
	HPCF[3:0] - High pass filter cutoff frequency
		Value depends on data rate. See datasheet table 26.
	*/
	gWriteByte(CTRL_REG2_G, 0x00); // Normal mode, high cutoff frequency

	/* CTRL_REG3_G sets up interrupt and DRDY_G pins
	Bits[7:0]: I1_IINT1 I1_BOOT H_LACTIVE PP_OD I2_DRDY I2_WTM I2_ORUN I2_EMPTY
	I1_INT1 - Interrupt enable on INT_G pin (0=disable, 1=enable)
	I1_BOOT - Boot status available on INT_G (0=disable, 1=enable)
	H_LACTIVE - Interrupt active configuration on INT_G (0:high, 1:low)
	PP_OD - Push-pull/open-drain (0=push-pull, 1=open-drain)
	I2_DRDY - Data ready on DRDY_G (0=disable, 1=enable)
	I2_WTM - FIFO watermark interrupt on DRDY_G (0=disable 1=enable)
	I2_ORUN - FIFO overrun interrupt on DRDY_G (0=disable 1=enable)
	I2_EMPTY - FIFO empty interrupt on DRDY_G (0=disable 1=enable) */
	// Int1 enabled (pp, active low), data read on DRDY_G:
	gWriteByte(CTRL_REG3_G, 0x88);

	/* CTRL_REG4_G sets the scale, update mode
	Bits[7:0] - BDU BLE FS1 FS0 - ST1 ST0 SIM
	BDU - Block data update (0=continuous, 1=output not updated until read
	BLE - Big/little endian (0=data LSB @ lower address, 1=LSB @ higher add)
	FS[1:0] - Full-scale selection
		00=245dps, 01=500dps, 10=2000dps, 11=2000dps
	ST[1:0] - Self-test enable
		00=disabled, 01=st 0 (x+, y-, z-), 10=undefined, 11=st 1 (x-, y+, z+)
	SIM - SPI serial interface mode select
		0=4 wire, 1=3 wire */
	gWriteByte(CTRL_REG4_G, 0x00); // Set scale to 245 dps

	/* CTRL_REG5_G sets up the FIFO, HPF, and INT1
	Bits[7:0] - BOOT FIFO_EN - HPen INT1_Sel1 INT1_Sel0 Out_Sel1 Out_Sel0
	BOOT - Reboot memory content (0=normal, 1=reboot)
	FIFO_EN - FIFO enable (0=disable, 1=enable)
	HPen - HPF enable (0=disable, 1=enable)
	INT1_Sel[1:0] - Int 1 selection configuration
	Out_Sel[1:0] - Out selection configuration */
	gWriteByte(CTRL_REG5_G, 0x00);

	// Temporary !!! For testing !!! Remove !!! Or make useful !!!
	configGyroInt(0x2A, 0, 0, 0, 0); // Trigger interrupt when above 0 DPS...
}

void LSM9DS0::initAccel()
{
	/* CTRL_REG0_XM (0x1F) (Default value: 0x00)
	Bits (7-0): BOOT FIFO_EN WTM_EN 0 0 HP_CLICK HPIS1 HPIS2
	BOOT - Reboot memory content (0: normal, 1: reboot)
	FIFO_EN - Fifo enable (0: disable, 1: enable)
	WTM_EN - FIFO watermark enable (0: disable, 1: enable)
	HP_CLICK - HPF enabled for click (0: filter bypassed, 1: enabled)
	HPIS1 - HPF enabled for interrupt generator 1 (0: bypassed, 1: enabled)
	HPIS2 - HPF enabled for interrupt generator 2 (0: bypassed, 1 enabled)   */
	xmWriteByte(CTRL_REG0_XM, 0x00);

	/* CTRL_REG1_XM (0x20) (Default value: 0x07)
	Bits (7-0): AODR3 AODR2 AODR1 AODR0 BDU AZEN AYEN AXEN
	AODR[3:0] - select the acceleration data rate:
		0000=power down, 0001=3.125Hz, 0010=6.25Hz, 0011=12.5Hz,
		0100=25Hz, 0101=50Hz, 0110=100Hz, 0111=200Hz, 1000=400Hz,
		1001=800Hz, 1010=1600Hz, (remaining combinations undefined).
	BDU - block data update for accel AND mag
		0: Continuous update
		1: Output registers aren't updated until MSB and LSB have been read.
	AZEN, AYEN, and AXEN - Acceleration x/y/z-axis enabled.
		0: Axis disabled, 1: Axis enabled									 */
	xmWriteByte(CTRL_REG1_XM, 0x57); // 100Hz data rate, x/y/z all enabled

	//Serial.println(xmReadByte(CTRL_REG1_XM));
	/* CTRL_REG2_XM (0x21) (Default value: 0x00)
	Bits (7-0): ABW1 ABW0 AFS2 AFS1 AFS0 AST1 AST0 SIM
	ABW[1:0] - Accelerometer anti-alias filter bandwidth
		00=773Hz, 01=194Hz, 10=362Hz, 11=50Hz
	AFS[2:0] - Accel full-scale selection
		000=+/-2g, 001=+/-4g, 010=+/-6g, 011=+/-8g, 100=+/-16g
	AST[1:0] - Accel self-test enable
		00=normal (no self-test), 01=positive st, 10=negative st, 11=not allowed
	SIM - SPI mode selection
		0=4-wire, 1=3-wire													 */
	xmWriteByte(CTRL_REG2_XM, 0x00); // Set scale to 2g

	/* CTRL_REG3_XM is used to set interrupt generators on INT1_XM
	Bits (7-0): P1_BOOT P1_TAP P1_INT1 P1_INT2 P1_INTM P1_DRDYA P1_DRDYM P1_EMPTY
	*/
	// Accelerometer data ready on INT1_XM (0x04)
	xmWriteByte(CTRL_REG3_XM, 0x04);
}

void LSM9DS0::initMag()
{
	/* CTRL_REG5_XM enables temp sensor, sets mag resolution and data rate
	Bits (7-0): TEMP_EN M_RES1 M_RES0 M_ODR2 M_ODR1 M_ODR0 LIR2 LIR1
	TEMP_EN - Enable temperature sensor (0=disabled, 1=enabled)
	M_RES[1:0] - Magnetometer resolution select (0=low, 3=high)
	M_ODR[2:0] - Magnetometer data rate select
		000=3.125Hz, 001=6.25Hz, 010=12.5Hz, 011=25Hz, 100=50Hz, 101=100Hz
	LIR2 - Latch interrupt request on INT2_SRC (cleared by reading INT2_SRC)
		0=interrupt request not latched, 1=interrupt request latched
	LIR1 - Latch interrupt request on INT1_SRC (cleared by readging INT1_SRC)
		0=irq not latched, 1=irq latched 									 */
	xmWriteByte(CTRL_REG5_XM, 0x94); // Mag data rate - 100 Hz, enable temperature sensor

	/* CTRL_REG6_XM sets the magnetometer full-scale
	Bits (7-0): 0 MFS1 MFS0 0 0 0 0 0
	MFS[1:0] - Magnetic full-scale selection
	00:+/-2Gauss, 01:+/-4Gs, 10:+/-8Gs, 11:+/-12Gs							 */
	xmWriteByte(CTRL_REG6_XM, 0x00); // Mag scale to +/- 2GS

	/* CTRL_REG7_XM sets magnetic sensor mode, low power mode, and filters
	AHPM1 AHPM0 AFDS 0 0 MLP MD1 MD0
	AHPM[1:0] - HPF mode selection
		00=normal (resets reference registers), 01=reference signal for filtering,
		10=normal, 11=autoreset on interrupt event
	AFDS - Filtered acceleration data selection
		0=internal filter bypassed, 1=data from internal filter sent to FIFO
	MLP - Magnetic data low-power mode
		0=data rate is set by M_ODR bits in CTRL_REG5
		1=data rate is set to 3.125Hz
	MD[1:0] - Magnetic sensor mode selection (default 10)
		00=continuous-conversion, 01=single-conversion, 10 and 11=power-down */
	xmWriteByte(CTRL_REG7_XM, 0x00); // Continuous conversion mode

	/* CTRL_REG4_XM is used to set interrupt generators on INT2_XM
	Bits (7-0): P2_TAP P2_INT1 P2_INT2 P2_INTM P2_DRDYA P2_DRDYM P2_Overrun P2_WTM
	*/
	xmWriteByte(CTRL_REG4_XM, 0x04); // Magnetometer data ready on INT2_XM (0x08)

	/* INT_CTRL_REG_M to set push-pull/open drain, and active-low/high
	Bits[7:0] - XMIEN YMIEN ZMIEN PP_OD IEA IEL 4D MIEN
	XMIEN, YMIEN, ZMIEN - Enable interrupt recognition on axis for mag data
	PP_OD - Push-pull/open-drain interrupt configuration (0=push-pull, 1=od)
	IEA - Interrupt polarity for accel and magneto
		0=active-low, 1=active-high
	IEL - Latch interrupt request for accel and magneto
		0=irq not latched, 1=irq latched
	4D - 4D enable. 4D detection is enabled when 6D bit in INT_GEN1_REG is set
	MIEN - Enable interrupt generation for magnetic data
		0=disable, 1=enable) */
	xmWriteByte(INT_CTRL_REG_M, 0x09); // Enable interrupts for mag, active-low, push-pull
}

// This is a function that uses the FIFO to accumulate sample of accelerometer and gyro data, average
// them, scales them to  gs and deg/s, respectively, and then passes the biases to the main sketch
// for subtraction from all subsequent data. There are no gyro and accelerometer bias registers to store
// the data as there are in the ADXL345, a precursor to the LSM9DS0, or the MPU-9150, so we have to
// subtract the biases ourselves. This results in a more accurate measurement in general and can
// remove errors due to imprecise or varying initial placement. Calibration of sensor data in this manner
// is good practice.
/*
void LSM9DS0::calLSM9DS0(float * gbias, float * abias)
{
  uint8_t data[6] = {0, 0, 0, 0, 0, 0};
  int16_t gyro_bias[3] = {0, 0, 0}, accel_bias[3] = {0, 0, 0};
  int samples, ii;

  // First get gyro bias
  uint8_t c = gReadByte(CTRL_REG5_G);
  gWriteByte(CTRL_REG5_G, c | 0x40);         // Enable gyro FIFO
  usleep(20 * 1000);                                 // Wait for change to take effect
  gWriteByte(FIFO_CTRL_REG_G, 0x20 | 0x1F);  // Enable gyro FIFO stream mode and set watermark at 32 samples
  usleep(1000 * 1000);  // delay 1000 milliseconds to collect FIFO samples

  samples = (gReadByte(FIFO_SRC_REG_G) & 0x1F); // Read number of stored samples

  for(ii = 0; ii < samples ; ii++) {            // Read the gyro data stored in the FIFO
    gReadBytes(OUT_X_L_G,  &data[0], 6);
    gyro_bias[0] += (((int16_t)data[1] << 8) | data[0]);
    gyro_bias[1] += (((int16_t)data[3] << 8) | data[2]);
    gyro_bias[2] += (((int16_t)data[5] << 8) | data[4]);
  }

  gyro_bias[0] /= samples; // average the data
  gyro_bias[1] /= samples;
  gyro_bias[2] /= samples;

  gbias[0] = (float)gyro_bias[0]*gRes;  // Properly scale the data to get deg/s
  gbias[1] = (float)gyro_bias[1]*gRes;
  gbias[2] = (float)gyro_bias[2]*gRes;

  c = gReadByte(CTRL_REG5_G);
  gWriteByte(CTRL_REG5_G, c & ~0x40);  // Disable gyro FIFO
  usleep(20 * 1000);
  gWriteByte(FIFO_CTRL_REG_G, 0x00);   // Enable gyro bypass mode


  //  Now get the accelerometer biases
  c = xmReadByte(CTRL_REG0_XM);
  xmWriteByte(CTRL_REG0_XM, c | 0x40);      // Enable accelerometer FIFO
  usleep(20 * 1000);                                // Wait for change to take effect
  xmWriteByte(FIFO_CTRL_REG, 0x20 | 0x1F);  // Enable accelerometer FIFO stream mode and set watermark at 32 samples
  usleep(1000 * 1000);  // delay 1000 milliseconds to collect FIFO samples

  samples = (xmReadByte(FIFO_SRC_REG) & 0x1F); // Read number of stored accelerometer samples

   for(ii = 0; ii < samples ; ii++) {          // Read the accelerometer data stored in the FIFO
    xmReadBytes(OUT_X_L_A, &data[0], 6);
    accel_bias[0] += (((int16_t)data[1] << 8) | data[0]);
    accel_bias[1] += (((int16_t)data[3] << 8) | data[2]);
    accel_bias[2] += (((int16_t)data[5] << 8) | data[4]) - (int16_t)(1./aRes); // Assumes sensor facing up!
  }

  accel_bias[0] /= samples; // average the data
  accel_bias[1] /= samples;
  accel_bias[2] /= samples;

  abias[0] = (float)accel_bias[0]*aRes; // Properly scale data to get gs
  abias[1] = (float)accel_bias[1]*aRes;
  abias[2] = (float)accel_bias[2]*aRes;

  c = xmReadByte(CTRL_REG0_XM);
  xmWriteByte(CTRL_REG0_XM, c & ~0x40);    // Disable accelerometer FIFO
  usleep(20 * 1000);
  xmWriteByte(FIFO_CTRL_REG, 0x00);       // Enable accelerometer bypass mode
}
*/

void LSM9DS0::readAccel()
{
	uint8_t temp[6]; // We'll read six bytes from the accelerometer into temp
	xmReadBytes(OUT_X_L_A, temp, 6); // Read 6 bytes, beginning at OUT_X_L_A
	ax = decode_as_int16(temp[1], temp[0]); // Store x-axis values into ax
	ay = decode_as_int16(temp[3], temp[2]); // Store y-axis values into ay
	az = decode_as_int16(temp[5], temp[4]); // Store z-axis values into az
}

void LSM9DS0::readMag()
{
	uint8_t temp[6]; // We'll read six bytes from the mag into temp
	xmReadBytes(OUT_X_L_M, temp, 6); // Read 6 bytes, beginning at OUT_X_L_M
	mx = decode_as_int16(temp[1], temp[0]); // Store x-axis values into mx
	my = decode_as_int16(temp[3], temp[2]); // Store y-axis values into my
	mz = decode_as_int16(temp[5], temp[4]); // Store z-axis values into mz
}

void LSM9DS0::readMotion(motion_event* event, bool adjust_output)
{
	uint8_t temp[6];

	xmReadBytes(OUT_X_L_A, temp, 6); // Read 6 bytes, beginning at OUT_X_L_A
	ax = decode_as_int16(temp[1], temp[0]);; // Store x-axis values into ax
	ay = decode_as_int16(temp[3], temp[2]);; // Store y-axis values into ay
	az = decode_as_int16(temp[5], temp[4]);; // Store z-axis values into az

	gReadBytes(OUT_X_L_G, temp, 6); // Read 6 bytes, beginning at OUT_X_L_G
	gx = decode_as_int16(temp[1], temp[0]); // Store x-axis values into gx
	gy = decode_as_int16(temp[3], temp[2]); // Store y-axis values into gy
	gz = decode_as_int16(temp[5], temp[4]); // Store z-axis values into gz

	xmReadBytes(OUT_X_L_M, temp, 6); // Read 6 bytes, beginning at OUT_X_L_M
	mx = decode_as_int16(temp[1], temp[0]); // Store x-axis values into mx
	my = decode_as_int16(temp[3], temp[2]); // Store y-axis values into my
	mz = decode_as_int16(temp[5], temp[4]); // Store z-axis values into mz

	if (event != NULL) {
		if(adjust_output) {
			// Remove center offset errors
		    event->ax = this->ax - this->acc_center_bias[X_AXIS];
		    event->ay = this->ay - this->acc_center_bias[Y_AXIS];
		    event->az = this->az - this->acc_center_bias[Z_AXIS];
		    event->gx = this->gx - this->gyro_center_bias[X_AXIS];
			event->gy = this->gy - this->gyro_center_bias[Y_AXIS];
			event->gz = this->gz - this->gyro_center_bias[Z_AXIS];
			event->mx = this->mx - this->mag_center_bias[X_AXIS];
			event->my = this->my - this->mag_center_bias[Y_AXIS];
			event->mz = this->mz - this->mag_center_bias[Z_AXIS];

			// Remove scaling errors or errors related to skew
			event->mx *= this->mag_scale_bias[X_AXIS];
			event->my *= this->mag_scale_bias[Y_AXIS];
			event->mz *= this->mag_scale_bias[Z_AXIS];
		}
		else {
			event->ax = this->ax;
			event->ay = this->ay;
			event->az = this->az;
			event->gx = this->gx;
			event->gy = this->gy;
			event->gz = this->gz;
			event->mx = this->mx;
			event->my = this->my;
			event->mz = this->mz;
		}
	}
}

void LSM9DS0::readTemp()
{
	uint8_t temp[2]; // We'll read two bytes from the temperature sensor into temp
	xmReadBytes(OUT_TEMP_L_XM, temp, 2); // Read 2 bytes, beginning at OUT_TEMP_L_M
	temperature = (((int16_t) temp[1] << 12) | temp[0] << 4 ) >> 4; // Temperature is a 12-bit signed integer
}

void LSM9DS0::readGyro()
{
	uint8_t temp[6]; // We'll read six bytes from the gyro into temp
	gReadBytes(OUT_X_L_G, temp, 6); // Read 6 bytes, beginning at OUT_X_L_G
	gx = decode_as_int16(temp[1], temp[0]); // Store x-axis values into gx
	gy = decode_as_int16(temp[3], temp[2]); // Store y-axis values into gy
	gz = decode_as_int16(temp[5], temp[4]); // Store z-axis values into gz
}

float LSM9DS0::calcGyro(int16_t gyro)
{
	// Return the gyro raw reading times our pre-calculated DPS / (ADC tick):
	return gRes * gyro;
}

float LSM9DS0::calcAccel(int16_t accel)
{
	// Return the accel raw reading times our pre-calculated g's / (ADC tick):
	return aRes * accel;
}

float LSM9DS0::calcMag(int16_t mag)
{
	// Return the mag raw reading times our pre-calculated Gs / (ADC tick):
	return mRes * mag;
}

void LSM9DS0::calcMotion(motion_event* event, f_motion_event* fevent)
{
	fevent->ax = aRes * event->ax;
	fevent->ay = aRes * event->ay;
	fevent->az = aRes * event->az;

	fevent->gx = gRes * event->gx;
	fevent->gy = gRes * event->gy;
	fevent->gz = gRes * event->gz;

	fevent->mx = gRes * event->mx;
	fevent->my = gRes * event->my;
	fevent->mz = gRes * event->mz;
}

void LSM9DS0::setGyroScale(gyro_scale gScl)
{
	// We need to preserve the other bytes in CTRL_REG4_G. So, first read it:
	uint8_t temp = gReadByte(CTRL_REG4_G);
	// Then mask out the gyro scale bits:
	temp &= 0xFF^(0x3 << 4);
	// Then shift in our new scale bits:
	temp |= gScl << 4;
	// And write the new register value back into CTRL_REG4_G:
	gWriteByte(CTRL_REG4_G, temp);

	// We've updated the sensor, but we also need to update our class variables
	// First update gScale:
	gScale = gScl;
	// Then calculate a new gRes, which relies on gScale being set correctly:
	calcgRes();
}

void LSM9DS0::setAccelScale(accel_scale aScl)
{
	// We need to preserve the other bytes in CTRL_REG2_XM. So, first read it:
	uint8_t temp = xmReadByte(CTRL_REG2_XM);
	// Then mask out the accel scale bits:
	temp &= 0xFF^(0x3 << 3);
	// Then shift in our new scale bits:
	temp |= aScl << 3;
	// And write the new register value back into CTRL_REG2_XM:
	xmWriteByte(CTRL_REG2_XM, temp);

	// We've updated the sensor, but we also need to update our class variables
	// First update aScale:
	aScale = aScl;
	// Then calculate a new aRes, which relies on aScale being set correctly:
	calcaRes();
}

void LSM9DS0::setMagScale(mag_scale mScl)
{
	// We need to preserve the other bytes in CTRL_REG6_XM. So, first read it:
	uint8_t temp = xmReadByte(CTRL_REG6_XM);
	// Then mask out the mag scale bits:
	temp &= 0xFF^(0x3 << 5);
	// Then shift in our new scale bits:
	temp |= mScl << 5;
	// And write the new register value back into CTRL_REG6_XM:
	xmWriteByte(CTRL_REG6_XM, temp);

	// We've updated the sensor, but we also need to update our class variables
	// First update mScale:
	mScale = mScl;
	// Then calculate a new mRes, which relies on mScale being set correctly:
	calcmRes();
}

void LSM9DS0::setGyroODR(gyro_odr gRate)
{
	// We need to preserve the other bytes in CTRL_REG1_G. So, first read it:
	uint8_t temp = gReadByte(CTRL_REG1_G);
	// Then mask out the gyro ODR bits:
	temp &= 0xFF^(0xF << 4);
	// Then shift in our new ODR bits:
	temp |= (gRate << 4);
	// And write the new register value back into CTRL_REG1_G:
	gWriteByte(CTRL_REG1_G, temp);
}
void LSM9DS0::setAccelODR(accel_odr aRate)
{
	// We need to preserve the other bytes in CTRL_REG1_XM. So, first read it:
	uint8_t temp = xmReadByte(CTRL_REG1_XM);
	// Then mask out the accel ODR bits:
	temp &= 0xFF^(0xF << 4);
	// Then shift in our new ODR bits:
	temp |= (aRate << 4);
	// And write the new register value back into CTRL_REG1_XM:
	xmWriteByte(CTRL_REG1_XM, temp);
}
void LSM9DS0::setAccelABW(accel_abw abwRate)
{
	// We need to preserve the other bytes in CTRL_REG2_XM. So, first read it:
	uint8_t temp = xmReadByte(CTRL_REG2_XM);
	// Then mask out the accel ABW bits:
	temp &= 0xFF^(0x3 << 7);
	// Then shift in our new ODR bits:
	temp |= (abwRate << 7);
	// And write the new register value back into CTRL_REG2_XM:
	xmWriteByte(CTRL_REG2_XM, temp);
}
void LSM9DS0::setMagODR(mag_odr mRate)
{
	// We need to preserve the other bytes in CTRL_REG5_XM. So, first read it:
	uint8_t temp = xmReadByte(CTRL_REG5_XM);
	// Then mask out the mag ODR bits:
	temp &= 0xFF^(0x7 << 2);
	// Then shift in our new ODR bits:
	temp |= (mRate << 2);
	// And write the new register value back into CTRL_REG5_XM:
	xmWriteByte(CTRL_REG5_XM, temp);
}

void LSM9DS0::configGyroInt(uint8_t int1Cfg, uint16_t int1ThsX, uint16_t int1ThsY, uint16_t int1ThsZ, uint8_t duration)
{
	gWriteByte(INT1_CFG_G, int1Cfg);
	gWriteByte(INT1_THS_XH_G, (int1ThsX & 0xFF00) >> 8);
	gWriteByte(INT1_THS_XL_G, (int1ThsX & 0xFF));
	gWriteByte(INT1_THS_YH_G, (int1ThsY & 0xFF00) >> 8);
	gWriteByte(INT1_THS_YL_G, (int1ThsY & 0xFF));
	gWriteByte(INT1_THS_ZH_G, (int1ThsZ & 0xFF00) >> 8);
	gWriteByte(INT1_THS_ZL_G, (int1ThsZ & 0xFF));
	if (duration)
		gWriteByte(INT1_DURATION_G, 0x80 | duration);
	else
		gWriteByte(INT1_DURATION_G, 0x00);
}

void LSM9DS0::calcgRes()
{
	// Possible gyro scales (and their register bit settings) are:
	// 245 DPS (00), 500 DPS (01), 2000 DPS (10). Here's a bit of an algorithm
	// to calculate DPS/(ADC tick) based on that 2-bit value:
	switch (gScale)
	{
	case G_SCALE_245DPS:
		gRes = 245.0 / 32768.0;
		break;
	case G_SCALE_500DPS:
		gRes = 500.0 / 32768.0;
		break;
	case G_SCALE_2000DPS:
		gRes = 2000.0 / 32768.0;
		break;
	}
}

void LSM9DS0::calcaRes()
{
	// Possible accelerometer scales (and their register bit settings) are:
	// 2 g (000), 4g (001), 6g (010) 8g (011), 16g (100). Here's a bit of an
	// algorithm to calculate g/(ADC tick) based on that 3-bit value:
	aRes = aScale == A_SCALE_16G ? 16.0 / 32768.0 :
		   (((float) aScale + 1.0) * 2.0) / 32768.0;
}

void LSM9DS0::calcmRes()
{
	// Possible magnetometer scales (and their register bit settings) are:
	// 2 Gs (00), 4 Gs (01), 8 Gs (10) 12 Gs (11). Here's a bit of an algorithm
	// to calculate Gs/(ADC tick) based on that 2-bit value:
	mRes = mScale == M_SCALE_2GS ? 2.0 / 32768.0 :
	       (float) (mScale << 2) / 32768.0;
}

void LSM9DS0::gWriteByte(uint8_t subAddress, uint8_t data)
{
	// Whether we're using I2C or SPI, write a byte using the
	// gyro-specific I2C address or SPI CS pin.
	if (interfaceMode == MODE_I2C)
		I2CwriteByte(gAddress, subAddress, data);
	else if (interfaceMode == MODE_SPI)
		SPIwriteByte(gAddress, subAddress, data);
	else {
	    std::cerr << "ERROR: Invalid interface mode=" << interfaceMode << std::endl;
	    exit(1);
	}
}

void LSM9DS0::xmWriteByte(uint8_t subAddress, uint8_t data)
{
	// Whether we're using I2C or SPI, write a byte using the
	// accelerometer-specific I2C address or SPI CS pin.
	if (interfaceMode == MODE_I2C)
		return I2CwriteByte(xmAddress, subAddress, data);
	else if (interfaceMode == MODE_SPI)
		return SPIwriteByte(xmAddress, subAddress, data);
	else {
	    std::cerr << "ERROR: Invalid interface mode=" << interfaceMode << std::endl;
	    exit(1);
	}
}

uint8_t LSM9DS0::gReadByte(uint8_t subAddress)
{
	// Whether we're using I2C or SPI, read a byte using the
	// gyro-specific I2C address or SPI CS pin.
	if (interfaceMode == MODE_I2C)
		return I2CreadByte(gAddress, subAddress);
	else if (interfaceMode == MODE_SPI)
		return SPIreadByte(gAddress, subAddress);
	else {
		std::cerr << "ERROR: Invalid interface mode=" << interfaceMode << std::endl;
	    exit(1);
	}
}

void LSM9DS0::gReadBytes(uint8_t subAddress, uint8_t * dest, uint8_t count)
{
	// Whether we're using I2C or SPI, read multiple bytes using the
	// gyro-specific I2C address or SPI CS pin.
	if (interfaceMode == MODE_I2C)
		I2CreadBytes(gAddress, subAddress, dest, count);
	else if (interfaceMode == MODE_SPI)
		SPIreadBytes(gAddress, subAddress, dest, count);
	else {
		std::cerr << "ERROR: Invalid interface mode=" << interfaceMode << std::endl;
		exit(1);
	}
}

uint8_t LSM9DS0::xmReadByte(uint8_t subAddress)
{
	// Whether we're using I2C or SPI, read a byte using the
	// accelerometer-specific I2C address or SPI CS pin.
	if (interfaceMode == MODE_I2C)
		return I2CreadByte(xmAddress, subAddress);
	else if (interfaceMode == MODE_SPI)
		return SPIreadByte(xmAddress, subAddress);
	else {
		std::cerr << "ERROR: Invalid interface mode=" << interfaceMode << std::endl;
		exit(1);
	}
}

void LSM9DS0::xmReadBytes(uint8_t subAddress, uint8_t * dest, uint8_t count)
{
	// Whether we're using I2C or SPI, read multiple bytes using the
	// accelerometer-specific I2C address or SPI CS pin.
	if (interfaceMode == MODE_I2C)
		I2CreadBytes(xmAddress, subAddress, dest, count);
	else if (interfaceMode == MODE_SPI)
		SPIreadBytes(xmAddress, subAddress, dest, count);
	else {
		std::cerr << "ERROR: Invalid interface mode=" << interfaceMode << std::endl;
		exit(1);
	}
}

void LSM9DS0::initSPI()
{
	mraa::Spi_Mode spimode = mraa::SPI_MODE1;

	wire.SPI = new mraa::Spi(0);

    //TODO: Add support for setting the frequency of the SPI communications
	//wire.SPI->frequency()
	wire.SPI->lsbmode(false);
	wire.SPI->mode(spimode);
}

void LSM9DS0::SPIwriteByte(uint8_t csPin, uint8_t subAddress, uint8_t data)
{
	uint8_t write_buffer[] = { (uint8_t)(subAddress & (uint8_t)0x3F), data };

	// If write, bit 0 (MSB) should be 0
	// If single write, bit 1 should be 0
	wire.SPI->transfer(write_buffer, NULL, 2);
}

uint8_t LSM9DS0::SPIreadByte(uint8_t csPin, uint8_t subAddress)
{
	uint8_t temp;
	// Use the multiple read function to read 1 byte.
	// Value is returned to `temp`.
	SPIreadBytes(csPin, subAddress, &temp, 1);
	return temp;
}

void LSM9DS0::SPIreadBytes(uint8_t csPin, uint8_t subAddress,
							uint8_t * dest, uint8_t count)
{
	// Initiate communication
	// To indicate a read, set bit 0 (msb) to 1
	// If we're reading multiple bytes, set bit 1 to 1
	// The remaining six bits are the address to be read
	uint8_t reg_address = (0x80 | (subAddress & 0x3F));
	if (count > 1)
		reg_address = (0xC0 | (subAddress & 0x3F));

	wire.SPI->transfer(&reg_address, dest, 1);
}

void LSM9DS0::initI2C()
{
	wire.I2C = new mraa::I2c(I2C_BUS);
}

// Wire.h read and write protocols
void LSM9DS0::I2CwriteByte(uint8_t address, uint8_t subAddress, uint8_t data)
{
	mraa::Result result = wire.I2C->address(address);
	if ( result == mraa::SUCCESS ) {
		result = wire.I2C->writeReg(subAddress, data);
		if ( result != mraa::SUCCESS ) {
			std::cout << "ERROR: I2C writeByte, write registry error. result=" << result << std::endl;
		}
	}
	else {
		std::cout << "ERROR: I2C writeByte set address error. result=" << result << std::endl;
	}
}

uint8_t LSM9DS0::I2CreadByte(uint8_t address, uint8_t subAddress)
{
	uint8_t data; // `data` will store the register data
	mraa::Result result = wire.I2C->address(address);
	if ( result == mraa::SUCCESS ) {
		data = wire.I2C->readReg(subAddress);
	}
	else {
		std::cout << "ERROR: I2C readByte set address error. result=" << result << std::endl;
	}
	return data;   // Return data read from slave register
}

void LSM9DS0::I2CreadBytes(uint8_t address, uint8_t subAddress, uint8_t * dest, uint8_t count)
{
	uint8_t stream_addr = subAddress | 0x80;
	mraa::Result result = wire.I2C->address(address);
	if ( result == mraa::SUCCESS ) {
		wire.I2C->write(&stream_addr, 1);
		result = wire.I2C->address(address);
		if ( result == mraa::SUCCESS ) {
			wire.I2C->read(dest, count);
		}
		else {
			std::cout << "ERROR: I2C readBytes address for read error. result=" << result << std::endl;
		}
	}
	else {
		std::cout << "ERROR: I2C readBytes address for write error. result=" << result << std::endl;
	}
}
