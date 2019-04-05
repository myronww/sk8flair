
import math
import mraa
import struct

#////////////////////////////
#// LSM9DS0 Gyro Registers //
#////////////////////////////
WHO_AM_I_G           = 0x0F
CTRL_REG1_G           = 0x20
CTRL_REG2_G           = 0x21
CTRL_REG3_G           = 0x22
CTRL_REG4_G           = 0x23
CTRL_REG5_G           = 0x24
REFERENCE_G           = 0x25
STATUS_REG_G       = 0x27
OUT_X_L_G           = 0x28
OUT_X_H_G           = 0x29
OUT_Y_L_G           = 0x2A
OUT_Y_H_G           = 0x2B
OUT_Z_L_G           = 0x2C
OUT_Z_H_G           = 0x2D
FIFO_CTRL_REG_G       = 0x2E
FIFO_SRC_REG_G       = 0x2F
INT1_CFG_G           = 0x30
INT1_SRC_G           = 0x31
INT1_THS_XH_G       = 0x32
INT1_THS_XL_G       = 0x33
INT1_THS_YH_G       = 0x34
INT1_THS_YL_G       = 0x35
INT1_THS_ZH_G       = 0x36
INT1_THS_ZL_G       = 0x37
INT1_DURATION_G       = 0x38

#//////////////////////////////////////////
#// LSM9DS0 Accel/Magneto (XM) Registers //
#//////////////////////////////////////////
OUT_TEMP_L_XM       = 0x05
OUT_TEMP_H_XM       = 0x06
STATUS_REG_M       = 0x07
OUT_X_L_M           = 0x08
OUT_X_H_M           = 0x09
OUT_Y_L_M           = 0x0A
OUT_Y_H_M           = 0x0B
OUT_Z_L_M           = 0x0C
OUT_Z_H_M           = 0x0D
WHO_AM_I_XM           = 0x0F
INT_CTRL_REG_M       = 0x12
INT_SRC_REG_M       = 0x13
INT_THS_L_M           = 0x14
INT_THS_H_M           = 0x15
OFFSET_X_L_M       = 0x16
OFFSET_X_H_M       = 0x17
OFFSET_Y_L_M       = 0x18
OFFSET_Y_H_M       = 0x19
OFFSET_Z_L_M       = 0x1A
OFFSET_Z_H_M       = 0x1B
REFERENCE_X           = 0x1C
REFERENCE_Y           = 0x1D
REFERENCE_Z           = 0x1E
CTRL_REG0_XM       = 0x1F
CTRL_REG1_XM       = 0x20
CTRL_REG2_XM       = 0x21
CTRL_REG3_XM       = 0x22
CTRL_REG4_XM       = 0x23
CTRL_REG5_XM       = 0x24
CTRL_REG6_XM       = 0x25
CTRL_REG7_XM       = 0x26
STATUS_REG_A       = 0x27
OUT_X_L_A           = 0x28
OUT_X_H_A           = 0x29
OUT_Y_L_A           = 0x2A
OUT_Y_H_A           = 0x2B
OUT_Z_L_A           = 0x2C
OUT_Z_H_A           = 0x2D
FIFO_CTRL_REG       = 0x2E
FIFO_SRC_REG       = 0x2F
INT_GEN_1_REG       = 0x30
INT_GEN_1_SRC       = 0x31
INT_GEN_1_THS       = 0x32
INT_GEN_1_DURATION   = 0x33
INT_GEN_2_REG       = 0x34
INT_GEN_2_SRC       = 0x35
INT_GEN_2_THS       = 0x36
INT_GEN_2_DURATION   = 0x37
CLICK_CFG           = 0x38
CLICK_SRC           = 0x39
CLICK_THS           = 0x3A
TIME_LIMIT           = 0x3B
TIME_LATENCY       = 0x3C
TIME_WINDOW           = 0x3D
ACT_THS               = 0x3E
ACT_DUR               = 0x3F


# MASKS
GYRO_SCALE_MASK = 0xFF ^ (0x3 << 4)


# I2C BUS
I2C_BUS = 1

# SPI BUS
SPI_BUS = 0
SPI_CLOCK_4MHZ = 4000000
SPI_MODE1 = 1

class ACCEL_ABW:
    """
        ACCEL_ABW defines all possible anti-aliasing filter rates of the accelerometer
    """
    A_ABW_773 = 0x0    # 773 Hz
    A_ABW_194 = 0x1    # 194 Hz
    A_ABW_362 = 0x2    # 362 Hz
    A_ABW_50 = 0x3     # 50 Hz

class ACCEL_ODR:
    """
        ACCEL_ODR defines all possible output data rates of the accelerometer
    """
    A_POWER_DOWN = 0x0  # Power-down mode
    A_ODR_3125 = 0x1    # 3.125 Hz
    A_ODR_625 = 0x2     # 6.25 Hz
    A_ODR_125 = 0x3     # 12.5 Hz
    A_ODR_25 = 0x4      # 25 Hz
    A_ODR_50 = 0x5      # 50 Hz
    A_ODR_100 = 0x6     # 100 Hz
    A_ODR_200 = 0x7     # 200 Hz
    A_ODR_400 = 0x8     # 400 Hz
    A_ODR_800 = 0x9     # 800 Hz
    A_ODR_1600 = 0xA    # 1600 Hz

class ACCEL_SCALE:
    """
        ACCEL_SCALE defines all possible FSR's of the accelerometer
    """
    A_SCALE_2G = 0b000 # 2g
    A_SCALE_4G = 0b001 # 4g
    A_SCALE_6G = 0b010 # 6g
    A_SCALE_8G = 0b011 # 8g
    A_SCALE_16G = 0b100 # 16g

class MAG_ODR:
    """
        MAG_ODR defines all possible output data rates of the magnetometer
    """
    M_ODR_3125 = 0x00  # 3.125 Hz
    M_ODR_625 = 0x01   # 6.25 Hz
    M_ODR_125 = 0x02   # 12.5 Hz
    M_ODR_25 = 0x03    # 25 Hz
    M_ODR_50 = 0x04    # 50 Hz
    M_ODR_100 = 0x05   # 100 Hz

class MAG_SCALE:
    """
        MAG_SCALE defines all possible FSR's of the magnetometer
    """
    M_SCALE_2GS = 0b00 # 2Gs
    M_SCALE_4GS = 0b01 # 4Gs
    M_SCALE_8GS = 0b10 # 8Gs
    M_SCALE_12GS = 0b11 # 12Gs

class INTERFACE_MODE:
    MODE_SPI = 0x00
    MODE_I2C = 0x01

class GYRO_ODR:
    """
        GYRO_ODR defines all possible data rate/bandwidth combos of the gyro
    """
    #                          ODR (Hz) --- Cutoff
    G_ODR_95_BW_125  = 0x0  #   95         12.5
    G_ODR_95_BW_25   = 0x1  #   95          25
    # 0x2 and 0x3 define the same data rate and bandwidth
    G_ODR_190_BW_125 = 0x4  #   190        12.5
    G_ODR_190_BW_25  = 0x5  #   190         25
    G_ODR_190_BW_50  = 0x6  #   190         50
    G_ODR_190_BW_70  = 0x7  #   190         70
    G_ODR_380_BW_20  = 0x8  #   380         20
    G_ODR_380_BW_25  = 0x9  #   380         25
    G_ODR_380_BW_50  = 0xA  #   380         50
    G_ODR_380_BW_100 = 0xB  #   380         100
    G_ODR_760_BW_30  = 0xC  #   760         30
    G_ODR_760_BW_35  = 0xD  #   760         35
    G_ODR_760_BW_50  = 0xE  #   760         50
    G_ODR_760_BW_100 = 0xF  #   760         100

class GYRO_SCALE:
    """
        GYRO_SCALE defines the possible full-scale ranges of the gyroscope
    """
    G_SCALE_245DPS = 0b00 # 245 degrees per second
    G_SCALE_500DPS = 0b01 # 500 dps
    G_SCALE_2000DPS = 0b10 # 2000 dps

ACCELEROMETER_SENSITIVITY = 8192.0

GYRO_ADDRESS = 0x6B
GYROSCOPE_SENSITIVITY = 65.536

PI_CONST = 3.14159265359

SAMPLE_RATE = 0.02                            # 10 ms sample rate

XM_ADDRESS = 0x1D

def make_int16(dataH, dataL):
    uval = (dataH << 8) | dataL
    if (uval & 0x8000) > 0:
        uval = (((uval ^ 0xffff) & 0xffff) + 1) * -1
    return uval

class LSM9DS0:

    # We'll store the gyro, accel, and magnetometer readings in a series of
    # public class variables. Each sensor gets three variables -- one for each
    # axis. Call readGyro(), readAccel(), and readMag() first, before using
    # these variables!
    # These values are the RAW signed 16-bit readings from the sensors.

    # x, y, and z axis readings of the gyroscope
    gx = None
    gy = None
    gz = None

    # x, y, and z axis readings of the accelerometer
    ax = None
    ay = None
    az = None

    # x, y, and z axis readings of the magnetometer
    mx = None
    my = None
    mz = None

    temperature = None

    pitch = 0.0
    roll = 0.0

    def __init__(self, gyro_addr=GYRO_ADDRESS, xm_addr=XM_ADDRESS, io_mode=INTERFACE_MODE.MODE_I2C):
        """
            The constructor will set up a handful of private variables, and set the communication mode as well.

            Input:
                io_mode     - Either MODE_SPI or MODE_I2C, whichever you're using to talk to the IC.
                gyro_addr   - If MODE_I2C, this is the I2C address of the gyroscope.  If MODE_SPI, this is
                              the chip select pin of the gyro (CSG)
                xm_addr - If MODE_I2C, this is the I2C address of the accel/mag.  If MODE_SPI, this is the
                              cs pin of the accel/mag (CSXM)
        """

        self._io_mode = io_mode

        self._xm_addr = xm_addr
        self._gyro_addr = gyro_addr

        self._gyro_scale = None
        self._accel_scale = None
        self._mag_scale = None

        # gRes, aRes, and mRes store the current resolution for each sensor. 
        # Units of these values would be DPS (or g's or Gs's) per ADC tick.
        # This value is calculated as (sensor scale) / (2^15).
        self._gyro_res = None
        self._accel_res = None
        self._mag_res = None

        # This will be either an SPI bus object or I2C buss object depending on the io_mode selected
        self._iocls = None

        return

    @property
    def accel_res(self):
        return self._accel_res

    @property
    def gyro_res(self):
        return self._gyro_res

    @property
    def mag_res(self):
        return self._mag_res

    def begin(self, gyro_scale=GYRO_SCALE.G_SCALE_245DPS, accel_scale=ACCEL_SCALE.A_SCALE_2G, mag_scale= MAG_SCALE.M_SCALE_2GS,
                    gyro_odr=GYRO_ODR.G_ODR_95_BW_125, accel_odr=ACCEL_ODR.A_ODR_50, mag_odr=MAG_ODR.M_ODR_50):
        """
            This will set up the scale and output rate of each sensor. It'll also "turn on" every sensor and every axis of
            every sensor.

            Input:
                gyro_scale = The scale of the gyroscope. This should be a gyro_scale value.
                accel_scale = The scale of the accelerometer. Should be a accel_scale value.
                mag_scale = The scale of the magnetometer. Should be a mag_scale value.
                gyro_odr = Output data rate of the gyroscope. gyro_odr value.
                accel_odr = Output data rate of the accelerometer. accel_odr value.
                mag_odr = Output data rate of the magnetometer. mag_odr value.

            Output: The function will return an unsigned 16-bit value. The most-sig bytes of the output are the WHO_AM_I
            reading of the accel. The least significant two bytes are the WHO_AM_I reading of the gyro.

            All parameters have a defaulted value, so you can call just "begin()".  Default values are FSR's of:
                245DPS, 2g, 2Gs; ODRs of 95 Hz for gyro, 100 Hz for accelerometer, 100 Hz for magnetometer.

            Use the return value of this function to verify communication.
        """
        # Store the given scales in class variables. These scale variables
        # are used throughout to calculate the actual g's, DPS,and Gs's.
        self._gyro_scale = gyro_scale
        self._accel_scale = accel_scale
        self._mag_scale = mag_scale

        # Once we have the scale values, we can calculate the resolution
        # of each sensor. That's what these functions are for. One for each sensor
        self._calc_gyro_resolution()  # Calculate DPS / ADC tick, stored in gRes variable
        self._calc_mag_resolution()   # Calculate Gs / ADC tick, stored in mRes variable
        self._calc_accel_resolution() # Calculate g / ADC tick, stored in aRes variable

        # Now, initialize our hardware interface.
        if self._io_mode == INTERFACE_MODE.MODE_I2C:        # If we're using I2C
            self._init_i2c()                        # Initialize I2C
        elif self._io_mode == INTERFACE_MODE.MODE_SPI:  # else, if we're using SPI
            self._init_spi()                        # Initialize SPI
        else:
            raise ValueError("Invalid io_mode passed (%d)" % self._io_mode)

        # To verify communication, we can read from the WHO_AM_I register of
        # each device. Store those in a variable so we can return them.
        g_test = self._gyro_read_byte(WHO_AM_I_G)    # Read the gyro WHO_AM_I
        xm_test = self._xm_read_byte(WHO_AM_I_XM)      # Read the accel/mag WHO_AM_I

        who_am_i = (xm_test << 8) | g_test

        # Gyro initialization stuff:
        self._init_gyro()
        self.set_gyro_odr(gyro_odr) # Set the gyro output data rate and bandwidth.
        self.set_gyro_scale(self._gyro_scale) # Set the gyro range

        # Accelerometer initialization stuff:
        self._init_accel() # "Turn on" all axes of the accel. Set up interrupts, etc.
        self.set_accel_odr(accel_odr) # Set the accel data rate.
        self.set_accel_scale(self._accel_scale) # Set the accel range.

        # Magnetometer initialization stuff:
        self._init_mag() # "Turn on" all axes of the mag. Set up interrupts, etc.
        self.set_mag_odr(mag_odr) # Set the magnetometer output data rate.
        self.set_mag_scale(self._mag_scale) # Set the magnetometer's range.

        # Once everything is initialized, return the WHO_AM_I registers we read:

        return who_am_i

    def calc_accel(self, accel):
        """
            This function reads in a signed 16-bit value and returns the scaled g's. This function relies on
            aScale and aRes being correct.

            Input:
                accel - A signed 16-bit raw reading from the accelerometer.
        """
        return self._accel_res * float(accel)

    def calc_gyro(self, gyro):
        """
            This function reads in a signed 16-bit value and returns the scaled DPS. This function relies on
            gScale and gRes being correct.

            Input:
                gyro - A signed 16-bit raw reading from the gyroscope.
        """
        return self._gyro_res * float(gyro)

    def calc_mag(self, mag):
        """
            This function reads in a signed 16-bit value and returns the scaled Gs. This function relies on mScale
            and mRes being correct.

            Input:
                mag - A signed 16-bit raw reading from the magnetometer.
        """
        return self._mag_res * float(mag)

    def config_gyro_int(self, int1Cfg, int1ThsX=0, int1ThsY=0, int1ThsZ=0, duration=0):
        """
            Configures the gyro interrupt output.  Triggers can be set to either rising above
            or falling below a specified threshold. This function helps setup the interrupt
            configuration and threshold values for all axes.

            Input:
                int1Cfg - A 8-bit value that is sent directly to the INT1_CFG_G register. This sets AND/OR
                          and high/low interrupt gen for each axis
                int1ThsX - 16-bit interrupt threshold value for x-axis
                int1ThsY - 16-bit interrupt threshold value for y-axis
                int1ThsZ - 16-bit interrupt threshold value for z-axis
                duration - Duration an interrupt holds after triggered. This value is copied directly into
                           the INT1_DURATION_G register.

            Before using this function, read about the INT1_CFG_G register and the related INT1* registers in
            the LMS9DS0 datasheet.
        """
        self._gyro_write_byte(INT1_CFG_G, int1Cfg)
        self._gyro_write_byte(INT1_THS_XH_G, (int1ThsX & 0xFF00) >> 8)
        self._gyro_write_byte(INT1_THS_XL_G, (int1ThsX & 0xFF))
        self._gyro_write_byte(INT1_THS_YH_G, (int1ThsY & 0xFF00) >> 8)
        self._gyro_write_byte(INT1_THS_YL_G, (int1ThsY & 0xFF))
        self._gyro_write_byte(INT1_THS_ZH_G, (int1ThsZ & 0xFF00) >> 8)
        self._gyro_write_byte(INT1_THS_ZL_G, (int1ThsZ & 0xFF))
        if (duration):
            self._gyro_write_byte(INT1_DURATION_G, 0x80 | duration)
        else:
            self._gyro_write_byte(INT1_DURATION_G, 0x00)
        return

    def read_accel(self):
        """
            This function will read all six accelerometer output registers.  The readings are stored in the
            class' ax, ay, and az variables. Read those _after_ calling read_accel().
        """
        data = self._xm_read_bytes(OUT_X_L_A, 6)  # Read 6 bytes, beginning at OUT_X_L_A
        self.ax = make_int16(data[1], data[0])  # Store x-axis values into ax
        self.ay = make_int16(data[3], data[2])  # Store y-axis values into ay
        self.az = make_int16(data[5], data[4])  # Store z-axis values into az
        return

    def read_gyro(self):
        """
            This function will read all six gyroscope output registers.  The readings are stored in the class'
            gx, gy, and gz variables. Read those _after_ calling read_gyro().
        """
        data = self._gyro_read_bytes(OUT_X_L_G, 6)  # Read 6 bytes, beginning at OUT_X_L_G
        self.gx = make_int16(data[1], data[0])  # Store x-axis values into gx
        self.gy = make_int16(data[3], data[2])  # Store y-axis values into gy
        self.gz = make_int16(data[5], data[4])  # Store z-axis values into gz
        return

    def read_mag(self):
        """
            This function will read all six magnetometer output registers.  The readings are stored in the class'
            mx, my, and mz variables. Read those _after_ calling read_mag().
        """
        data = self._xm_read_bytes(OUT_X_L_M, 6)  # Read 6 bytes, beginning at OUT_X_L_M
        self.mx = make_int16(data[1], data[0])  # Store x-axis values into mx
        self.my = make_int16(data[3], data[2])  # Store y-axis values into my
        self.mz = make_int16(data[5], data[4])  # Store z-axis values into mz
        return

    def read_temp(self):
        """
            This function will read two temperature output registers.  The combined readings are stored in the class'
            temperature variables. Read those _after_ calling read_temp().
        """
        data = self._xm_read_bytes(OUT_TEMP_L_XM, 2)  # Read 2 bytes, beginning at OUT_TEMP_L_M
        self.temperature = (((data[1] << 12) | data[0] << 4 ) >> 4)  # Temperature is a 12-bit signed integer
        return

    def set_accel_abw(self, accel_abw):
        """
            Set the anti-aliasing filter rate of the accelerometer

            Input:
                accel_abw - The desired anti-aliasing filter rate of the accel.  Must be a value from the accel_abw
                            enum (check above, there're 4).
        """
        # We need to preserve the other bytes in CTRL_REG2_XM. So, first read it:
        temp = self._xm_read_byte(CTRL_REG2_XM)
        # Then mask out the accel ABW bits:
        temp &= 0xFF^(0x3 << 7)
        # Then shift in our new ODR bits:
        temp |= (accel_abw << 7)
        # And write the new register value back into CTRL_REG2_XM:
        self._xm_write_byte(CTRL_REG2_XM, temp)
        return

    def set_accel_odr(self, accel_odr):
        """
            Set the output data rate of the accelerometer

            Input:
                accel_odr - The desired output rate of the accel. Must be a value from the accel_odr enum (check above, there're 11).
        """
        # We need to preserve the other bytes in CTRL_REG1_XM. So, first read it:
        temp = self._xm_read_byte(CTRL_REG1_XM)
        # Then mask out the accel ODR bits:
        temp &= 0xFF^(0xF << 4)
        # Then shift in our new ODR bits:
        temp |= (accel_odr << 4)
        # And write the new register value back into CTRL_REG1_XM:
        self._xm_write_byte(CTRL_REG1_XM, temp)
        return

    def set_accel_scale(self, accel_scale):
        """
            This function can be called to set the scale of the accelerometer to 2, 4, 6, 8, or 16 g's.

            Input:
                accel_scale - The desired accelerometer scale. Must be one of five possible values from the
                              accel_scale enum.
        """
        # We need to preserve the other bytes in CTRL_REG2_XM. So, first read it:
        temp = self._xm_read_byte(CTRL_REG2_XM)
        # Then mask out the accel scale bits:
        temp &= 0xFF^(0x3 << 3)
        # Then shift in our new scale bits:
        temp |= accel_scale << 3
        # And write the new register value back into CTRL_REG2_XM:
        self._xm_write_byte(CTRL_REG2_XM, temp)

        # We've updated the sensor, but we also need to update our class variables
        # First update aScale:
        self._accel_scale = accel_scale
        # Then calculate a new aRes, which relies on aScale being set correctly:
        self._calc_accel_resolution()
        return

    def set_gyro_odr(self, gyro_odr):
        """
            Set the output data rate and bandwidth of the gyroscope

            Input:
                gyro_odr - The desired output rate and cutoff frequency of the gyro. Must be a value from the
                gyro_odr enum (check above, there're 14).
        """
        return

    def set_gyro_scale(self, gyro_scale):
        """
            This function can be called to set the scale of the gyroscope to 245, 500, or 200 degrees per second.

            Input:
                gyro_scale - The desired gyroscope scale. Must be one of three possible values from the gyro_scale enum.
        """
        # We need to preserve the other bytes in CTRL_REG4_G. So, first read it:
        temp = self._gyro_read_byte(CTRL_REG4_G)
        # Then mask out the gyro scale bits:
        temp = temp & GYRO_SCALE_MASK
        # Then shift in our new scale bits:
        temp = temp | gyro_scale << 4
        # And write the new register value back into CTRL_REG4_G:
        self._gyro_write_byte(CTRL_REG4_G, temp)

        # We've updated the sensor, but we also need to update our class variables
        # First update gScale:
        self._gyro_scale = gyro_scale
        # Then calculate a new gRes, which relies on gScale being set correctly:
        self._calc_gyro_resolution()
        return

    def set_mag_odr(self, mag_odr):
        """
            Set the output data rate of the magnetometer

            Input:
                mag_scale - The desired output rate of the mag. Must be a value from the mag_odr enum (check above, there're 6).
        """
        # We need to preserve the other bytes in CTRL_REG5_XM. So, first read it:
        temp = self._xm_read_byte(CTRL_REG5_XM)
        # Then mask out the mag ODR bits:
        temp &= 0xFF^(0x7 << 2)
        # Then shift in our new ODR bits:
        temp |= (mag_odr << 2)
        # And write the new register value back into CTRL_REG5_XM:
        self._xm_write_byte(CTRL_REG5_XM, temp)
        return

    def set_mag_scale(self, mag_scale):
        """
            This function can be called to set the scale of the magnetometer to 2, 4, 8, or 12 Gs.

            Input:
                mag_scale - The desired magnetometer scale. Must be one of four possible values from the mag_scale enum.
        """
        # We need to preserve the other bytes in CTRL_REG6_XM. So, first read it:
        temp = self._xm_read_byte(CTRL_REG6_XM)
        # Then mask out the mag scale bits:
        temp &= 0xFF^(0x3 << 5)
        # Then shift in our new scale bits:
        temp |= mag_scale << 5
        # And write the new register value back into CTRL_REG6_XM:
        self._xm_write_byte(CTRL_REG6_XM, temp)
        
        # We've updated the sensor, but we also need to update our class variables
        # First update mScale:
        self._mag_scale = mag_scale
        # Then calculate a new mRes, which relies on mScale being set correctly:
        self._calc_mag_resolution()

        return

    def update_complimentary_filter(self):

        data = self._xm_read_bytes(OUT_X_L_A, 6)  # Read 6 bytes, beginning at OUT_X_L_A
        self.ax = make_int16(data[1], data[0])  # Store x-axis values into ax
        self.ay = make_int16(data[3], data[2])  # Store y-axis values into ay
        self.az = make_int16(data[5], data[4])  # Store z-axis values into az

        data = self._gyro_read_bytes(OUT_X_L_G, 6)  # Read 6 bytes, beginning at OUT_X_L_G
        self.gx = make_int16(data[1], data[0])  # Store x-axis values into gx
        self.gy = make_int16(data[3], data[2])  # Store y-axis values into gy
        self.gz = make_int16(data[5], data[4])  # Store z-axis values into gz

        data = self._xm_read_bytes(OUT_X_L_M, 6)  # Read 6 bytes, beginning at OUT_X_L_M
        self.mx = ((data[1] << 8) | data[0])  # Store x-axis values into mx
        self.my = ((data[3] << 8) | data[2])  # Store y-axis values into my
        self.mz = ((data[5] << 8) | data[4])  # Store z-axis values into mz

        self.pitch = self.pitch + ((float(self.gx) / GYROSCOPE_SENSITIVITY) * SAMPLE_RATE) # Angle around the X-axis
        self.roll = self.roll + ((float(self.gy) / GYROSCOPE_SENSITIVITY) * SAMPLE_RATE) # Angle around the Y-axis

        # Compensate for drift with accelerometer data if !bullshit
        # Sensitivity = -2 to 2 G at 16Bit -> 2G = 32768 && 0.5G = 8192
        force_mag = abs(self.ax) + abs(self.ay) + abs(self.az);
        if (force_mag > 8192 and force_mag < 32768):
            # Turning around the X axis results in a vector on the Y-axis
            pitchAcc = math.atan2(float(self.ay), float(self.az)) * 180 / PI_CONST
            self.pitch = self.pitch * 0.98 + pitchAcc * 0.02

            # Turning around the Y axis results in a vector on the X-axis
            rollAcc = math.atan2(float(self.ax), float(self.az)) * 180 / PI_CONST;
            self.roll = self.roll * 0.98 + rollAcc * 0.02

        return

    #void calLSM9DS0(float gbias[3], float abias[3])

    def _calc_gyro_resolution(self):
        """
            Calculate the resolution of the gyroscope. This function will set the value of the gRes variable.

            NOTE: gyro_scale must be set prior to calling this function.
        """
        # Possible gyro scales (and their register bit settings) are:
        # 245 DPS (00), 500 DPS (01), 2000 DPS (10). Here's a bit of an algorithm
        # to calculate DPS/(ADC tick) based on that 2-bit value:
        if self._gyro_scale == GYRO_SCALE.G_SCALE_245DPS:
            self._gyro_res = 245.0 / 32768.0
        elif self._gyro_scale == GYRO_SCALE.G_SCALE_500DPS:
            self._gyro_res = 500.0 / 32768.0
        elif self._gyro_scale == GYRO_SCALE.G_SCALE_2000DPS:
            self._gyro_res = 2000.0 / 32768.0
        else:
            raise ValueError("The gyro_scale(%r) is not supported" % self._gyro_scale)
        return

    def _calc_mag_resolution(self):
        """
            Calculate the resolution of the magnetometer. This function will set the value of the mRes variable.

            NOTE: mag_scale must be set prior to calling this function.
        """
        # Possible magnetometer scales (and their register bit settings) are:
        # 2 Gs (00), 4 Gs (01), 8 Gs (10) 12 Gs (11). Here's a bit of an algorithm
        # to calculate Gs/(ADC tick) based on that 2-bit value:
        if self._mag_scale == MAG_SCALE.M_SCALE_2GS:
            self._mag_res = 2.0 / 32768.0
        elif self._mag_scale == MAG_SCALE.M_SCALE_4GS or \
             self._mag_scale == MAG_SCALE.M_SCALE_8GS or \
             self._mag_scale == MAG_SCALE.M_SCALE_12GS: 
            self._mag_res = float((self._mag_scale << 2)) / 32768.0
        else:
            raise ValueError("The mag_scale(%r) is not supported" % self._mag_scale)
        return

    def _calc_accel_resolution(self):
        """
            Calculate the resolution of the accelerometer. This function will set the value of the aRes variable.

            NOTE: accel_scale must be set prior to calling this function.
        """
        # Possible accelerometer scales (and their register bit settings) are:
        # 2 g (000), 4g (001), 6g (010) 8g (011), 16g (100). Here's a bit of an 
        # algorithm to calculate g/(ADC tick) based on that 3-bit value:
        if self._accel_scale == ACCEL_SCALE.A_SCALE_16G:
            self._accel_res = 16.0 / 32768.0
        elif self._accel_scale == ACCEL_SCALE.A_SCALE_2G or \
             self._accel_scale == ACCEL_SCALE.A_SCALE_4G or \
             self._accel_scale == ACCEL_SCALE.A_SCALE_6G or \
             self._accel_scale == ACCEL_SCALE.A_SCALE_8G: 
            self._accel_res = ((float(self._accel_scale) + 1.0) * 2.0) / 32768.0
        else:
            raise ValueError("The accel_scale(%r) is not supported" % self._accel_scale)
        return

    def _init_accel(self):
        """
            Sets up the accelerometer to begin reading.  This function steps through all accelerometer related control registers.

            Upon exit these registers will be set as:
                CTRL_REG0_XM = 0x00: FIFO disabled. HPF bypassed. Normal mode.
                CTRL_REG1_XM = 0x57: 100 Hz data rate. Continuous update. all axes enabled.
                CTRL_REG2_XM = 0x00:  2g scale. 773 Hz anti-alias filter BW.
                CTRL_REG3_XM = 0x04: Accel data ready signal on INT1_XM pin.
        """
        # CTRL_REG0_XM (0x1F) (Default value: 0x00)
        # Bits (7-0): BOOT FIFO_EN WTM_EN 0 0 HP_CLICK HPIS1 HPIS2
        # BOOT - Reboot memory content (0: normal, 1: reboot)
        # FIFO_EN - Fifo enable (0: disable, 1: enable)
        # WTM_EN - FIFO watermark enable (0: disable, 1: enable)
        # HP_CLICK - HPF enabled for click (0: filter bypassed, 1: enabled)
        # HPIS1 - HPF enabled for interrupt generator 1 (0: bypassed, 1: enabled)
        # HPIS2 - HPF enabled for interrupt generator 2 (0: bypassed, 1 enabled)   */
        self._xm_write_byte(CTRL_REG0_XM, 0x00)

        # CTRL_REG1_XM (0x20) (Default value: 0x07)
        # Bits (7-0): AODR3 AODR2 AODR1 AODR0 BDU AZEN AYEN AXEN
        # AODR[3:0] - select the acceleration data rate:
        #     0000=power down, 0001=3.125Hz, 0010=6.25Hz, 0011=12.5Hz, 
        #     0100=25Hz, 0101=50Hz, 0110=100Hz, 0111=200Hz, 1000=400Hz,
        #     1001=800Hz, 1010=1600Hz, (remaining combinations undefined).
        # BDU - block data update for accel AND mag
        #     0: Continuous update
        #     1: Output registers aren't updated until MSB and LSB have been read.
        # AZEN, AYEN, and AXEN - Acceleration x/y/z-axis enabled.
        #     0: Axis disabled, 1: Axis enabled                                     */    
        self._xm_write_byte(CTRL_REG1_XM, 0x57) # 100Hz data rate, x/y/z all enabled

        # Serial.println(xmReadByte(CTRL_REG1_XM))
        # CTRL_REG2_XM (0x21) (Default value: 0x00)
        # Bits (7-0): ABW1 ABW0 AFS2 AFS1 AFS0 AST1 AST0 SIM
        # ABW[1:0] - Accelerometer anti-alias filter bandwidth
        #     00=773Hz, 01=194Hz, 10=362Hz, 11=50Hz
        # AFS[2:0] - Accel full-scale selection
        #     000=+/-2g, 001=+/-4g, 010=+/-6g, 011=+/-8g, 100=+/-16g
        # AST[1:0] - Accel self-test enable
        #     00=normal (no self-test), 01=positive st, 10=negative st, 11=not allowed
        # SIM - SPI mode selection
        #     0=4-wire, 1=3-wire                                                     */
        self._xm_write_byte(CTRL_REG2_XM, 0x00) # Set scale to 2g

        # CTRL_REG3_XM is used to set interrupt generators on INT1_XM
        # Bits (7-0): P1_BOOT P1_TAP P1_INT1 P1_INT2 P1_INTM P1_DRDYA P1_DRDYM P1_EMPTY
        # Accelerometer data ready on INT1_XM (0x04)
        self._xm_write_byte(CTRL_REG3_XM, 0x04)

        return

    def _init_gyro(self):
        """
            Sets up the gyroscope to begin reading. This function steps through all five gyroscope control registers.

            Upon exit, the following parameters will be set:
                CTRL_REG1_G = 0x0F: Normal operation mode, all axes enabled. 95 Hz ODR, 12.5 Hz cutoff frequency.
                CTRL_REG2_G = 0x00: HPF set to normal mode, cutoff frequency set to 7.2 Hz (depends on ODR).
                CTRL_REG3_G = 0x88: Interrupt enabled on INT_G (set to push-pull and active high). Data-ready output enabled on DRDY_G.
                CTRL_REG4_G = 0x00: Continuous update mode. Data LSB stored in lower address. Scale set to 245 DPS. SPI mode set to 4-wire.
                CTRL_REG5_G = 0x00: FIFO disabled. HPF disabled.
        """
        # CTRL_REG1_G sets output data rate, bandwidth, power-down and enables
        # Bits[7:0]: DR1 DR0 BW1 BW0 PD Zen Xen Yen
        # DR[1:0] - Output data rate selection
        #    00=95Hz, 01=190Hz, 10=380Hz, 11=760Hz
        # BW[1:0] - Bandwidth selection (sets cutoff frequency)
        #    Value depends on ODR. See datasheet table 21.
        # PD - Power down enable (0=power down mode, 1=normal or sleep mode)
        # Zen, Xen, Yen - Axis enable (o=disabled, 1=enabled)
        self._gyro_write_byte(CTRL_REG1_G, 0x0F) # Normal mode, enable all axes

        # CTRL_REG2_G sets up the HPF
        # Bits[7:0]: 0 0 HPM1 HPM0 HPCF3 HPCF2 HPCF1 HPCF0
        # HPM[1:0] - High pass filter mode selection
        #    00=normal (reset reading HP_RESET_FILTER, 01=ref signal for filtering,
        #    10=normal, 11=autoreset on interrupt
        # HPCF[3:0] - High pass filter cutoff frequency
        #    Value depends on data rate. See datasheet table 26.
        self._gyro_write_byte(CTRL_REG2_G, 0x00) # Normal mode, high cutoff frequency

        # CTRL_REG3_G sets up interrupt and DRDY_G pins
        # Bits[7:0]: I1_IINT1 I1_BOOT H_LACTIVE PP_OD I2_DRDY I2_WTM I2_ORUN I2_EMPTY
        # I1_INT1 - Interrupt enable on INT_G pin (0=disable, 1=enable)
        # I1_BOOT - Boot status available on INT_G (0=disable, 1=enable)
        # H_LACTIVE - Interrupt active configuration on INT_G (0:high, 1:low)
        # PP_OD - Push-pull/open-drain (0=push-pull, 1=open-drain)
        # I2_DRDY - Data ready on DRDY_G (0=disable, 1=enable)
        # I2_WTM - FIFO watermark interrupt on DRDY_G (0=disable 1=enable)
        # I2_ORUN - FIFO overrun interrupt on DRDY_G (0=disable 1=enable)
        # I2_EMPTY - FIFO empty interrupt on DRDY_G (0=disable 1=enable) */
        # Int1 enabled (pp, active low), data read on DRDY_G:
        self._gyro_write_byte(CTRL_REG3_G, 0x88)

        # CTRL_REG4_G sets the scale, update mode
        # Bits[7:0] - BDU BLE FS1 FS0 - ST1 ST0 SIM
        # BDU - Block data update (0=continuous, 1=output not updated until read
        # BLE - Big/little endian (0=data LSB @ lower address, 1=LSB @ higher add)
        #FS[1:0] - Full-scale selection
        #    00=245dps, 01=500dps, 10=2000dps, 11=2000dps
        #ST[1:0] - Self-test enable
        #    00=disabled, 01=st 0 (x+, y-, z-), 10=undefined, 11=st 1 (x-, y+, z+)
        #SIM - SPI serial interface mode select
        #    0=4 wire, 1=3 wire */
        self._gyro_write_byte(CTRL_REG4_G, 0x00) # Set scale to 245 dps

        # CTRL_REG5_G sets up the FIFO, HPF, and INT1
        # Bits[7:0] - BOOT FIFO_EN - HPen INT1_Sel1 INT1_Sel0 Out_Sel1 Out_Sel0
        # BOOT - Reboot memory content (0=normal, 1=reboot)
        # FIFO_EN - FIFO enable (0=disable, 1=enable)
        # HPen - HPF enable (0=disable, 1=enable)
        # INT1_Sel[1:0] - Int 1 selection configuration
        # Out_Sel[1:0] - Out selection configuration */
        self._gyro_write_byte(CTRL_REG5_G, 0x00)
        
        # Temporary !!! For testing !!! Remove !!! Or make useful !!!
        #self.config_gyro_int(0x2A, 0, 0, 0, 0)  # Trigger interrupt when above 0 DPS...

        return

    def _init_mag(self):
        """
            Sets up the magnetometer to begin reading. This function steps through all magnetometer-related control registers.

            Upon exit these registers will be set as:

                CTRL_REG4_XM = 0x04: Mag data ready signal on INT2_XM pin.
                CTRL_REG5_XM = 0x14: 100 Hz update rate. Low resolution. Interrupt requests don't latch. Temperature sensor disabled.
                CTRL_REG6_XM = 0x00:  2 Gs scale.
                CTRL_REG7_XM = 0x00: Continuous conversion mode. Normal HPF mode.
                INT_CTRL_REG_M = 0x09: Interrupt active-high. Enable interrupts.
        """
        # CTRL_REG5_XM enables temp sensor, sets mag resolution and data rate
        # Bits (7-0): TEMP_EN M_RES1 M_RES0 M_ODR2 M_ODR1 M_ODR0 LIR2 LIR1
        # TEMP_EN - Enable temperature sensor (0=disabled, 1=enabled)
        # M_RES[1:0] - Magnetometer resolution select (0=low, 3=high)
        # M_ODR[2:0] - Magnetometer data rate select
        #     000=3.125Hz, 001=6.25Hz, 010=12.5Hz, 011=25Hz, 100=50Hz, 101=100Hz
        # LIR2 - Latch interrupt request on INT2_SRC (cleared by reading INT2_SRC)
        #     0=interrupt request not latched, 1=interrupt request latched
        # LIR1 - Latch interrupt request on INT1_SRC (cleared by readging INT1_SRC)
        #     0=irq not latched, 1=irq latched                                      */
        self._xm_write_byte(CTRL_REG5_XM, 0x94) # Mag data rate - 100 Hz, enable temperature sensor

        # CTRL_REG6_XM sets the magnetometer full-scale
        # Bits (7-0): 0 MFS1 MFS0 0 0 0 0 0
        # MFS[1:0] - Magnetic full-scale selection
        # 00:+/-2Gauss, 01:+/-4Gs, 10:+/-8Gs, 11:+/-12Gs                             */
        self._xm_write_byte(CTRL_REG6_XM, 0x00) # Mag scale to +/- 2GS

        # CTRL_REG7_XM sets magnetic sensor mode, low power mode, and filters
        # AHPM1 AHPM0 AFDS 0 0 MLP MD1 MD0
        # AHPM[1:0] - HPF mode selection
        #     00=normal (resets reference registers), 01=reference signal for filtering, 
        #     10=normal, 11=autoreset on interrupt event
        # AFDS - Filtered acceleration data selection
        #     0=internal filter bypassed, 1=data from internal filter sent to FIFO
        # MLP - Magnetic data low-power mode
        #     0=data rate is set by M_ODR bits in CTRL_REG5
        #     1=data rate is set to 3.125Hz
        # MD[1:0] - Magnetic sensor mode selection (default 10)
        #     00=continuous-conversion, 01=single-conversion, 10 and 11=power-down */
        self._xm_write_byte(CTRL_REG7_XM, 0x00) # Continuous conversion mode

        #  CTRL_REG4_XM is used to set interrupt generators on INT2_XM
        #  Bits (7-0): P2_TAP P2_INT1 P2_INT2 P2_INTM P2_DRDYA P2_DRDYM P2_Overrun P2_WTM
        self._xm_write_byte(CTRL_REG4_XM, 0x04) # Magnetometer data ready on INT2_XM (0x08)

        #  INT_CTRL_REG_M to set push-pull/open drain, and active-low/high
        # Bits[7:0] - XMIEN YMIEN ZMIEN PP_OD IEA IEL 4D MIEN
        # XMIEN, YMIEN, ZMIEN - Enable interrupt recognition on axis for mag data
        # PP_OD - Push-pull/open-drain interrupt configuration (0=push-pull, 1=od)
        # IEA - Interrupt polarity for accel and magneto
        #     0=active-low, 1=active-high
        # IEL - Latch interrupt request for accel and magneto
        #     0=irq not latched, 1=irq latched
        # 4D - 4D enable. 4D detection is enabled when 6D bit in INT_GEN1_REG is set
        # MIEN - Enable interrupt generation for magnetic data
        #     0=disable, 1=enable) */
        self._xm_write_byte(INT_CTRL_REG_M, 0x09) # Enable interrupts for mag, active-low, push-pull

        return

    def _gyro_read_byte(self, reg_addr):
        """
            Reads a byte from a specified gyroscope register.

            Input:
                reg_addr - Register to be read from.

            Output:
                An 8-bit value read from the requested address.
        """
        rtn_val = None

        # Whether we're using I2C or SPI, read a byte using the
        # gyro-specific I2C address or SPI CS pin.
        if self._io_mode == INTERFACE_MODE.MODE_I2C:
            rtn_val = self._i2c_read_byte(self._gyro_addr, reg_addr)
        elif self._io_mode == INTERFACE_MODE.MODE_SPI:
            rtn_val = self._spi_read_byte(self._gyro_addr, reg_addr)
        else:
            raise ValueError("Illegal iomode (%r)" % self._io_mode)

        return rtn_val

    def _gyro_read_bytes(self, reg_addr, count):
        """
            Reads a number of bytes -- beginning at an address and incrementing from there -- from the gyroscope.

            Input:
                reg_addr - Register to be read from.
                count - The number of bytes to be read.

            Output: A buffer with the returned data
        """
        rdata = None
        # Whether we're using I2C or SPI, read a byte using the
        # gyro-specific I2C address or SPI CS pin.
        if self._io_mode == INTERFACE_MODE.MODE_I2C:
            rdata = self._i2c_read_bytes(self._gyro_addr, reg_addr, count)
        elif self._io_mode == INTERFACE_MODE.MODE_SPI:
            rdata = self._spi_read_bytes(self._gyro_addr, reg_addr, count)
        else:
            raise ValueError("Illegal iomode (%r)" % self._io_mode)
        return rdata

    def _gyro_write_byte(self, reg_addr, val):
        """
            Write a byte to a register in the gyroscope.

            Input:
                reg_addr - Register to be written to.
                val - data value to be written to the register.
        """
        # Whether we're using I2C or SPI, write a byte using the
        # gyro-specific I2C address or SPI CS pin.
        if self._io_mode == INTERFACE_MODE.MODE_I2C:
            self._i2c_write_byte(self._gyro_addr, reg_addr, val)
        elif self._io_mode == INTERFACE_MODE.MODE_SPI:
            self._spi_write_byte(self._gyro_addr, reg_addr, val)
        else:
            raise ValueError("Illegal iomode (%r)" % self._io_mode)

        return

    def _xm_read_byte(self, reg_addr):
        """
            Reads a byte from a specified accelerator/magnetometer register.

            Input:
                reg_addr - Register to be read from.

            Output:
                An 8-bit value read from the requested address.
        """
        rtn_val = None

        # Whether we're using I2C or SPI, read a byte using the
        # gyro-specific I2C address or SPI CS pin.
        if self._io_mode == INTERFACE_MODE.MODE_I2C:
            rtn_val = self._i2c_read_byte(self._xm_addr, reg_addr)
        elif self._io_mode == INTERFACE_MODE.MODE_SPI:
            rtn_val = self._spi_read_byte(self._xm_addr, reg_addr)
        else:
            raise ValueError("Illegal iomode (%r)" % self._io_mode)

        return rtn_val

    def _xm_read_bytes(self, reg_addr, count):
        """
            Reads a number of bytes -- beginning at an address and incrementing from there -- from the accelerator/magnetometer.

            Input:
                reg_addr - Register to be read from.
                count - The number of bytes to be read.

            Output: A buffer with the returned data
        """
        rdata = None
        # Whether we're using I2C or SPI, read a byte using the
        # gyro-specific I2C address or SPI CS pin.
        if self._io_mode == INTERFACE_MODE.MODE_I2C:
            rdata = self._i2c_read_bytes(self._xm_addr, reg_addr, count)
        elif self._io_mode == INTERFACE_MODE.MODE_SPI:
            rdata = self._spi_read_bytes(self._xm_addr, reg_addr, count)
        else:
            raise ValueError("Illegal iomode (%r)" % self._io_mode)

        return rdata

    def _xm_write_byte(self, reg_addr, val):
        """
            Write a byte to a register in the accelerator/magnetometer.

            Input:
                reg_addr - Register to be written to.
                val - data value to be written to the register.
        """
        # Whether we're using I2C or SPI, write a byte using the
        # gyro-specific I2C address or SPI CS pin.
        if self._io_mode == INTERFACE_MODE.MODE_I2C:
            self._i2c_write_byte(self._xm_addr, reg_addr, val)
        elif self._io_mode == INTERFACE_MODE.MODE_SPI:
            self._spi_write_byte(self._xm_addr, reg_addr, val)
        else:
            raise ValueError("Illegal iomode (%r)" % self._io_mode)
        return

    def _init_i2c(self):
        """
            Initialize the I2C hardware. This function will setup all I2C pins and related hardware.
        """
        self._iocls = mraa.I2c(I2C_BUS)
        return

    def _init_spi(self):
        """
            Initialize the SPI hardware. This function will setup all SPI pins and related hardware.
        """
        self._iocls = mraa.Spi(0)

        # Maximum SPI frequency is 10MHz, lets set the frequency to 4MHz:
        self._iocls.frequency(SPI_CLOCK_4MHZ)

        # Data is read and written MSb first.
        self._iocls.lsbmode(False)

        # Data is captured on rising edge of clock (CPHA = 0)
        # Base value of the clock is HIGH (CPOL = 1)
        self._iocls.setDataMode(SPI_MODE1)

        return

    def _i2c_write_byte(self, dev_addr, reg_addr, val):
        """
            Write a byte out of I2C to a register in the device

            Input:
                address - The 7-bit I2C address of the slave device.
                reg_addr - The register to be written to.
                val - Byte to be written to the register.
        """
        self._iocls.address(dev_addr) # Initialize the Tx buffer
        self._iocls.writeReg(reg_addr, val) # write the data to the specified register
        return

    def _i2c_read_byte(self, dev_addr, reg_addr):
        """
            Read a single byte from a register over I2C.

            Input:
                address - The 7-bit I2C address of the slave device.
                reg_addr - The register to be read from.

            Output:
                The byte read from the requested address.
        """
        self._iocls.address(dev_addr) # Initialize the Tx buffer
        rbyte = self._iocls.readReg(reg_addr)
        return rbyte

    def _i2c_read_bytes(self, dev_addr, reg_addr, count):
        """
            Read a series of bytes, starting at a register via SPI

            Input:
                address - The 7-bit I2C address of the slave device.
                reg_addr - The register to begin reading.
                buffer - Pointer to an array where we'll store the readings.
                count - Number of registers to be read.

            Output:
                No value is returned by the function, but the registers read are all stored in the
                'buffer' array given.
        """
        self._iocls.address(dev_addr) # Initialize the Tx buffer
        rdata = self._iocls.readBytesReg(reg_addr | 0x80, count)
        return rdata

    def _spi_write_byte(self, cs_pin, reg_addr, val):
        """
            Write a byte out of SPI to a register in the device

            Input:
                cs_pin - The chip select pin of the slave device.
                reg_addr - The register to be written to.
                val - Byte to be written to the register.
        """
        #digitalWrite(cs_pin, LOW)  # Initiate communication

        # If write, bit 0 (MSB) should be 0
        # If single write, bit 1 should be 0
        self._iocls.writeByte(reg_addr & 0x3F); # Send Address
        self._iocls.writeByte(val) # Send data

        #digitalWrite(csPin, HIGH); # Close communication
        return

    def _spi_read_byte(self, cs_pin, reg_addr):
        """
            Read a single byte from a register over SPI.

            Input:
                cs_pin - The chip select pin of the slave device.
                reg_addr - The register to be read from.

            Output:
                The byte read from the requested address.
        """
        return

    def _spi_read_bytes(self, cs_pin, reg_addr, buff, count):
        """
            Read a series of bytes, starting at a register via SPI

            Input:
                cs_pin - The chip select pin of a slave device.
                reg_addr - The register to begin reading.
                buffer - Pointer to an array where we'll store the readings.
                count - Number of registers to be read.

            Output:
                No value is returned by the function, but the registers read are all stored in the
                'buffer' array given.
        """
        return


if __name__ == "__main__":
    import sys
    import time

    msensor = LSM9DS0()
    msensor.begin(accel_scale=ACCEL_SCALE.A_SCALE_16G, mag_scale=MAG_SCALE.M_SCALE_12GS)

    display = "pr"
    if len(sys.argv) > 1:
        display = sys.argv[1]

    if display == "a":
        while True:
            msensor.read_accel()
            rx = msensor.ax
            ry = msensor.ay
            rz = msensor.az
            cx = msensor.calc_accel(rx)
            cy = msensor.calc_accel(ry)
            cz = msensor.calc_accel(rz)
            res = msensor.accel_res
            print "ax=%06d, ay=%06d, az=%06d, cx=%05f, cy=%05f, cz=%05f, res=%f" % (rx, ry, rz, cx, cy, cz, res)
            time.sleep(.2)
    elif display == "g":
        while True:
            msensor.read_gyro()
            rx = msensor.gx
            ry = msensor.gy
            rz = msensor.gz
            cx = msensor.calc_gyro(rx)
            cy = msensor.calc_gyro(ry)
            cz = msensor.calc_gyro(rz)
            res = msensor.gyro_res
            print "gx=%06d, gy=%06d, gz=%06d, cx=%05f, cy=%05f, cz=%05f, res=%f" % (rx, ry, rz, cx, cy, cz, res)
            time.sleep(.2)
    elif display == "m":
        while True:
            msensor.read_mag()
            rx = msensor.mx
            ry = msensor.my
            rz = msensor.mz
            cx = msensor.calc_mag(rx)
            cy = msensor.calc_mag(ry)
            cz = msensor.calc_mag(rz)
            res = msensor.mag_res
            print "mx=%06d, my=%06d, mz=%06d, cx=%05f, cy=%05f, cz=%05f, res=%f" % (rx, ry, rz, cx, cy, cz, res)
            time.sleep(.2)
    else:
        while True:
            msensor.update_complimentary_filter()
            gx = msensor.gx
            gy = msensor.gy
            gz = msensor.gz
            ax = msensor.ax
            ay = msensor.ay
            az = msensor.az
            pitch = msensor.pitch
            roll = msensor.roll

            print "gx=%06d, gy=%06d, gz=%06d, ax=%06d, ay=%06d, az=%06d, p=%f, r=%f" % (gx, gy, gz, ax, ay, az, pitch, roll)
            time.sleep(SAMPLE_RATE)


