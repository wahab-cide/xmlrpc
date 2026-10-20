# TinyBookstore

A Java XML-RPC bookstore server with Python, Java and Go clients.

Needs Java, Python 3 and make. Go is only needed for the Go client.

## Start the server

```bash
make migrate    # create the database (once)
make server     # start the server on port 8888
```

## Use a client

In another terminal:

```bash
python3 client/client.py localhost search college life
python3 client/client.py localhost lookup 12365
python3 client/client.py localhost buy 12365
```

The Java and Go clients take the same arguments:

```bash
make client ARGS="localhost search college life"
make goclient ARGS="localhost search college life"
```

Book IDs: 53477, 53573 (Distributed systems), 12365, 12498 (College life).

## Server commands

Type these into the server's terminal:

```
log                      show purchases
restock <amount>         add copies to every book
update <item> <price>    change a price
```

Every book is also restocked by 5 every 60 seconds.

To reset the books to their starting stock and prices, run `make migrate`
again.
