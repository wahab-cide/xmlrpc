package main

import (
	"fmt"
	"os"
	"strconv"
	"strings"

	"github.com/kolo/xmlrpc"
)

// usage: go run . <server> search <topic>
//        go run . <server> lookup <item>
//        go run . <server> buy <item>

type Book struct {
	ID    int     `xmlrpc:"id"`
	Title string  `xmlrpc:"title"`
	Topic string  `xmlrpc:"topic"`
	Price float64 `xmlrpc:"price"`
	Stock int     `xmlrpc:"stock"`
}

func main() {
	if len(os.Args) < 4 {
		fmt.Println("usage: go run . <server> search|lookup|buy <topic or item>")
		os.Exit(1)
	}

	server, err := xmlrpc.NewClient("http://"+os.Args[1]+":8888", nil)
	check(err)
	command := os.Args[2]

	switch command {
	case "search":
		topic := strings.Join(os.Args[3:], " ")
		var books []Book
		check(server.Call("store.search", topic, &books))
		if len(books) == 0 {
			fmt.Println("no books for topic:", topic)
		}
		for _, book := range books {
			fmt.Println(book.ID, book.Title)
		}

	case "lookup":
		var book Book
		check(server.Call("store.lookup", item(), &book))
		// the server sends an empty struct if the item doesn't exist
		if book.ID == 0 {
			fmt.Println("no such item")
		} else {
			fmt.Println("Title:   ", book.Title)
			fmt.Println("Topic:   ", book.Topic)
			fmt.Println("Price:   ", book.Price)
			if book.Stock > 0 {
				fmt.Println("In stock: yes")
			} else {
				fmt.Println("In stock: no")
			}
		}

	case "buy":
		var bought bool
		check(server.Call("store.buy", item(), &bought))
		if bought {
			fmt.Println("purchase complete")
		} else {
			fmt.Println("purchase failed (out of stock or no such item)")
		}

	default:
		fmt.Println("unknown command:", command)
	}
}

func item() int {
	id, err := strconv.Atoi(os.Args[3])
	if err != nil {
		fmt.Println("item must be a number:", os.Args[3])
		os.Exit(1)
	}
	return id
}

func check(err error) {
	if err != nil {
		fmt.Println("Client error:", err)
		os.Exit(1)
	}
}
