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

TODO

