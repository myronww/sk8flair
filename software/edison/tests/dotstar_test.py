

import mraa
import traceback
import time

DATA_PIN = 35
CLOCK_PIN = 26

LED_COUNT = 16

class LightPaletteSolid():
    def __init__(self, led_count):
        self._led_count = led_count
        self._buffer = bytearray(led_count * 4)
        self.clear()
        return

    @property
    def buffer(self):
        return self._buffer

    @property
    def led_count(self):
        return self._led_count

    def clear(self):
        self.fill( 0, 0, 0)
        return

    def fill(self, red, green, blue):
        buffer = self._buffer

        for led_index in xrange(self._led_count):
            buff_index = led_index * 4
            buffer[buff_index] = 0xFF
            buffer[buff_index + 1] = blue  # BLUE
            buffer[buff_index + 2] = green # GREEN
            buffer[buff_index + 3] = red   # RED

        return


class DotStarDevice():
    """
        The strip expects the colors to be sent in BGR order.  They are sent in a buffer
        with as a set of 4 byts per pixel ( 0xFF/B/G/R )
    """

    INTERVAL_20_USEC = .000020
    INTERVAL_80_USEC = .000080

    def __init__(self, data_pin=DATA_PIN, clock_pin=CLOCK_PIN):

        self._data_pin = mraa.Gpio(data_pin)
        self._data_pin.dir(mraa.DIR_OUT)

        self._clock_pin = mraa.Gpio(clock_pin)
        self._clock_pin.dir(mraa.DIR_OUT)

        return

    def render_buffer(self, buffer):
        self._writeBuffer(buffer)
        return

    def _counter_cycle(self):
        """
            This function is setup for timing so that the write, cycle and counter/clock cycle
            will have similar duration so that the output clock and data wave will be very
            close to a square wave.

            The function clocks the top cycle of the output clock at 300 usec
        """
        self._clock_pin.write(1) # Appx 60 usec

        # Go figure, getting the time takes a very
        # short but regular interval of time
        time.sleep(self.INTERVAL_80_USEC) # Appx 180 usec

        self._clock_pin.write(0) # Appx 60 usec
        return

    def _write_cycle(self, val):
        """
            This function is setup for timing so that the write, cycle and counter/clock cycle
            will have similar duration so that the output clock and data wave will be very
            close to a square wave.

            We also want to make sure the data write happens in the middle of the down cycle
            of the clock.

            The function clocks the top cycle of the output clock at 300 usec
        """
        time.sleep(self.INTERVAL_20_USEC)  # Appx 120 usec
        self._data_pin.write(val)            # Appx 60  usec
        time.sleep(self.INTERVAL_20_USEC)  # Appx 120 usec
        return

    def _writeBuffer(self, buffer):
        """
            Writes the buffer to the output
        """
        # Write out the fixed size start frame
        self._write_frame_start()

        for byte in buffer:
            nextWrite = 1 if byte & 0x80 else 0
            self._write_cycle(nextWrite)
            self._counter_cycle()

            nextWrite = 1 if byte & 0x40 else 0
            self._write_cycle(nextWrite)
            self._counter_cycle()

            nextWrite = 1 if byte & 0x20 else 0
            self._write_cycle(nextWrite)
            self._counter_cycle()

            nextWrite = 1 if byte & 0x10 else 0
            self._write_cycle(nextWrite)
            self._counter_cycle()

            nextWrite = 1 if byte & 0x08 else 0
            self._write_cycle(nextWrite)
            self._counter_cycle()

            nextWrite = 1 if byte & 0x04 else 0
            self._write_cycle(nextWrite)
            self._counter_cycle()

            nextWrite = 1 if byte & 0x02 else 0
            self._write_cycle(nextWrite)
            self._counter_cycle()

            nextWrite = 1 if byte & 0x01 else 0
            self._write_cycle(nextWrite)
            self._counter_cycle()

        # Write out the stop frame to clock out the data to the rest
        # of the pixels in the strip
        clock_count = len(buffer) >> 3
        self._write_frame_stop(clock_count)

        return

    def _write_frame_start(self):
        """
            A minimum of 32 zeroes are required to initiate an update. Increasing the number of zeroes does not have any
        impact. The LED frame is identified by the first one bit following the start frame.

        The LED output color is updated immediately after the first valid LED frame. This is quite interesting, since
        it means that almost arbitrary update rates of the APA102 are possible. However, this may lead to a 'staggered'
        update for longer strings, where the first LEDs in a string are updated earlier than the later ones. The best
        way to work around this is to use a sufficiently high SPI clock rate.
        """
        for bit in xrange(32):
            self._write_cycle(0)
            self._counter_cycle()
        return

    def _write_frame_stop(self, clock_count):
        """
            The function of the 'End frame' is to supply more clock pulses to the string until the data has permeated to
        the last LED. The number of clock pulses required is exactly half the total number of LEDs in the string. The
        recommended end frame length of 32 is only sufficient for strings up to 64 LEDs. This was first pointed out by
        Bernd in a comment. It should not matter, whether the end frame consists of ones or zeroes. Just don't mix them.

        Furthermore, omitting the end frame will not mean that data from the update is discarded. Instead it will be loaded
        in to the PWM registers at the start of the next update.
        """
        for bit in xrange(clock_count):
            self._write_cycle(0)
            self._counter_cycle()


def dotstar_test_main():
    dstar = DotStarDevice()

    red_palette = LightPaletteSolid(LED_COUNT)
    red_palette.fill(255, 0, 0)

    green_palette = LightPaletteSolid(LED_COUNT)
    green_palette.fill(0, 255, 0)

    blue_palette = LightPaletteSolid(LED_COUNT)
    blue_palette.fill(0, 0, 255)

    while True:
        dstar.render_buffer(blue_palette.buffer)
        time.sleep(.5)

        dstar.render_buffer(green_palette.buffer)
        time.sleep(.5)

        dstar.render_buffer(red_palette.buffer)
        time.sleep(.5)

        dstar.render_buffer(blue_palette.buffer)
        time.sleep(.25)

        dstar.render_buffer(green_palette.buffer)
        time.sleep(.25)

        dstar.render_buffer(red_palette.buffer)
        time.sleep(.25)

    return


if __name__ == "__main__":
    try:
        dotstar_test_main()
    except:
        err_msg = traceback.format_exc()
        print(err_msg)



