all: SSLPoke.class

SSLPoke.class: SSLPoke.java
	javac $^

test: clean
	/usr/lib/jvm/java-11-openjdk-amd64/bin/javac SSLPoke.java
	/usr/lib/jvm/java-17-openjdk-amd64/bin/javac SSLPoke.java
	/usr/lib/jvm/java-21-openjdk-amd64/bin/javac SSLPoke.java
	@rm SSLPoke.class

clean:
	@rm -f SSLPoke.class

.PHONY: test clean
