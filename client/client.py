#!/usr/bin/env python3

import xmlrpc.client, sys

# usage: python3 client.py <server>
# then type commands until quit (or Ctrl-D)
USAGE = "commands: search <topic> | lookup <item> | buy <item> | quit"

if len(sys.argv) != 2:
    print("usage: python3 client.py <server>")
    sys.exit(1)

server = xmlrpc.client.ServerProxy("http://" + sys.argv[1] + ":8888")
print(USAGE)

while True:
    try:
        words = input("> ").split()
    except EOFError:
        break

    if len(words) == 0:
        continue
    command = words[0]
    if command == "quit":
        break
    if len(words) < 2:
        print(USAGE)
        continue

    # an error (bad number, server down) ends this command, not the client
    try:
        if command == "search":
            topic = " ".join(words[1:])
            books = server.store.search(topic)
            if len(books) == 0:
                print("no books for topic:", topic)
            for book in books:
                print(book["id"], book["title"])

        elif command == "lookup":
            book = server.store.lookup(int(words[1]))
            if len(book) == 0:
                print("no such item")
            else:
                print("Title:   ", book["title"])
                print("Topic:   ", book["topic"])
                print("Price:   ", book["price"])
                print("In stock:", "yes" if book["stock"] > 0 else "no")

        elif command == "buy":
            if server.store.buy(int(words[1])):
                print("purchase complete")
            else:
                print("purchase failed (out of stock or no such item)")

        else:
            print("unknown command:", command)
    except ValueError:
        print("item must be a number:", words[1])
    except Exception as e:
        print("Client error:", e)
