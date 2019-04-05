#!/usr/bin/python

from __future__ import absolute_import, print_function, unicode_literals

from optparse import OptionParser

import atexit
import ctypes
import ctypes.util
import json
import logging
import mraa
import os
import resource
import signal
import socket
import subprocess
import sys
import time
import traceback
import uuid

import dbus
import dbus.service
import dbus.mainloop.glib
try:
  from gi.repository import GObject
except ImportError:
  import gobject as GObject


# ========================= CLOCK SET FUNCTIONS ===================
CLOCK_REALTIME = 0
librt = ctypes.CDLL(ctypes.util.find_library("rt"))

class timespec(ctypes.Structure):
    _fields_ = [
        ("tv_sec", ctypes.c_long),
        ("tv_nsec", ctypes.c_long)]

def call_librt_clock_settime(sec, nsec):
    ts = timespec()
    ts.tv_sec = sec
    ts.tv_nsec = nsec

    librt.clock_settime(CLOCK_REALTIME, ctypes.byref(ts))
    return


BLUEZ_BUS_NAME = "org.bluez"
BLUEZ_BUS_PATH = "/org/bluez"

BLUEZ_INTERFACE_AGENTMANAGER1 = "org.bluez.AgentManager1"
BLUEZ_INTERFACE_AGENT1 = "org.bluez.Agent1"
BLUEZ_INTERFACE_DEVICE1 = "org.bluez.Device1"
BLUEZ_INTERFACE_PROFILE1 = "org.bluez.Profile1"
BLUEZ_INTERFACE_PROFILEMANAGER1 = "org.bluez.ProfileManager1"

SK8FLAIR_AGENT_PATH = "/skateflair/agent"
SK8FLAIR_PROFILE_PATH = "/foo/bar/profile"

SK8FLAIR_PAIRING_CAPABILITIES = "NoInputNoOutput" 
SK8FLAIR_PIN_CODE = "12345"

SK8FLAIR_DIR = os.path.abspath(os.path.dirname(__file__))

SK8FLAIR_PROFILES_DIRECTORY = os.path.join(SK8FLAIR_DIR, "profiles")
SK8FLAIR_CURRENT_PROFILE_FILE = os.path.join(SK8FLAIR_DIR, "current_profile.config")
SK8FLAIR_RELAY_FILE = os.path.join(SK8FLAIR_DIR, "sk8flair_relay.sock")
SK8FLAIR_SERVICE_LOGFILE = os.path.join(SK8FLAIR_DIR, "sk8flair_service_d.log")

SK8FLAIR_PID_FILE = "/tmp/Sk8Flair.pid"

SK8FLAIR_SERVICE_LOGGER =  "sk8flair_service_d"

SK8FLAIR_LED_COUNT = 16


# Service Globals
device_obj = None
dev_path = None
mainloop = None

service_logger = None
svclog_stream = None

def ask(prompt):
    try:
        return raw_input(prompt)
    except:
        return input(prompt)

def log_message(message):
    global service_logger
    # print("%s: %s" % (SK8FLAIR_SERVICE_LOGGER, message))
    if not service_logger is None:
        service_logger.log(50, message)
        svclog_stream.flush()
    return

def open_logger(clean_file=False):
    global service_logger
    global svclog_stream

    mode = 'a'
    if clean_file:
        mode = 'w'
    logf = open(SK8FLAIR_SERVICE_LOGFILE, mode)
    svclog_stream = logging.StreamHandler(logf)
    service_logger = logging.getLogger(SK8FLAIR_SERVICE_LOGGER)
    service_logger.addHandler(svclog_stream)
    service_logger.log(50, "Successfully opened the 'sk8flair_service_d' logger.")
    return

def run_command(command_line):
    sproc = subprocess.Popen(command_line,
            stdout = subprocess.PIPE,
            stderr = subprocess.PIPE,
            bufsize = -1, shell = True)
    stdout, stderr = sproc.communicate()
    rtncode = sproc.returncode

    return rtncode, stdout, stderr

def run_command_detached(command_line):
    sproc = subprocess.Popen(command_line, shell=False, stdin=None, stdout=None, stderr=None, close_fds=True)
    return

def set_trusted(path):
    bus = dbus.SystemBus()
    props = dbus.Interface(bus.get_object("org.bluez", path), "org.freedesktop.DBus.Properties")
    props.Set(BLUEZ_INTERFACE_DEVICE1, "Trusted", True)

def sk8flair_app_start():
    sk8flair_app_stop()
    sk8flair_cmd = "/home/root/sk8flair/Sk8Flair"
    run_command_detached(sk8flair_cmd)
    return

def sk8flair_app_stop():
    if os.path.exists(SK8FLAIR_PID_FILE):
        with open(SK8FLAIR_PID_FILE, 'r') as sfpid_f:
            proc_id_str = sfpid_f.read().strip()
            proc_id = int(proc_id_str)
            try:
                os.kill(proc_id, signal.SIGTERM)
            except:
                pass

    time.sleep(.2)
    turn_off_lights()

    return

def skflair_current_profile():
    current_profile = None
    with open(SK8FLAIR_CURRENT_PROFILE_FILE, 'r') as cpf:
        current_profile = cpf.read()
    if current_profile.endswith(".xml"):
        current_profile = current_profile[:-4]
    return current_profile

def sk8flair_list_profiles():
    profiles = []
    for litem in os.listdir(SK8FLAIR_PROFILES_DIRECTORY):
        item_basename, item_ext = os.path.splitext(litem)
        if item_ext == ".xml":
            profiles.append(item_basename)
    return profiles

def turn_off_lights():
    dstar = DotStarDevice()
    off_palette = LightPaletteSolid(SK8FLAIR_LED_COUNT)
    dstar.render_buffer(off_palette.buffer)
    del off_palette
    del dstar
    return

def wpacli_command(cmd, expect="OK"):
    log_message("RUNNING - %s" % cmd)
    status, stdout, stderr = run_command(cmd)
    log_message(cmd + " - " + stdout)
    if expect != None and stdout.strip() != expect:
        raise Exception("WPACLI FAIL: %s" % cmd)
    return stdout

def wifi_hotspot(net_ssid, net_security, passkey):

    log_message("disable one time setup")

    # We have to disable the Wifi hotspot before trying to setup a connection
    cmd = "configure_edison --disableOneTimeSetup"
    status, stdout, stderr = run_command(cmd)
    if status != 0:
        raise Exception("wifi_hotspot: %s" % stderr)

    time.sleep(5)

    log_message("start wpa_supplicant")

    cmd = "systemctl start wpa_supplicant"
    status, stdout, stderr = run_command(cmd)
    if status != 0:
        raise Exception("wifi_hotspot: %s" % stderr)

    wpacli_command("wpa_cli -i wlan0 disconnect");
    wpacli_command("wpa_cli -i wlan0 remove_network all");
    wpacli_command('wpa_cli -i wlan0 add_network', "0")
    wpacli_command('wpa_cli -i wlan0 set_network 0 mode 0')
    wpacli_command('wpa_cli -i wlan0 set_network 0 ssid \'"%s"\'' % net_ssid)
    wpacli_command("wpa_cli -i wlan0 set_network 0 auth_alg OPEN")
    if net_security == "OPEN":
        wpacli_command("wpa_cli -i wlan0 set_network 0 key_mgmt NONE")
    elif net_security == "WPA2":
        wpacli_command('wpa_cli -i wlan0 set_network 0 key_mgmt WPA-PSK')
        wpacli_command('wpa_cli -i wlan0 set_network 0 proto RSN')
        wpacli_command('wpa_cli -i wlan0 set_network 0 psk \'"%s"\'' % passkey)
    else:
        raise Exception("HOTSPOT: Invalid security mode '%s'" % net_security)

    wpacli_command("wpa_cli -i wlan0 set_network 0 scan_ssid 1")
    wpacli_command("wpa_cli -i wlan0 select_network 0")

    wpacli_command("wpa_cli -i wlan0 enable_network 0")
    wpacli_command("wpa_cli -i wlan0 reassociate")

    wlan_status_cmd = "wpa_cli -i wlan0 status | grep wpa_state"
    now = time.time()
    end = now + 60
    while True:
        status, stdout, stderr = run_command(wlan_status_cmd)
        if stdout.find("COMPLETED") > -1:
            log_message(wlan_status_cmd + " - PASSED")
            log_message(stdout)
            break
        time.sleep(5)
        now = time.time()
        if now > end:
            raise Exception("HOTSPOT: Timeout waiting for wifi connection.")

    addr_ip = "0.0.0.0"
    status, stdout, stderr = run_command("ifconfig wlan0 | grep 'inet addr:'")
    if status == 0:
        addr_content = stdout.strip().replace("inet addr:", "").replace("Bcast:", "").replace("Mask:", "")
        addr_components  = addr_content.split(" ")
        addr_ip = addr_components[0]

    if addr_ip == "0.0.0.0":
        status, stdout, stderr = run_command("ifconfig wlan0")
        log_message(stdout)

    return addr_ip

def wifi_off():
    cmd = "rfkill block wifi"
    run_command(cmd)
    return

def wifi_on():
    cmd = "rfkill unblock wifi"
    run_command(cmd)

    cmd = "configure_edison --enableOneTimeSetup --persist"
    run_command(cmd)
    return

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

# GP130 - 61 - 2 (clock) - 26
# GP131 - 46 - 3 (data) - 35
DATA_PIN = 35
CLOCK_PIN = 26

FLAIR_CLASS = {
    "flair-type": "sk8flair", 
    "plugins": ["solid", "wheel", "compass"],
}

FLAIR_CLASS_JSON = json.dumps(FLAIR_CLASS)

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


class Rejected(dbus.DBusException):
    _dbus_error_name = "org.bluez.Error.Rejected"

class GoodbyeError(Exception):
    def __init__(self, message, *args):
        Exception.__init__(self, message, *args)
        return

class Sk8FlairAgent(dbus.service.Object):

    def set_exit_on_release(self, exit_on_release):
        self.exit_on_release = exit_on_release

    @dbus.service.method(BLUEZ_INTERFACE_AGENT1, in_signature="", out_signature="")
    def Release(self):
        log_message("Sk8FlairAgent - Release called")
        return

    @dbus.service.method(BLUEZ_INTERFACE_AGENT1, in_signature="os", out_signature="")
    def AuthorizeService(self, device, uuid):
        log_message("AuthorizeService (%s, %s)" % (device, uuid))
        #authorize = ask("Authorize connection (yes/no): ")
        #if (authorize == "yes"):
        #    return
        #raise Rejected("Connection rejected by user")
        return

    @dbus.service.method(BLUEZ_INTERFACE_AGENT1, in_signature="o", out_signature="s")
    def RequestPinCode(self, device):
        log_message("RequestPinCode (%s)" % (device))
        try:
            set_trusted(device)
        except:
            err_msg = traceback.format_exc()
            log_message(err_msg)
        return SK8FLAIR_PIN_CODE

    @dbus.service.method(BLUEZ_INTERFACE_AGENT1, in_signature="o", out_signature="u")
    def RequestPasskey(self, device):
        log_message("RequestPasskey (%s)" % (device))
        set_trusted(device)
        #passkey = ask("Enter passkey: ")
        passkey = 0
        return dbus.UInt32(passkey)

    @dbus.service.method(BLUEZ_INTERFACE_AGENT1, in_signature="ouq", out_signature="")
    def DisplayPasskey(self, device, passkey, entered):
        """
            This method gets called when the service daemon
            needs to display a passkey for an authentication.

            The entered parameter indicates the number of already
            typed keys on the remote side.

            An empty reply should be returned. When the passkey
            needs no longer to be displayed, the Cancel method
            of the agent will be called.

            During the pairing process this method might be
            called multiple times to update the entered value.
            
        """
        log_message("DisplayPasskey (%s, %06u entered %u)" % (device, passkey, entered))
        return

    @dbus.service.method(BLUEZ_INTERFACE_AGENT1, in_signature="os", out_signature="")
    def DisplayPinCode(self, device, pincode):
        log_message("DisplayPinCode (%s, %s)" % (device, pincode))
        return

    @dbus.service.method(BLUEZ_INTERFACE_AGENT1, in_signature="ou", out_signature="")
    def RequestConfirmation(self, device, passkey):
        log_message("RequestConfirmation (%s, %06d)" % (device, passkey))
        confirm = ask("Confirm passkey (yes/no): ")
        if (confirm == "yes"):
            set_trusted(device)
            return
        raise Rejected("Passkey doesn't match")
        return

    @dbus.service.method(BLUEZ_INTERFACE_AGENT1, in_signature="o", out_signature="")
    def RequestAuthorization(self, device):
        log_message("RequestAuthorization (%s)" % (device))
        #auth = ask("Authorize? (yes/no): ")
        #if (auth == "yes"):
        #    return
        #raise Rejected("Pairing rejected")
        return

    @dbus.service.method(BLUEZ_INTERFACE_AGENT1, in_signature="", out_signature="")
    def Cancel(self):
        log_message("Sk8FlairAgent - Cancel called")
        return

class Sk8FlairProfile(dbus.service.Object):
    fd = -1
    protocol = "1"

    TEMPLATE_ERROR_EXCEPTION = "ERROR Exception thrown for %r command."
    TEMPLATE_ERROR_INVALID_COMMAND = "ERROR Invalid command %r."
    TEMPLATE_ERROR_INVALID_DATA = "ERROR Invalid data %r passed with %r command."
    TEMPLATE_ERROR_INVALID_REQUEST = "ERROR Invalid request format."

    TEMPLATE_SUCCESS = "SUCCESS %s"

    @dbus.service.method(BLUEZ_INTERFACE_PROFILE1, in_signature="", out_signature="")
    def Release(self):
        log_message("Sk8FlairProfile - Release called")

    @dbus.service.method(BLUEZ_INTERFACE_PROFILE1, in_signature="", out_signature="")
    def Cancel(self):
        log_message("Sk8FlairProfile - Cancel called")

    @dbus.service.method(BLUEZ_INTERFACE_PROFILE1, in_signature="oha{sv}", out_signature="")
    def NewConnection(self, path, fd, properties):
        try:
            self.fd = fd.take()
            log_message("Sk8FlairProfile - NewConnection(%s, %d)" % (path, self.fd))

            server_sock = socket.fromfd(self.fd, socket.AF_UNIX, socket.SOCK_STREAM)
            try:
                server_sock.setblocking(1)
                server_sock.send("WAITING")

                while True:
                    req_reply = self.TEMPLATE_ERROR_INVALID_REQUEST
                    req_buffer = server_sock.recv(4096)

                    log_message("Sk8FlairProfile - buffer received '%s'" % str(req_buffer))

                    cmd_parts = req_buffer.split(" ", 1)

                    if len(cmd_parts) > 0:
                        cmd_word = cmd_parts[0]
                        cmd_data = None
                        if len(cmd_parts) > 1:
                            cmd_data = cmd_parts[1]

                        if cmd_word.find("/") > -1:
                            req_reply = self.__process_request(cmd_word, cmd_data, req_buffer)
                        else:
                            req_reply = self.__process_legacy_request(cmd_word, cmd_data, req_buffer)
                    else:
                        req_reply = "ERROR 'Empty or Invalid request.'"

                    server_sock.send(req_reply)
            except GoodbyeError as goodbye:
                pass
            except:
                err_msg = traceback.format_exc()
                server_sock.send(err_msg + "\n")
                log_message(err_msg)
            finally:
                server_sock.close()
        except:
            err_msg = traceback.format_exc()
            log_message(err_msg)

        return

    @dbus.service.method(BLUEZ_INTERFACE_PROFILE1, in_signature="o", out_signature="")
    def RequestDisconnection(self, path):
        log_message("RequestDisconnection(%s)" % (path))
        if (self.fd > 0):
            os.close(self.fd)
        self.fd = -1

    def __process_legacy_request(self, cmd_word, cmd_data, req_buffer):
        # We will send back a success reply unless a failure occurs
        req_reply = self.TEMPLATE_SUCCESS % cmd_word

        try:
            if (cmd_word == "CONFIG"):
                config_file, config_content = cmd_data.split(" ", 1)
                config_file_full = os.path.join(SK8FLAIR_PROFILES_DIRECTORY, config_file)
                with open(config_file_full, 'w') as configf:
                    configf.write(config_content)
            elif (cmd_word == "FLAIRINFO"):
                req_reply = FLAIR_CLASS_JSON
            elif (cmd_word == "FLAIRPLUGINS"):
                req_reply = "solid, wheel, compass"
            elif (cmd_word == "HOTSPOT"):
                cmd_data = cmd_data.strip()
                data_parts = cmd_data.split(" ")

                data_parts_len = len(data_parts)
                if data_parts_len < 1:
                    raise Exception("Invalid args. Must have (SSID) (SMOD) (PASSKEY)")

                ssid = data_parts[0]
                smode = "OPEN"
                passkey = None
                if data_parts_len > 1:
                    smode = data_parts[1]
                if data_parts_len > 2:
                    passkey = data_parts[2]

                ip_addr = wifi_hotspot(ssid, smode, passkey)
                req_reply = ip_addr

            elif (cmd_word == "LIGHTS"):
                cmd_data = cmd_data.strip()
                if cmd_data == "OFF":
                    sk8flair_app_stop()
                elif cmd_data == "ON":
                    sk8flair_app_start()
                else:
                    req_reply = self.TEMPLATE_ERROR_INVALID_DATA % (cmd_data, cmd_word)
            elif (cmd_word == "REBOOT"):
                run_command("reboot")
            elif (cmd_word == "RESET"):
                sk8flair_app_stop()
                time.sleep(1)
                sk8flair_app_start()
            elif (cmd_word == "SHELL"):
                cmd_data = cmd_data.strip()
                if cmd_data.startswith("XANIHPX") and cmd_data.endswith("XANIHPX"):
                    cmd_data = cmd_data.lstrip("XANIHPX").rstrip("XANIHPX")
                    rtncode, stdout, stderr = run_command(cmd_data)
                    if rtncode == 0:
                        req_reply = "(%d)\n%s" % (rtncode, stdout)
                    else:
                        req_reply = "(%d)\n%s" % (rtncode, stderr)
            elif (cmd_word == "WIFI"):
                cmd_data = cmd_data.strip()
                if cmd_data == "OFF":
                    wifi_off()
                elif cmd_data == "ON":
                    wifi_on()
                else:
                    req_reply = self.TEMPLATE_ERROR_INVALID_DATA % (cmd_data, cmd_word)
            elif (cmd_word == "PROFILES"):
                profile_list = sk8flair_list_profiles()
                current_profile = skflair_current_profile()
                profile_content = " %s %s" % (current_profile, ",".join(profile_list))
                req_reply = self.TEMPLATE_SUCCESS % cmd_word + profile_content
            elif (cmd_word == "TIMESET"):
                sec_comp, nano_comp = cmd_data.strip().split(" ")
                time_sec = long(sec_comp)
                time_nano = long(nano_comp)
                call_librt_clock_settime(time_sec, time_nano)
            elif (cmd_word == "CALIBRATE") or \
                 (cmd_word == "ECHO") or \
                 (cmd_word == "PROFILE") or \
                 (cmd_word == "RECORD"):
               req_reply = self.__relay_request(req_buffer)
            elif (cmd_word == "GOODBYE"):
               raise GoodbyeError("GOODBYE")
            else:
               req_reply = self.TEMPLATE_ERROR_INVALID_COMMAND % cmd_word
        except:
            req_reply = self.TEMPLATE_ERROR_EXCEPTION % cmd_word
            raise

        return req_reply

    def __process_request(self, leaf_url, content):
        
        return

    def __relay_request(self, req_buffer):
        """
            A request was received that needs to be forwarded to the Sk8Flair application
        """
        relay_sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)

        reply_data = "ERROR REPLY-NOT-SET"

        try:
            relay_sock.connect(SK8FLAIR_RELAY_FILE)

            try:
                log_message("SENDING: %s" % req_buffer)

                relay_sock.sendall(req_buffer)

                reply_data = relay_sock.recv(1024)
            except IOError as ioerr:
                reply_data = "ERROR IOERROR - %r" % ioerr

        except:
            err_msg = traceback.format_exc()
            log_message(err_msg)
            reply_data = "ERROR %s" % err_msg

        finally:
            relay_sock.close()

        return reply_data

class SkateFlairService(object):

    #Default maximum for the number of available file descriptors
    DEFAULT_MAXFD = 1024

    #Default file mode creation mask for the daemon
    DEFAULT_UMASK = 0

    #Default working directory for the daemon
    DEFAULT_ROOTDIR = "/"

    #Default device null
    DEFAULT_DEVNULL = "/dev/null"

    SERVICE_PID_FILE = "/tmp/sk8flair_service_d.pid"

    PROFILE_UUID = "1101"
    PROFILE_ARGS = {
        "AutoConnect": False,
        "Name": "Flair Service",
        "Role": "server",
        "PSM": dbus.UInt16("3"),
        "Service": "flair/sk8flair"
    }

    def __init__(self):
        """
            This is a generic daemon class intended to make it easy to create a daemon.  A daemon has the following configurable
            behaviors:

                1. Resets the current working directory to '/'
                2. Resets the current file creation mode mask to 0
                3. Closes all open files (1024)
                4. Detaches from the starting terminal and redirects standard I/O streams to '/dev/null'

            References:
                1. Advanced Programming in the Unix Environment: W. Richard Stevens
            """
        self._pid_file = self.SERVICE_PID_FILE

        self._profile_uuid = self.PROFILE_UUID
        self._profile_args = self.PROFILE_ARGS
        self._daemon_logfile = None

        return

    def daemonize(self):
        """
            This method detaches the current process from the controlling terminal and forks
            it to a process that is a background daemon process.
        """
        self._daemon_logfile = "/tmp/sk8flair_service_d.log"
        if os.path.exists(self._daemon_logfile):
            os.remove(self._daemon_logfile)

        proc_id = None
        try:
            # Fork the 'FIRST' child process and let the parent process where (pid > 0) exit cleanly and return
            # to the terminal
            proc_id = os.fork()
        except OSError as os_err:
            err_msg = "%s\n    errno=%d\n" % (os_err.strerror, os_err.errno)
            log_message(err_msg)
            raise Exception (err_msg)

        # Fork returns 0 in the child and a process id in the parent.  If we are running in the parent
        # process then exit cleanly with no error.
        if proc_id > 0:
            sys.exit(0)

        # Call os.setsid() to:
        #    1. Become the session leader of this new session
        #    2. Become the process group leader of this new process group
        #    3. This also guarantees that this process will not have controlling terminal
        os.setsid()

        proc_id = None
        try:
            # For the 'SECOND' child process and let the parent process where (proc_id > 0) exit cleanly
            # This second process fork has the following effects:
            #     1. Since the first child is a session leader without controlling terminal, it is possible
            #        for it to acquire one be opening one in the future.  This second fork guarantees that
            #        the child is no longer a session leader, preventing the daemon from ever acquiring a
            #        controlling terminal.
            proc_id = os.fork()
        except OSError as os_err:
            err_msg = "%s\n    errno=%d\n" % (os_err.strerror, os_err.errno)
            log_message(err_msg)
            raise Exception (err_msg)

        # Fork returns 0 in the child and a process id in the parent.  If we are running in the parent
        # process then exit cleanly with no error.
        if proc_id > 0:
            sys.exit(0)

        log_message("Second fork successful.")

        # We want to change the working directory of the daemon to '/' to avoid the issue of not being
        # able to unmount the file system at shutdown time.
        os.chdir(SkateFlairService.DEFAULT_ROOTDIR)

        # We don't want to inherit the file mode creation flags from the parent process.  We
        # give the child process complete control over the permissions
        os.umask(SkateFlairService.DEFAULT_UMASK)

        maxfd = resource.getrlimit(resource.RLIMIT_NOFILE)[1]
        if (maxfd == resource.RLIM_INFINITY):
            maxfd = SkateFlairService.DEFAULT_MAXFD

        stdin_fileno = sys.stdin.fileno()
        stdout_fileno = sys.stdout.fileno()
        stderr_fileno = sys.stderr.fileno()

        # Go through all the file descriptors that could have possibly been open and close them
        # This includes the existing stdin, stdout and stderr
        sys.stdout.flush()
        sys.stderr.flush()

        # Close all 1024 possible open FDs
        for fd in xrange(maxfd):
            try:
                os.close(fd)
            except OSError as os_err:
                pass
            except:
                err_trace = traceback.format_exc()

        # Create the standard file descriptors and redirect them to the standard file descriptor
        # numbers 0 stdin, 1 stdout, 2 stderr
        stdin_f = file(SkateFlairService.DEFAULT_DEVNULL , 'r')
        stdout_f = file(SkateFlairService.DEFAULT_DEVNULL, 'a+')
        stderr_f = file(SkateFlairService.DEFAULT_DEVNULL, 'a+')

        os.dup2(stdin_f.fileno(), stdin_fileno)
        os.dup2(stdout_f.fileno(), stdout_fileno)
        os.dup2(stderr_f.fileno(), stderr_fileno)

        # Register an the removal of the PID file on python interpreter exit
        atexit.register(self._remove_pidfile)

        # Create the pid file to prevent multiple launches of the daemon
        pid_str = str(os.getpid())
        with open(self._pid_file, 'w') as pid_f:
            pid_f.write("%s\n" % pid_str)

        return

    def monitor_loop(self):
        """
            This method monitors the Sk8Flair app and makes sure it is always running
        """
        #TODO: Implement this method
        return

    def process_exists(self, process_id):
        """
            Checks to see if a process exists
        """
        result = None
        try:
            os.kill(process_id, 0)
            result = True
        except OSError:
            result = False
        return result

    def restart(self):
        """
            Restart the Skate Flair Service
        """
        self.stop()
        self.start()
        return

    def run(self):
        """
            This is the main 'Skate Flair Service' entry method that will setup the DBUS interfaces
            to handle the Bluetooth integration for the service.
        """
        turn_off_lights()

        nxt_cmd = "/usr/sbin/rfkill unblock bluetooth"
        rtncode, stdout, stderr = run_command(nxt_cmd)
        if rtncode != 0:
            log_message("'%s' command failed. rtncode=%d" % (nxt_cmd, rtncode))
        time.sleep(1)

        nxt_cmd = "/usr/bin/hciconfig hci0 up"
        run_command(nxt_cmd)
        if rtncode != 0:
            log_message("'%s' command failed. rtncode=%d" % (nxt_cmd, rtncode))
        time.sleep(3)

        nxt_cmd = "/usr/bin/hciconfig hci0 piscan"
        run_command(nxt_cmd)
        if rtncode != 0:
            log_message("'%s' command failed. rtncode=%d" % (nxt_cmd, rtncode))
        time.sleep(2)

        nxt_cmd = "/usr/bin/hciconfig hci0 sspmode 0"
        run_command(nxt_cmd)
        if rtncode != 0:
            log_message("'%s' command failed. rtncode=%d" % (nxt_cmd, rtncode))

        sk8flair_app_start()
        log_message("Started 'Sk8Flair' application and detached.")

        dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)

        bus = dbus.SystemBus()

        if bus != None:
            agent = Sk8FlairAgent(bus, SK8FLAIR_AGENT_PATH)
            profile = Sk8FlairProfile(bus, SK8FLAIR_PROFILE_PATH)

            bluez_obj = bus.get_object(BLUEZ_BUS_NAME, BLUEZ_BUS_PATH)

            if bluez_obj != None:
                mainloop = GObject.MainLoop()

                try:
                    profile_manager = dbus.Interface(bluez_obj, BLUEZ_INTERFACE_PROFILEMANAGER1)

                    profile_manager.RegisterProfile(SK8FLAIR_PROFILE_PATH, self._profile_uuid, self._profile_args)
                    log_message("RFCOMM serial profile registered.")

                    try:
                        agent_manager = dbus.Interface(bluez_obj, BLUEZ_INTERFACE_AGENTMANAGER1)

                        resp = agent_manager.RegisterAgent(SK8FLAIR_AGENT_PATH, SK8FLAIR_PAIRING_CAPABILITIES)
                        log_message("Pairing agent registered. resp=%r" % resp)

                        resp = agent_manager.RequestDefaultAgent(SK8FLAIR_AGENT_PATH)
                        log_message("Pairing agent set as default. resp=%r" % resp)

                        # This is where our thread enters our GObject dispatching loop
                        log_message("Starting 'Skate Flair Service' DBUS main loop.")
                        mainloop.run()

                        log_message("Main loop exited normally.")

                    except:
                        err_msg = traceback.format_exc()
                        log_message(err_msg)

                    finally:
                        agent_manager.UnregisterAgent(SK8FLAIR_AGENT_PATH)
                        log_message("Pairing agent unregistered.")

                finally:
                        profile_manager.UnregisterProfile(SK8FLAIR_PROFILE_PATH)
                        log_message("RFCOMM serial profile unregistered.")

            else:
                log_message("Unable to open the BlueZ bus.")

        else:
            log_message("Unable to open DBUS system bus.")

        return

    def start(self):
        """
            Start the Skate Flair Service
        """

        #Check to see if the pid file exists to see if the daemon is already running
        proc_id = None
        try:
            with open(self._pid_file, 'r') as pid_f:
                proc_id_str = pid_f.read().strip()
                proc_id = int(proc_id_str)

            # If we found a PID file but the process in the PID file does not exists,
            # then we are most likely reading a stale PID file.  Go ahead and startup
            # a new instance of the daemon
            if not self.process_exists(proc_id):
                os.remove(self._pid_file)
                proc_id = None

        except IOError:
            proc_id = None

        if proc_id != None:
            log_message("The 'Skate Flair Service' was already running.")
            sys.exit(1)

        # Start the daemon
        log_message("The 'Skate Flair Service' is about to become a daemon.")
        self.daemonize()
 
        # Now that we are a daemon we need to switch over to using the logger
        open_logger()

        log_message("The 'Skate Flair Service' is now a daemon, lets run with it.")
        self.run()

        return

    def stop(self):
        """
            Stop the Skate Flair Service
        """

        #Check to see if the PID file exists to see if the daemon is already running
        proc_id = None
        try:
            with open(self._pid_file, 'r') as pid_f:
                proc_id_str = pid_f.read().strip()
                proc_id = int(proc_id_str)
        except IOError:
            proc_id = None

        if not proc_id:
            log_message("The 'Skate Flair Service' was not running.")
            return # This is not an error in a restart so don't exit with a error code

        # The process was running so we need to shut it down
        try:
            while True:
                os.kill(proc_id, signal.SIGTERM)
                time.sleep(0.1)
        except OSError as os_err:
            err_str = str(os_err)
            if err_str.find("No such process") > 0:
                if os.path.exists(self._pid_file):
                    os.remove(self._pid_file)
            else:
                log_message(err_str)
                sys.exit(1)

        sk8flair_app_stop()

        return

    def _remove_pidfile(self):
        if os.path.exists(self._pid_file):
            os.remove(self._pid_file)
        return

if __name__ == '__main__':

    try:
        if len(sys.argv) == 2:
            service = SkateFlairService()
            action_arg = sys.argv[1]
            if action_arg == 'restart':
                service.restart()
            elif action_arg == 'start':
                service.start()
            elif action_arg == 'stop':
                service.stop()
            else:
                sys.stderr.write("Unknown command '%s'.\n" % action_arg)
                sys.stderr.write("usage: %s start|stop|restart\n" % sys.argv[0])
                exit(2)
        else:
            sys.stderr.write("usage: %s start|stop|restart\n" % sys.argv[0])
            exit(2)
        exit(0)
    except SystemExit:
        raise
    except:
        err_trace = traceback.format_exc()
        log_message(err_trace)
        exit(1)