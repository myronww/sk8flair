
import mraa
import time

PIN_I2C_SCL = 7
PIN_I2C_SDA = 19


test_pin = mraa.Gpio(PIN_I2C_SCL)

val = 0

while True:
    test_pin.write(val)
    val = (val + 1) % 2
    now = time.time()