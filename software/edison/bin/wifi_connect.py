
import subprocess
import time

def log_message(message):
    print(message)
    return

def run_command(command_line):
    sproc = subprocess.Popen(command_line,
            stdout = subprocess.PIPE,
            stderr = subprocess.PIPE,
            bufsize = -1, shell = True)
    stdout, stderr = sproc.communicate()
    rtncode = sproc.returncode

    return rtncode, stdout, stderr

def wpacli_command(cmd, expect="OK"):
    log_message("RUNNING: %s" % cmd)
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

    log_message("Sleeping for 5 seconds")
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

    return addr_ip


ip_addr =  wifi_hotspot("Pattisons-West", "WPA2", "rollerskate")
#ip_addr =  wifi_hotspot("WISPRED2", "WPA2", "EnterWisp66&&")

print (ip_addr)
