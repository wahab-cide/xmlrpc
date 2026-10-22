# Server runs from server/, so bookstore.db lives in server/

# clients connect here; override with: make client SERVER=<host>
SERVER = localhost

build:
	cd server && javac -cp ".:../lib/*" *.java
	cd client && javac -cp ".:../lib/*" *.java

migrate: build
	cd server && java -cp ".:../lib/*" BookstoreMigration

server: build
	cd server && java -cp ".:../lib/*" BookstoreServer

client: build
	cd client && java -cp ".:../lib/*" Client $(SERVER)

goclient:
	cd client && go run . $(SERVER)

clean:
	rm -f server/*.class client/*.class

# also wipes the database
reset: clean
	rm -f server/bookstore.db

.PHONY: build migrate server client goclient clean reset
