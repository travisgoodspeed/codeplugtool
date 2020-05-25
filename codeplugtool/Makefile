
JAVA?=java -cp /opt/jSerialComm-2.6.2.jar:.
main?=$(JAVA) com.kk4vcz.codeplug.Main
cli?=$(JAVA) com.kk4vcz.codeplug.CommandLineInterface

run:
	$(main);

d74dump:
	$(cli) d74 /dev/ttyACM0 info dump

d74upload:
	$(cli) d74 /dev/ttyACM0 upload knoxville.csv



