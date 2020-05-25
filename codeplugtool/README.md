Howdy y'all,

This is a friendly CAT tool for the Kenwood TH-D74 that I hope to build into a
portable framework for programming frequencies into radios from Linux, Windows,
Mac and Android.

It is organized as Java classes and interfaces with no dependencies other than
an InputStream and OutputStream to the radio.  The first major milestone will be
a command-line tool that dumps the radio's channels to a textfile and writes them
back with modifications.  Later, a GUI and support for other radios will be added.

73 de Knoxville,

--Travis KK4VCZ


## Dependencies

On Windows and Linux, the jSerialComm library is used to provide a serial port
connection.  It shouldn't be called directly except from the main() method, so
that it doesn't break compatibility with Android, where we'll only provide a
Bluetooth RFCOMM connection or a USB Host connection of our own styling.

## Building

TODO

## Examples

This is a quick example of the tool dumping my Kenwood TH-D74A.

```
Codeplug Tool by KK4VCZ and Friends
Opened port ttyACM0
Model:       TH-D74,K,4
Version:     1.09
Serial:      B9A10175
Callsign:    KK4VCZ-1
Frequency:   146520000
# ME 001,0146520000,0000600000,0,0,0,0,1,0,0,0,0,0,0,0,08,08,000,0,CQCQCQ,0,00,0
# Channel 001
# 146.520000 MHz FM (TX 146.520000 MHz)
# ME 002,0146520000,0000600000,0,0,0,0,1,1,0,0,0,0,0,0,12,08,000,0,CQCQCQ,0,00,0
# Channel 002
# 146.520000 MHz FM (TX 146.520000 MHz)
# TX Tone 100.000000
# ME 003,0146550000,0000600000,0,0,0,0,1,0,1,0,0,0,0,0,12,13,000,0,CQCQCQ,0,00,0
# Channel 003
# 146.550000 MHz FM (TX 146.550000 MHz)
# TX Tone 103.500000
# RX Tone 103.500000
# ME 010,0145370000,0000600000,0,0,0,0,1,0,0,0,0,0,0,2,08,08,000,0,CQCQCQ,0,00,0
# Channel 010
# 145.370000 MHz FM (TX 144.770000 MHz)
done
```
