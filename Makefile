# Server runs from server/, so bookstore.db lives in server/

build:
	cd server && javac -cp ".:../lib/*" *.java
	cd client && javac -cp ".:../lib/*" *.java

migrate: build
	cd server && java -cp ".:../lib/*" BookstoreMigration

server: build
	cd server && java -cp ".:../lib/*" BookstoreServer

# make client ARGS="localhost search college life"
client: build
	cd client && java -cp ".:../lib/*" Client $(ARGS)

# make goclient ARGS="localhost search college life"
goclient:
	cd client && go run . $(ARGS)

clean:
	rm -f server/*.class client/*.class

# also wipes the database
reset: clean
	rm -f server/bookstore.db

.PHONY: build migrate server client goclient clean reset
