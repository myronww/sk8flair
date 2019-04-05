PCBNEW-LibModule-V1  Saturday, December 20, 2014 'PMt' 01:39:33 PM
# encoding utf-8
Units mm
$INDEX
MAXIM:MAXIM T1433-2
SMD_Packages:SM0603
SMD_Packages:SOT23-5
SMD_Packages:SSOP8
open-project:ATMEL-QFN-64
open-project:CMA-4544PF-W
open-project:CONN_JST-2_SMD
open-project:EPAD
open-project:FTDI-QFN32
open-project:LGA-14
open-project:LGA-16
open-project:MICRO-B_USB
open-project:SMD-MOM-SW-2P
open-project:SMD-XTAL
open-project:SMD_Balum_Filter
open-project:SMD_Chip_Antenna
open-project:SMT_SIDE_SLIDE_SW
open-project:Side-SMT-SW
$EndINDEX
$MODULE MAXIM:MAXIM T1433-2
Po 32.8 33 0 15 53B071A9 00000000 ~~
Li MAXIM:MAXIM T1433-2
Sc 0
AR /539497B9
Op 0 0 0
T0 0 -2.6 0.5 0.5 0 0.1 N I 21 N "U1"
T1 0 2.7 0.5 0.5 0 0.1 N V 21 N "MAX9814"
DC -1 1 -1.2 1 0.15 21
DS -1.5 1.5 1.5 1.5 0.15 21
DS 1.5 1.5 1.5 -1.5 0.15 21
DS 1.5 -1.5 -1.5 -1.5 0.15 21
DS -1.5 -1.5 -1.5 1.5 0.15 21
$PAD
Sh "11" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0 -1.5
$EndPAD
$PAD
Sh "4" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0 1.5
$EndPAD
$PAD
Sh "3" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 27 "Net-(C1-Pad2)"
Po -0.4 1.5
$EndPAD
$PAD
Sh "2" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 3 "+BATT"
Po -0.8 1.5
$EndPAD
$PAD
Sh "1" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 43 "Net-(C6-Pad2)"
Po -1.2 1.5
$EndPAD
$PAD
Sh "5" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 3 "+BATT"
Po 0.4 1.5
$EndPAD
$PAD
Sh "6" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 29 "Net-(C11-Pad1)"
Po 0.8 1.5
$EndPAD
$PAD
Sh "7" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 1.2 1.5
$EndPAD
$PAD
Sh "10" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 85 "Net-(U1-Pad10)"
Po 0.4 -1.5
$EndPAD
$PAD
Sh "9" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0.8 -1.5
$EndPAD
$PAD
Sh "8" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 44 "Net-(C9-Pad1)"
Po 1.2 -1.5
$EndPAD
$PAD
Sh "12" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 28 "Net-(C10-Pad1)"
Po -0.4 -1.5
$EndPAD
$PAD
Sh "13" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 83 "Net-(R3-Pad1)"
Po -0.8 -1.5
$EndPAD
$PAD
Sh "14" R 0.2 0.7 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 78 "Net-(R1-Pad1)"
Po -1.2 -1.5
$EndPAD
$EndMODULE MAXIM:MAXIM T1433-2
$MODULE SMD_Packages:SM0603
Po 24.9 25.3 0 15 53B09244 00000000 ~~
Li SMD_Packages:SM0603
Sc 0
AR /53A15312
Op 0 0 0
At SMD
T0 0 0 0.508 0.4572 0 0.1143 N V 21 N "R15"
T1 0 0 0.508 0.4572 0 0.1143 N I 21 N "1k"
DS -1.143 -0.635 1.143 -0.635 0.127 21
DS 1.143 -0.635 1.143 0.635 0.127 21
DS 1.143 0.635 -1.143 0.635 0.127 21
DS -1.143 0.635 -1.143 -0.635 0.127 21
$PAD
Sh "1" R 0.635 1.143 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 81 "Net-(R15-Pad1)"
Po -0.762 0
$EndPAD
$PAD
Sh "2" R 0.635 1.143 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 46 "Net-(D1-Pad2)"
Po 0.762 0
$EndPAD
$SHAPE3D
Na "smd\\resistors\\R0603.wrl"
Sc 0.5 0.5 0.5
Of 0 0 0.001
Ro 0 0 0
$EndSHAPE3D
$EndMODULE SMD_Packages:SM0603
$MODULE SMD_Packages:SOT23-5
Po 38.65 43.55 0 15 53B0EBE0 00000000 ~~
Li SMD_Packages:SOT23-5
Sc 0
AR /5395516C
Op 0 0 0
At SMD
T0 2.19964 -0.29972 0.635 0.635 900 0.127 N V 21 N "U5"
T1 -0.1 0.1 0.635 0.635 0 0.127 N V 21 N "LD3985G33R"
DS 1.524 -0.889 1.524 0.889 0.127 21
DS 1.524 0.889 -1.524 0.889 0.127 21
DS -1.524 0.889 -1.524 -0.889 0.127 21
DS -1.524 -0.889 1.524 -0.889 0.127 21
$PAD
Sh "1" R 0.508 0.762 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 3 "+BATT"
Po -0.9525 1.27
$EndPAD
$PAD
Sh "3" R 0.508 0.762 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 12 "AXIS-OFF"
Po 0.9525 1.27
$EndPAD
$PAD
Sh "5" R 0.508 0.762 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 1 "+3.3V"
Po -0.9525 -1.27
$EndPAD
$PAD
Sh "2" R 0.508 0.762 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0 1.27
$EndPAD
$PAD
Sh "4" R 0.508 0.762 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 38 "Net-(C23-Pad1)"
Po 0.9525 -1.27
$EndPAD
$SHAPE3D
Na "smd/SOT23_5.wrl"
Sc 0.1 0.1 0.1
Of 0 0 0
Ro 0 0 0
$EndSHAPE3D
$EndMODULE SMD_Packages:SOT23-5
$MODULE SMD_Packages:SSOP8
Po 44.65 45.5 0 15 53B071A9 00000000 ~~
Li SMD_Packages:SSOP8
Sc 0
AR /53958300
Op 0 0 0
At SMD
T0 0 0.508 1.016 1.016 0 0.1524 N V 21 N "U4"
T1 0 -0.762 0.762 0.508 0 0.1524 N I 21 N "TXS0102-QFN"
DC -1.016 1.016 -1.016 0.762 0.1524 21
DS 1.524 1.524 -1.524 1.524 0.1524 21
DS -1.524 1.524 -1.524 -1.524 0.1524 21
DS -1.524 -1.524 1.524 -1.524 0.1524 21
DS 1.524 -1.524 1.524 1.524 0.1524 21
$PAD
Sh "1" R 0.4064 1.27 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 1 "+3.3V"
Po -0.9779 2.2225
$EndPAD
$PAD
Sh "2" R 0.4064 1.27 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 7 "/SDA (3.3v)"
Po -0.3302 2.2225
$EndPAD
$PAD
Sh "3" R 0.4064 1.27 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 6 "/SCL (3.3v)"
Po 0.3302 2.2225
$EndPAD
$PAD
Sh "4" R 0.4064 1.27 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0.9779 2.2225
$EndPAD
$PAD
Sh "5" R 0.4064 1.27 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 12 "AXIS-OFF"
Po 0.9779 -2.2225
$EndPAD
$PAD
Sh "6" R 0.4064 1.27 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 109 "SCL (+BAT)"
Po 0.3302 -2.2225
$EndPAD
$PAD
Sh "7" R 0.4064 1.27 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 110 "SDA (+BAT)"
Po -0.3302 -2.2225
$EndPAD
$PAD
Sh "8" R 0.4064 1.27 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 3 "+BATT"
Po -0.9779 -2.2225
$EndPAD
$SHAPE3D
Na "smd/cms_so8.wrl"
Sc 0.25 0.25 0.25
Of 0 0 0
Ro 0 0 0
$EndSHAPE3D
$EndMODULE SMD_Packages:SSOP8
$MODULE open-project:ATMEL-QFN-64
Po 50.1 33.1 0 15 53B05DFC 00000000 ~~
Li open-project:ATMEL-QFN-64
Sc 0
AR /53A56428
Op 0 0 0
T0 0 -6.3 1 1 0 0.15 N I 21 N "U7"
T1 0 6.2 1 1 0 0.15 N V 21 N "ATMEGA256RFR2"
DS -4.4 0 -4.4 -4.4 0.15 21
DS -4.4 -4.4 4.4 -4.4 0.15 21
DS 4.4 -4.4 4.4 4.4 0.15 21
DS 4.4 4.4 -4.4 4.4 0.15 21
DS -4.4 4.4 -4.4 0 0.15 21
$PAD
Sh "8" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 48 "Net-(FLTR1-Pad4)"
Po -4.25 -0.25
$EndPAD
$PAD
Sh "9" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 47 "Net-(FLTR1-Pad3)"
Po -4.25 0.25
$EndPAD
$PAD
Sh "7" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -4.25 -0.75
$EndPAD
$PAD
Sh "6" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 23 "Net-(ADC1-Pad1)"
Po -4.25 -1.25
$EndPAD
$PAD
Sh "5" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 24 "Net-(ADC2-Pad1)"
Po -4.25 -1.75
$EndPAD
$PAD
Sh "4" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 25 "Net-(ADC3-Pad1)"
Po -4.25 -2.25
$EndPAD
$PAD
Sh "3" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 26 "Net-(ADC4-Pad1)"
Po -4.25 -2.75
$EndPAD
$PAD
Sh "2" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 97 "Net-(U7-Pad2)"
Po -4.25 -3.25
$EndPAD
$PAD
Sh "1" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 89 "Net-(U7-Pad1)"
Po -4.25 -3.75
$EndPAD
$PAD
Sh "10" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -4.25 0.75
$EndPAD
$PAD
Sh "11" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 108 "PROGRAM"
Po -4.25 1.25
$EndPAD
$PAD
Sh "12" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 13 "BRD RESET"
Po -4.25 1.75
$EndPAD
$PAD
Sh "13" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 90 "Net-(U7-Pad13)"
Po -4.25 2.25
$EndPAD
$PAD
Sh "14" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 91 "Net-(U7-Pad14)"
Po -4.25 2.75
$EndPAD
$PAD
Sh "15" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 92 "Net-(U7-Pad15)"
Po -4.25 3.25
$EndPAD
$PAD
Sh "16" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 93 "Net-(U7-Pad16)"
Po -4.25 3.75
$EndPAD
$PAD
Sh "48" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 105 "Net-(U7-Pad48)"
Po 4.25 -3.75
$EndPAD
$PAD
Sh "47" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 56 "Net-(IC1-Pad2)"
Po 4.25 -3.25
$EndPAD
$PAD
Sh "46" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 65 "Net-(IC1-Pad30)"
Po 4.25 -2.75
$EndPAD
$PAD
Sh "45" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 4.25 -2.25
$EndPAD
$PAD
Sh "44" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 3 "+BATT"
Po 4.25 -1.75
$EndPAD
$PAD
Sh "43" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 74 "Net-(PB1-Pad1)"
Po 4.25 -1.25
$EndPAD
$PAD
Sh "42" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 75 "Net-(PB2-Pad1)"
Po 4.25 -0.75
$EndPAD
$PAD
Sh "41" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 76 "Net-(PB3-Pad1)"
Po 4.25 -0.25
$EndPAD
$PAD
Sh "40" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 77 "Net-(PB4-Pad1)"
Po 4.25 0.25
$EndPAD
$PAD
Sh "39" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 104 "Net-(U7-Pad39)"
Po 4.25 0.75
$EndPAD
$PAD
Sh "38" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 103 "Net-(U7-Pad38)"
Po 4.25 1.25
$EndPAD
$PAD
Sh "37" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 102 "Net-(U7-Pad37)"
Po 4.25 1.75
$EndPAD
$PAD
Sh "36" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 101 "Net-(U7-Pad36)"
Po 4.25 2.25
$EndPAD
$PAD
Sh "35" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 4.25 2.75
$EndPAD
$PAD
Sh "34" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 3 "+BATT"
Po 4.25 3.25
$EndPAD
$PAD
Sh "33" R 0.5 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 100 "Net-(U7-Pad33)"
Po 4.25 3.75
$EndPAD
$PAD
Sh "64" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 107 "Net-(U7-Pad64)"
Po -3.75 -4.25
$EndPAD
$PAD
Sh "17" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 94 "Net-(U7-Pad17)"
Po -3.75 4.25
$EndPAD
$PAD
Sh "49" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 12 "AXIS-OFF"
Po 3.75 -4.25
$EndPAD
$PAD
Sh "32" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 18 "NPDATA1"
Po 3.75 4.25
$EndPAD
$PAD
Sh "63" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 30 "Net-(C11-Pad2)"
Po -3.25 -4.25
$EndPAD
$PAD
Sh "62" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 106 "Net-(U7-Pad62)"
Po -2.75 -4.25
$EndPAD
$PAD
Sh "61" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -2.25 -4.25
$EndPAD
$PAD
Sh "60" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 37 "Net-(C22-Pad2)"
Po -1.75 -4.25
$EndPAD
$PAD
Sh "59" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 3 "+BATT"
Po -1.25 -4.25
$EndPAD
$PAD
Sh "58" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -0.75 -4.25
$EndPAD
$PAD
Sh "57" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 35 "Net-(C18-Pad2)"
Po -0.25 -4.25
$EndPAD
$PAD
Sh "56" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 31 "Net-(C14-Pad2)"
Po 0.25 -4.25
$EndPAD
$PAD
Sh "55" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0.75 -4.25
$EndPAD
$PAD
Sh "54" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 3 "+BATT"
Po 1.25 -4.25
$EndPAD
$PAD
Sh "53" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 9 "ACC-DRDY"
Po 1.75 -4.25
$EndPAD
$PAD
Sh "52" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 11 "ACC-INT2"
Po 2.25 -4.25
$EndPAD
$PAD
Sh "51" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 10 "ACC-INT1"
Po 2.75 -4.25
$EndPAD
$PAD
Sh "50" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 17 "MODE-SW"
Po 3.25 -4.25
$EndPAD
$PAD
Sh "18" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 95 "Net-(U7-Pad18)"
Po -3.25 4.25
$EndPAD
$PAD
Sh "19" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 96 "Net-(U7-Pad19)"
Po -2.75 4.25
$EndPAD
$PAD
Sh "20" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -2.25 4.25
$EndPAD
$PAD
Sh "21" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 36 "Net-(C19-Pad2)"
Po -1.75 4.25
$EndPAD
$PAD
Sh "22" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 36 "Net-(C19-Pad2)"
Po -1.25 4.25
$EndPAD
$PAD
Sh "23" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 3 "+BATT"
Po -0.75 4.25
$EndPAD
$PAD
Sh "24" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -0.25 4.25
$EndPAD
$PAD
Sh "25" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 109 "SCL (+BAT)"
Po 0.25 4.25
$EndPAD
$PAD
Sh "26" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 110 "SDA (+BAT)"
Po 0.75 4.25
$EndPAD
$PAD
Sh "27" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 16 "GYRO-INT1"
Po 1.25 4.25
$EndPAD
$PAD
Sh "28" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 15 "GYRO-DRDY"
Po 1.75 4.25
$EndPAD
$PAD
Sh "29" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 98 "Net-(U7-Pad29)"
Po 2.25 4.25
$EndPAD
$PAD
Sh "30" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 99 "Net-(U7-Pad30)"
Po 2.75 4.25
$EndPAD
$PAD
Sh "31" R 0.3 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 19 "NPDATA2"
Po 3.25 4.25
$EndPAD
$PAD
Sh "65" R 5 5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0 0
$EndPAD
$EndMODULE open-project:ATMEL-QFN-64
$MODULE open-project:CMA-4544PF-W
Po 33 42 0 15 53A12CFF 00000000 ~~
Li open-project:CMA-4544PF-W
Sc 0
AR /5394A4FD
Op 0 0 0
T0 0 -4 1 1 0 0.15 N V 21 N "MIC1"
T1 0 8 1 1 0 0.15 N I 21 N "SM-MIC"
DC 0 0 4.5 4.5 0.5 21
$PAD
Sh "1" C 1.3 1.3 0 0 0
Dr 0.6 0 0
At STD N 00E0FFFF
Ne 45 "Net-(C9-Pad2)"
Po -1.25 2
$EndPAD
$PAD
Sh "2" C 1.3 1.3 0 0 0
Dr 0.6 0 0
At STD N 00E0FFFF
Ne 14 "GND"
Po 1.25 2
$EndPAD
$EndMODULE open-project:CMA-4544PF-W
$MODULE open-project:CONN_JST-2_SMD
Po 45.3 22.3 0 15 53B12B2C 00000000 ~~
Li open-project:CONN_JST-2_SMD
Kw JST
Sc 0
AR /53959B96
Op 0 0 0
T0 -0.01 -10.35 0.762 0.762 0 0.127 N V 21 N "P2"
T1 -0.01 1.34 0.762 0.762 0 0.127 N V 21 N "CONN_2"
DS -4.0005 0 4.0005 0 0.381 21
DS 4.0005 0 4.0005 -8.49884 0.381 21
DS 4.0005 -8.49884 3.50012 -8.49884 0.381 21
DS 3.50012 -8.49884 3.50012 -5.99948 0.381 21
DS 3.50012 -5.99948 -3.50012 -5.99948 0.381 21
DS -3.50012 -5.99948 -3.50012 -8.49884 0.381 21
DS -3.50012 -8.49884 -4.0005 -8.49884 0.381 21
DS -4.0005 -8.49884 -4.0005 0 0.381 21
T2 -2.49936 -8.49884 1.524 1.524 0 0.3048 N V 21 N "+"
$PAD
Sh "" R 1.4986 3.39598 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 0 ""
Po -3.34772 -1.69926
$EndPAD
$PAD
Sh "1" R 0.99568 3.49758 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 2 "+5C"
Po -0.99822 -7.24916
$EndPAD
$PAD
Sh "2" R 0.99568 3.49758 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0.99822 -7.24916
$EndPAD
$PAD
Sh "" R 1.4986 3.39598 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 0 ""
Po 3.34772 -1.69926
$EndPAD
$EndMODULE open-project:CONN_JST-2_SMD
$MODULE open-project:EPAD
Po 23.5 36.2 0 15 53B0F9A6 00000000 ~~
Li open-project:EPAD
Sc 0
AR /5396A4BF
Op 0 0 0
T0 0 -2.7 1 1 0 0.15 N I 21 N "PB4"
T1 0 2.2 1 1 0 0.15 N I 21 N "PB3"
$PAD
Sh "1" O 2.5 2.5 0 0 0
Dr 1 0 -0.4
At STD N 00E0FFFF
Ne 77 "Net-(PB4-Pad1)"
Po 0.2 -0.2
$EndPAD
$EndMODULE open-project:EPAD
$MODULE open-project:FTDI-QFN32
Po 40.8 29.1 0 15 53B127A0 00000000 ~~
Li open-project:FTDI-QFN32
Sc 0
AR /53B0A96F
Op 0 0 0
T0 0 -6.9 1 1 0 0.15 N V 21 N "IC1"
T1 0 5.4 1 1 0 0.15 N V 21 N "FT232RQ"
DC -2.3 -2.2 -2.2 -2.2 0.15 21
DS -2.5 0 -2.5 2.5 0.15 21
DS -2.5 2.5 2.5 2.5 0.15 21
DS 2.5 2.5 2.5 -2.5 0.15 21
DS 2.5 -2.5 -2.5 -2.5 0.15 21
DS -2.5 -2.5 -2.5 0 0.15 21
$PAD
Sh "25" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 60 "Net-(IC1-Pad25)"
Po 1.75 -2.3
$EndPAD
$PAD
Sh "26" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 1.25 -2.3
$EndPAD
$PAD
Sh "27" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 61 "Net-(IC1-Pad27)"
Po 0.75 -2.3
$EndPAD
$PAD
Sh "28" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 62 "Net-(IC1-Pad28)"
Po 0.25 -2.3
$EndPAD
$PAD
Sh "29" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 63 "Net-(IC1-Pad29)"
Po -0.25 -2.3
$EndPAD
$PAD
Sh "30" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 65 "Net-(IC1-Pad30)"
Po -0.75 -2.3
$EndPAD
$PAD
Sh "31" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 39 "Net-(C24-Pad2)"
Po -1.25 -2.3
$EndPAD
$PAD
Sh "32" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 66 "Net-(IC1-Pad32)"
Po -1.75 -2.3
$EndPAD
$PAD
Sh "17" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 2.3 1.75
Le 0.1
$EndPAD
$PAD
Sh "18" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 13 "BRD RESET"
Po 2.3 1.25
$EndPAD
$PAD
Sh "19" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 8 "/VBUS"
Po 2.3 0.75
$EndPAD
$PAD
Sh "20" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 2.3 0.25
$EndPAD
$PAD
Sh "21" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 57 "Net-(IC1-Pad21)"
Po 2.3 -0.25
$EndPAD
$PAD
Sh "22" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 58 "Net-(IC1-Pad22)"
Po 2.3 -0.75
$EndPAD
$PAD
Sh "23" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 59 "Net-(IC1-Pad23)"
Po 2.3 -1.25
$EndPAD
$PAD
Sh "24" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 2.3 -1.75
$EndPAD
$PAD
Sh "1" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 3 "+BATT"
Po -2.3 -1.75
$EndPAD
$PAD
Sh "2" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 56 "Net-(IC1-Pad2)"
Po -2.3 -1.25
$EndPAD
$PAD
Sh "3" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 64 "Net-(IC1-Pad3)"
Po -2.3 -0.75
$EndPAD
$PAD
Sh "4" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -2.3 -0.25
$EndPAD
$PAD
Sh "5" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 67 "Net-(IC1-Pad5)"
Po -2.3 0.25
$EndPAD
$PAD
Sh "6" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 68 "Net-(IC1-Pad6)"
Po -2.3 0.75
$EndPAD
$PAD
Sh "7" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 69 "Net-(IC1-Pad7)"
Po -2.3 1.25
$EndPAD
$PAD
Sh "8" R 0.6 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 70 "Net-(IC1-Pad8)"
Po -2.3 1.75
Le 0.1
$EndPAD
$PAD
Sh "9" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 71 "Net-(IC1-Pad9)"
Po -1.75 2.3
$EndPAD
$PAD
Sh "10" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 49 "Net-(IC1-Pad10)"
Po -1.25 2.3
$EndPAD
$PAD
Sh "11" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 50 "Net-(IC1-Pad11)"
Po -0.75 2.3
$EndPAD
$PAD
Sh "12" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 51 "Net-(IC1-Pad12)"
Po -0.25 2.3
$EndPAD
$PAD
Sh "13" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 52 "Net-(IC1-Pad13)"
Po 0.25 2.3
$EndPAD
$PAD
Sh "14" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 53 "Net-(IC1-Pad14)"
Po 0.75 2.3
$EndPAD
$PAD
Sh "15" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 54 "Net-(IC1-Pad15)"
Po 1.25 2.3
$EndPAD
$PAD
Sh "16" R 0.3 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 55 "Net-(IC1-Pad16)"
Po 1.75 2.3
$EndPAD
$PAD
Sh "33" R 3.2 3.2 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0 0
$EndPAD
$EndMODULE open-project:FTDI-QFN32
$MODULE open-project:LGA-14
Po 54.95 50.95 0 15 53B08667 00000000 ~~
Li open-project:LGA-14
Sc 0
AR /5394CAC6
Op 0 0 0
T0 0 -2.6 1 1 0 0.15 N I 21 N "U2"
T1 0 2.6 1 1 0 0.15 N I 21 N "LSM303DLHC"
DC -2.5 1.2 -2.6 1.2 0.15 21
DS 0 -1.5 2.5 -1.5 0.15 21
DS 2.5 -1.5 2.5 1.5 0.15 21
DS 2.5 1.5 -2.5 1.5 0.15 21
DS -2.5 1.5 -2.5 -1.5 0.15 21
DS -2.5 -1.5 0 -1.5 0.15 21
$PAD
Sh "1" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 1 "+3.3V"
Po -2 1
$EndPAD
$PAD
Sh "2" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 6 "/SCL (3.3v)"
Po -1.2 1
$EndPAD
$PAD
Sh "3" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 7 "/SDA (3.3v)"
Po -0.4 1
$EndPAD
$PAD
Sh "4" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 11 "ACC-INT2"
Po 0.4 1
$EndPAD
$PAD
Sh "5" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 10 "ACC-INT1"
Po 1.2 1
$EndPAD
$PAD
Sh "6" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 32 "Net-(C15-Pad1)"
Po 2 1
$EndPAD
$PAD
Sh "7" R 0.8 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 2 0
$EndPAD
$PAD
Sh "8" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 86 "Net-(U2-Pad8)"
Po 2 -1
$EndPAD
$PAD
Sh "9" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 9 "ACC-DRDY"
Po 1.2 -1
$EndPAD
$PAD
Sh "10" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0.4 -1
$EndPAD
$PAD
Sh "11" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -0.4 -1
$EndPAD
$PAD
Sh "12" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 41 "Net-(C3-Pad1)"
Po -1.2 -1
$EndPAD
$PAD
Sh "13" R 0.5 0.8 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 42 "Net-(C3-Pad2)"
Po -2 -1
$EndPAD
$PAD
Sh "14" R 0.8 0.5 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 1 "+3.3V"
Po -2 0
$EndPAD
$EndMODULE open-project:LGA-14
$MODULE open-project:LGA-16
Po 50.9 43.75 0 15 53B08D5B 00000000 ~~
Li open-project:LGA-16
Sc 0
AR /5394CAD5
Op 0 0 0
T0 0 -2.9 1 1 0 0.15 N I 21 N "U3"
T1 0 3.2 1 1 0 0.15 N I 21 N "L3GD20"
DC -1.7 -1.6 -1.7 -1.7 0.15 21
DS 0 -2 -2 -2 0.15 21
DS -2 -2 -2 2 0.15 21
DS -2 2 2 2 0.15 21
DS 2 2 2 -2 0.15 21
DS 2 -2 0 -2 0.15 21
$PAD
Sh "13" R 0.3 0.4 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 34 "Net-(C17-Pad1)"
Po 0.975 -1.7
$EndPAD
$PAD
Sh "14" R 0.3 0.4 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 34 "Net-(C17-Pad1)"
Po 0.325 -1.7
$EndPAD
$PAD
Sh "15" R 0.3 0.4 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 1 "+3.3V"
Po -0.325 -1.7
$EndPAD
$PAD
Sh "16" R 0.3 0.4 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 1 "+3.3V"
Po -0.975 -1.7
$EndPAD
$PAD
Sh "9" R 0.4 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 1.7 0.975
$EndPAD
$PAD
Sh "10" R 0.4 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 34 "Net-(C17-Pad1)"
Po 1.7 0.325
$EndPAD
$PAD
Sh "11" R 0.4 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 34 "Net-(C17-Pad1)"
Po 1.7 -0.325
$EndPAD
$PAD
Sh "12" R 0.4 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 34 "Net-(C17-Pad1)"
Po 1.7 -0.975
$EndPAD
$PAD
Sh "1" R 0.4 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 1 "+3.3V"
Po -1.7 -0.975
$EndPAD
$PAD
Sh "2" R 0.4 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 6 "/SCL (3.3v)"
Po -1.7 -0.325
$EndPAD
$PAD
Sh "3" R 0.4 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 7 "/SDA (3.3v)"
Po -1.7 0.325
$EndPAD
$PAD
Sh "4" R 0.4 0.3 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 87 "Net-(U3-Pad4)"
Po -1.7 0.975
$EndPAD
$PAD
Sh "5" R 0.3 0.4 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 88 "Net-(U3-Pad5)"
Po -0.975 1.7
$EndPAD
$PAD
Sh "6" R 0.3 0.4 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 15 "GYRO-DRDY"
Po -0.325 1.7
$EndPAD
$PAD
Sh "7" R 0.3 0.4 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 16 "GYRO-INT1"
Po 0.325 1.7
$EndPAD
$PAD
Sh "8" R 0.3 0.4 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0.975 1.7
$EndPAD
$EndMODULE open-project:LGA-16
$MODULE open-project:MICRO-B_USB
Po 33.3 21.8 0 15 53B10D2F 00000000 ~~
Li open-project:MICRO-B_USB
Sc 0
AR /53B06A44
Op 0 0 0
T0 0 -5.842 0.762 0.762 0 0.127 N V 21 N "P1"
T1 -0.05 2.09 0.762 0.762 0 0.127 N V 21 N "MICRO-B_USB"
DS -4.0005 1.00076 -4.0005 1.19888 0.09906 21
DS 4.0005 1.00076 4.0005 1.19888 0.09906 21
DS -4.0005 -4.39928 4.0005 -4.39928 0.09906 21
DS 4.0005 -4.39928 4.0005 1.00076 0.09906 21
DS 4.0005 1.19888 -4.0005 1.19888 0.09906 21
DS -4.0005 1.00076 -4.0005 -4.39928 0.09906 21
$PAD
Sh "5" R 1.89738 1.89738 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -1.19888 -1.4478
$EndPAD
$PAD
Sh "5" R 1.89992 1.89738 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 1.19888 -1.4478
$EndPAD
$PAD
Sh "5" R 1.79578 1.89738 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 3.79984 -1.4478
$EndPAD
$PAD
Sh "5" R 2.0955 1.59766 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -3.0988 -3.99796
$EndPAD
$PAD
Sh "1" R 0.39878 1.3462 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 8 "/VBUS"
Po -1.29794 -4.12496
.LocalClearance 0.2032
$EndPAD
$PAD
Sh "2" R 0.39878 1.3462 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 54 "Net-(IC1-Pad15)"
Po -0.6477 -4.12496
.LocalClearance 0.2032
$EndPAD
$PAD
Sh "3" R 0.39878 1.3462 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 53 "Net-(IC1-Pad14)"
Po 0 -4.12496
.LocalClearance 0.2032
$EndPAD
$PAD
Sh "4" R 0.39878 1.3462 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 73 "Net-(P1-Pad4)"
Po 0.6477 -4.12496
.LocalClearance 0.2032
$EndPAD
$PAD
Sh "5" R 0.39878 1.3462 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 1.29794 -4.12496
.LocalClearance 0.2032
$EndPAD
$PAD
Sh "5" R 2.0955 1.59766 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 3.0988 -3.99796
$EndPAD
$PAD
Sh "5" R 1.79578 1.89738 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -3.79984 -1.4478
$EndPAD
$EndMODULE open-project:MICRO-B_USB
$MODULE open-project:SMD-MOM-SW-2P
Po 53.55 41.1 0 15 53F00316 00000000 ~~
Li open-project:SMD-MOM-SW-2P
Sc 0
AR /5396DFEC
Op 0 0 0
T0 0 -2 1 1 0 0.15 N I 21 N "SW2"
T1 0 1.2 1 1 0 0.15 N I 21 N "KPT-1187B"
$PAD
Sh "1" R 1.1 1.6 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -3.65 0
$EndPAD
$PAD
Sh "2" R 1.1 1.6 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 13 "BRD RESET"
Po 3.65 0
$EndPAD
$EndMODULE open-project:SMD-MOM-SW-2P
$MODULE open-project:SMD-XTAL
Po 39.1 33.8 0 15 53A7FC45 00000000 ~~
Li open-project:SMD-XTAL
Sc 0
AR /53A5C6BA
Op 0 0 0
T0 0 -3.6 1 1 0 0.15 N V 21 N "X2"
T1 0 3.5 1 1 0 0.15 N V 21 N "16.0 MHz"
DC -1.3 1 -1.2 1 0.15 21
DS 0 -1.2 -1.6 -1.2 0.15 21
DS -1.6 -1.2 -1.6 1.3 0.15 21
DS -1.6 1.3 1.6 1.3 0.15 21
DS 1.6 1.3 1.6 -1.2 0.15 21
DS 1.6 -1.2 0 -1.2 0.15 21
$PAD
Sh "1" R 1.3 1.1 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 35 "Net-(C18-Pad2)"
Po -1.15 0.9
$EndPAD
$PAD
Sh "2" R 1.3 1.1 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 1.15 0.9
$EndPAD
$PAD
Sh "3" R 1.3 1.1 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 31 "Net-(C14-Pad2)"
Po 1.15 -0.9
$EndPAD
$PAD
Sh "4" R 1.3 1.1 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -1.15 -0.9
$EndPAD
$EndMODULE open-project:SMD-XTAL
$MODULE open-project:SMD_Balum_Filter
Po 56.7 33.1 0 15 53B0595A 00000000 ~~
Li open-project:SMD_Balum_Filter
Sc 0
AR /53A78815
Op 0 0 0
T0 0 -2.4 1 1 0 0.15 N I 21 N "FLTR1"
T1 0 2.5 1 1 0 0.15 N I 21 N "Balun-Filter"
DS -0.7 -0.1 -0.7 0.1 0.15 21
DS -0.7 0.1 -0.4 0.1 0.15 21
DS -0.4 0.1 -0.4 -0.1 0.15 21
DS -0.4 -0.1 -0.7 -0.1 0.15 21
DS 0 -0.7 -1 -0.7 0.15 21
DS -1 -0.7 -1 0.7 0.15 21
DS -1 0.7 1 0.7 0.15 21
DS 1 0.7 1 -0.7 0.15 21
DS 1 -0.7 0 -0.7 0.15 21
$PAD
Sh "2" R 0.35 1 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0 0.8
$EndPAD
$PAD
Sh "1" R 0.65 1 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 5 "/50-Ohm-Feed"
Po -0.8 0.8
$EndPAD
$PAD
Sh "3" R 0.65 1 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 47 "Net-(FLTR1-Pad3)"
Po 0.8 0.8
$EndPAD
$PAD
Sh "4" R 0.65 1 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 48 "Net-(FLTR1-Pad4)"
Po 0.8 -0.8
$EndPAD
$PAD
Sh "5" R 0.35 1 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po 0 -0.8
$EndPAD
$PAD
Sh "6" R 0.35 1 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 14 "GND"
Po -0.65 -0.8
$EndPAD
$EndMODULE open-project:SMD_Balum_Filter
$MODULE open-project:SMD_Chip_Antenna
Po 69.2 39.2 -0 15 53FBA6BE 00000000 ~~
Li open-project:SMD_Chip_Antenna
Sc 0
AR /53A7882E
Op 0 0 0
T0 0 -2.2 1 1 -0 0.15 N I 21 N "A1"
T1 0 2 1 1 -0 0.15 N I 21 N "SMT_Antenna"
DS -4.1 0 -4.1 2 0.15 21
DS -4.1 2 4 2 0.15 21
DS 4 2 4 0 0.15 21
DS 4 0 4 -2 0.15 21
DS 4 -2 -4.1 -2 0.15 21
DS -4.1 -2 -4.1 0 0.15 21
DS -1.9 -0.1 -1.9 0.1 0.15 21
DS -1.9 0.1 -1.5 0.1 0.15 21
DS -1.5 0.1 -1.5 -0.1 0.15 21
DS -1.5 -0.1 -1.8 -0.1 0.15 21
DS -1.8 -0.1 -1.9 -0.1 0.15 21
DS 0 -1 -2.5 -1 0.15 21
DS -2.5 -1 -2.5 1 0.15 21
DS -2.5 1 2.5 1 0.15 21
DS 2.5 1 2.5 -1 0.15 21
DS 2.5 -1 0 -1 0.15 21
$PAD
Sh "1" R 0.7 1 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 4 "/50-Ohm-Ant"
Po -2.25 0
$EndPAD
$PAD
Sh "2" R 0.7 1 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 20 "Net-(A1-Pad2)"
Po 2.25 0
$EndPAD
$PAD
Sh "3" R 2.8 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 21 "Net-(A1-Pad3)"
Po 0 -0.75
$EndPAD
$PAD
Sh "4" R 2.8 0.6 0 0 -0
Dr 0 0 0
At SMD N 00888000
Ne 22 "Net-(A1-Pad4)"
Po 0 0.75
$EndPAD
$EndMODULE open-project:SMD_Chip_Antenna
$MODULE open-project:SMT_SIDE_SLIDE_SW
Po 29.8 55.3 0 15 53A28672 00000000 ~~
Li open-project:SMT_SIDE_SLIDE_SW
Sc 0
AR /53A27BAF
Op 0 0 0
T0 0 -9 1 1 0 0.15 N V 21 N "SW3"
T1 0 6.7 1 1 0 0.15 N V 21 N "PCM12SMTR"
DS 0 2.6 -0.7 2.6 0.15 21
DS -0.7 2.6 -0.7 1.4 0.15 21
DS -0.7 1.4 0.7 1.4 0.15 21
DS 0.7 1.4 0.7 2.6 0.15 21
DS 0.7 2.6 0 2.6 0.15 21
DS -3.4 -1.3 3.4 -1.3 0.15 21
DS 3.4 -1.3 3.4 1.3 0.15 21
DS 3.4 1.3 -3.4 1.3 0.15 21
DS -3.4 1.3 -3.4 -1.3 0.15 21
$PAD
Sh "" C 0.889 0.889 0 0 0
Dr 0.889 0 0
At HOLE N 00E0FFFF
Ne 0 ""
Po 1.4986 0
$EndPAD
$PAD
Sh "" C 0.889 0.889 0 0 0
Dr 0.889 0 0
At HOLE N 00E0FFFF
Ne 0 ""
Po -1.4986 0
$EndPAD
$PAD
Sh "2" R 0.7112 1.524 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 2 "+5C"
Po 0.762 -1.7272
$EndPAD
$PAD
Sh "3" R 0.7112 1.524 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 0 ""
Po 2.2606 -1.7272
$EndPAD
$PAD
Sh "1" R 0.7112 1.524 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 3 "+BATT"
Po -2.2352 -1.7272
$EndPAD
$PAD
Sh "" R 0.9906 0.8128 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 0 ""
Po -3.6449 -1.1049
$EndPAD
$PAD
Sh "" R 0.9906 0.8128 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 0 ""
Po -3.6449 1.1049
$EndPAD
$PAD
Sh "" R 0.9906 0.8128 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 0 ""
Po 3.6449 -1.1049
$EndPAD
$PAD
Sh "" R 0.9906 0.8128 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 0 ""
Po 3.6449 1.1049
$EndPAD
$EndMODULE open-project:SMT_SIDE_SLIDE_SW
$MODULE open-project:Side-SMT-SW
Po 40.2 55.4 0 15 53A146A8 00000000 ~~
Li open-project:Side-SMT-SW
Sc 0
AR /53A006F9
Op 0 0 0
T0 0 -2.7 1 1 0 0.15 N I 21 N "SW1"
T1 0 4.2 1 1 0 0.15 N I 21 N "PTS840"
DS 0 2.1 0.9 2.1 0.15 21
DS 0.9 2.1 0.9 1.6 0.15 21
DS 0.9 1.6 -0.9 1.6 0.15 21
DS -0.9 1.6 -0.9 2.1 0.15 21
DS -0.9 2.1 0 2.1 0.15 21
DS 0 -1.5 -4 -1.5 0.15 21
DS -4 -1.5 -4 1.4 0.15 21
DS -4 1.4 -4 1.5 0.15 21
DS -4 1.5 4.1 1.5 0.15 21
DS 4.1 1.5 4.1 -1.5 0.15 21
DS 4.1 -1.5 0 -1.5 0.15 21
$PAD
Sh "" C 0.75 0.75 0 0 0
Dr 0.75 0 0
At HOLE N 0000FFFF
Ne 0 ""
Po 0 -0.9
$EndPAD
$PAD
Sh "" C 0.7 0.7 0 0 0
Dr 0.7 0 0
At HOLE N 0000FFFF
Ne 0 ""
Po 0 0.9
$EndPAD
$PAD
Sh "1" R 1.4 1.05 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 72 "Net-(JMP1-Pad1)"
Po -3.6 -0.825
$EndPAD
$PAD
Sh "GND" R 1.1 0.5 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 0 ""
Po 0 -1.7
$EndPAD
$PAD
Sh "2" R 1.4 1.05 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 17 "MODE-SW"
Po 3.6 -0.825
$EndPAD
$PAD
Sh "3" R 1.4 1.05 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 0 ""
Po -3.6 0.825
$EndPAD
$PAD
Sh "4" R 1.4 1.05 0 0 0
Dr 0 0 0
At SMD N 00888000
Ne 0 ""
Po 3.6 0.825
$EndPAD
$EndMODULE open-project:Side-SMT-SW
$EndLIBRARY
