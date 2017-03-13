SRCS = $(wildcard src/simulator/beans/*.java src/simulator/util/*.java simulator/reader/*.java src/simulator/processor/*.java src/simulator/driver/*.java)
CLS  = $(SRCS:.java=.class)

default:
	javac -classpath . $(SRCS)

clean:
	$(RM) $(CLS)
