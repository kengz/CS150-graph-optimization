JFLAGS = -g
JC = javac
JVM= java 
FILE=
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Builder.java \
	Graph.java \
	Importer.java \
	NNGraph.java \
	P3.java
	
	
	

MAIN = P3

default: classes run clean

classes: $(CLASSES:.java=.class)
 

run: $(MAIN).class
	$(JVM) $(MAIN)

clean:
	$(RM) *.class *.java~

