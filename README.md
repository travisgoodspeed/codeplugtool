Howdy y'all,

This is a friendly CAT tool for the Kenwood TH-D72, TH-D74 and TH-D710
written in Java for the command line, with the aim of eventually
supporting many radios and running over Bluetooth from an Android
phone.  The Yaesu FT-991A and other radios are partially supported.

73 de Knoxville,

--Travis KK4VCZ


## Dependencies

The [jSerialComm](https://github.com/Fazecast/jSerialComm) library is
used to provide a serial port connection for the CLI tool.  It is
embedded in the repo and statically linked into `CodePlugTool.jar`.

On Android, we'll instead be using the built-in Bluetooth RFCOMM
libraries.

## Building

Development is performed in Eclipse, but it's easy to build from the
command line with Apache Ant.

```
dell% ant clean jar
Buildfile: build.xml

clean:
   [delete] Deleting directory build

compile:
    [mkdir] Created dir: build/classes
    [unzip] Expanding: lib/jSerialComm-2.6.2.jar into build/classes
    [javac] Compiling 9 source files to build/classes

jar:
      [jar] Building jar: CodePlugTool.jar

BUILD SUCCESSFUL
Total time: 1 second
```

## CLI Usage

The CLI is unstable at this point, but generally you provide a driver
(`d74`) and a port name (`ttyACM0`) as your parameters, then a verb or
to to direct the tool around.  Run the tool with no parameters to see
a list of available drivers, ports, and verbs.


Usage with no parameters,

```
dell% java -jar CodePlugTool.jar 
Usage: 
cpt [driver] [port/file] [verbs]

Drivers:
        Kenwood
                d74 -- TH-D74 Tri-Band HT
                d710 -- TM-D710 Mobile
        Yaesu
                991a -- Yaesu FT-991A
        Others
                csv -- Chirp's CSV format.
Ports:
        ttyS0   -- Physical Port S0
        ttyS2   -- Physical Port S2
        ttyS1   -- Physical Port S1
        ttyUSB1 -- USB-to-Serial Port (cp210x)
        ttyUSB0 -- USB-to-Serial Port (cp210x)
Verbs:
        info             -- Prints the radio's info.
        dump             -- Dumps the radio's channels to the console.
        upload foo.csv   -- Uploads a CSV file from CHIRP to the radio.
        download foo.csv -- Downloads a CSV file from the radio.
        raw 'ME 000'   -- Runs a raw command and prints the result.
```

Viewing the radio settings,

```
dell% java -jar CodePlugTool.jar d74 ttyACM0 info
Model:       TH-D74,K,4
Version:     1.09
Serial:      B9A10175
Callsign:    NOCALL
Frequency:   224380000
```

Viewing the channels,

```
dell% java -jar CodePlugTool.jar d74 ttyACM0 dump
000 146.010000 MHz 
100 147.315000 MHz (TX +0.600000 MHz) 
101 146.895000 MHz (TX -0.600000 MHz) T100.000000
102 145.410000 MHz (TX -0.600000 MHz) T127.300000
103 147.240000 MHz (TX +0.600000 MHz) T114.800000
104 144.940000 MHz (TX 147.440000 MHz) 
105 146.925000 MHz (TX -0.600000 MHz) T114.800000
106 145.450000 MHz (TX -0.600000 MHz) T141.300000
107 146.790000 MHz (TX -0.600000 MHz) 
108 147.045000 MHz (TX -0.600000 MHz) T151.400000
...
```

Flashing a CSV file from Chirp to a Kenwood TH-D74,

```
dell% head knoxville.csv 
Location,Name,Frequency,Duplex,Offset,Tone,rToneFreq,cToneFreq,DtcsCode,DtcsPolarity,Mode,TStep,Skip,Comment,URCALL,RPT1CALL,RPT2CALL,DVCODE
0,,146.010000,,0.600000,,88.5,88.5,023,NN,FM,5.00,,,,,,
100,,147.315000,+,0.600000,,88.5,88.5,023,NN,FM,5.00,,Rogersville,,,,
101,W4KEV,146.895000,-,0.600000,Tone,100.0,88.5,023,NN,FM,5.00,,Dandridge,,,,
102,W4KEV,145.410000,-,0.600000,Tone,127.3,127.3,023,NN,FM,5.00,,"Greeneville, Bald Mtn",,,,
103,KE4KQI,147.240000,+,0.600000,Tone,114.8,114.8,023,NN,FM,5.00,,Sneedville,,,,
104,K4HXD,144.940000,split,147.440000,,88.5,88.5,023,NN,DV,5.00,,Knoxville,CQCQCQ,,,0
105,W4GZX,146.925000,-,0.600000,Tone,114.8,88.5,023,NN,Auto,5.00,,"Cleveland, CARC Clubhouse",,,,
106,KQ4E,145.450000,-,0.600000,Tone,141.3,141.3,023,NN,Auto,5.00,,Morristown,,,,
107,N4AW,146.790000,-,0.600000,,88.5,88.5,023,NN,FM,5.00,,"Pickens, Sassafrass Mountain",,,,

dell% java -jar CodePlugTool.jar d74 ttyACM0 upload knoxville.csv             
000 146.010000 MHz 
100 147.315000 MHz (TX +0.600000 MHz) 
101 146.895000 MHz (TX -0.600000 MHz) T100.000000
102 145.410000 MHz (TX -0.600000 MHz) T127.300000
103 147.240000 MHz (TX +0.600000 MHz) T114.800000
104 144.940000 MHz (TX 147.440000 MHz) 
105 146.925000 MHz (TX -0.600000 MHz) T114.800000
106 145.450000 MHz (TX -0.600000 MHz) T141.300000
107 146.790000 MHz (TX -0.600000 MHz) 
108 147.045000 MHz (TX -0.600000 MHz) T151.400000
109 145.490000 MHz (TX -0.600000 MHz) T167.900000
110 147.240000 MHz (TX +0.600000 MHz) T151.400000
...
```

## Developer Usage

See `CommandLineInterface` for quick examples of communicating
with a radio.  Better examples are on the way.


## License

If you use this code, you owe me one India Pale Ale.  If you complain
that this isn't in the spirit of Free Software, you owe me a dozen of
them.

