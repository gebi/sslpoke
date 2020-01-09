all: SSLPoke.class

SSLPoke.class: SSLPoke.java
	javac $^

clean:
	rm -f SSLPoke.class
