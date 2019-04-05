

import mraa

WHO_AM_I_G           = 0x0F
WHO_AM_I_XM           = 0x0F

# I2C BUS
I2C_BUS = 1

GYRO_ADDRESS = 0x6B
XM_ADDRESS = 0x1D

def i2c_write_byte(i2cio, dev_addr, reg_addr, val):
    """
        Write a byte out of I2C to a register in the device

        Input:
            address - The 7-bit I2C address of the slave device.
            reg_addr - The register to be written to.
            val - Byte to be written to the register.
    """
    i2cio.address(dev_addr) # Initialize the Tx buffer
    i2cio.writeReg(reg_addr, val) # write the data to the specified register
    return

def i2c_read_byte(i2cio, dev_addr, reg_addr):
    """
        Read a single byte from a register over I2C.

        Input:
            address - The 7-bit I2C address of the slave device.
            reg_addr - The register to be read from.

        Output:
            The byte read from the requested address.
    """
    i2cio.address(dev_addr) # Initialize the Tx buffer
    rbyte = i2cio.readReg(reg_addr)
    return rbyte

if __name__ == "__main__":
    i2cio = mraa.I2c(I2C_BUS)

    while True:
        result = i2c_read_byte(i2cio, GYRO_ADDRESS, WHO_AM_I_G)
        print (" G: %x" % result)

        result = i2c_read_byte(i2cio, XM_ADDRESS, WHO_AM_I_XM)
        print ("XM: %x" % result

        time.sleep(.2)

