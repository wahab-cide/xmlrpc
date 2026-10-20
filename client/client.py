#!/usr/bin/env python3

import xmlrpc.client, sys

# usage: python3 client.py <server> search <topic>
#        python3 client.py <server> lookup <item>
#        python3 client.py <server> buy <item>
if len(sys.argv) < 4:
    print("usage: python3 client.py <server> search|lookup|buy <topic or item>")
    sys.exit(1)

server = xmlrpc.client.ServerProxy("http://" + sys.argv[1] + ":8888")
command = sys.argv[2]

if command == "search":
    topic = " ".join(sys.argv[3:])
    books = server.store.search(topic)
    if len(books) == 0:
        print("no books for topic:", topic)
    for book in books:
        print(book["id"], book["title"])

elif command == "lookup":
    book = server.store.lookup(int(sys.argv[3]))
    if len(book) == 0:
        print("no such item")
    else:
        print("Title:   ", book["title"])
        print("Topic:   ", book["topic"])
        print("Price:   ", book["price"])
        print("In stock:", "yes" if book["stock"] > 0 else "no")

elif command == "buy":
    if server.store.buy(int(sys.argv[3])):
        print("purchase complete")
    else:
        print("purchase failed (out of stock or no such item)")

else:
    print("unknown command:", command)
