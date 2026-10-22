package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"

	"github.com/kolo/xmlrpc"
)

// usage: go run . <server>
// then type commands until quit (or Ctrl-D)

const usage = "commands: search <topic> | lookup <item> | buy <item> | quit"

type Book struct {
	ID    int     `xmlrpc:"id"`
	Title string  `xmlrpc:"title"`
	Topic string  `xmlrpc:"topic"`
	Price float64 `xmlrpc:"price"`
	Stock int     `xmlrpc:"stock"`
}

func main() {
	if len(os.Args) != 2 {
		fmt.Println("usage: go run . <server>")
		os.Exit(1)
	}

	server, err := xmlrpc.NewClient("http://"+os.Args[1]+":8888", nil)
	if err != nil {
		fmt.Println("Client error:", err)
		os.Exit(1)
	}

	fmt.Println(usage)
	in := bufio.NewScanner(os.Stdin)
	for {
		fmt.Print("> ")
		if !in.Scan() {
			break
		}
		words := strings.Fields(in.Text())
		if len(words) == 0 {
			continue
		}
		if words[0] == "quit" {
			break
		}
		if len(words) < 2 {
			fmt.Println(usage)
			continue
		}

		// an error (bad number, server down) ends this command, not the client
		if err := run(server, words); err != nil {
			fmt.Println("Client error:", err)
		}
	}
}

// run does one command, e.g. ["lookup", "12365"]
func run(server *xmlrpc.Client, words []string) error {
	switch words[0] {
	case "search":
		topic := strings.Join(words[1:], " ")
		var books []Book
		if err := server.Call("store.search", topic, &books); err != nil {
			return err
		}
		if len(books) == 0 {
			fmt.Println("no books for topic:", topic)
		}
		for _, book := range books {
			fmt.Println(book.ID, book.Title)
		}

	case "lookup":
		id, err := item(words[1])
		if err != nil {
			return err
		}
		var book Book
		if err := server.Call("store.lookup", id, &book); err != nil {
			return err
		}
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
		id, err := item(words[1])
		if err != nil {
			return err
		}
		var bought bool
		if err := server.Call("store.buy", id, &bought); err != nil {
			return err
		}
		if bought {
			fmt.Println("purchase complete")
		} else {
			fmt.Println("purchase failed (out of stock or no such item)")
		}

	default:
		fmt.Println("unknown command:", words[0])
	}
	return nil
}

func item(word string) (int, error) {
	id, err := strconv.Atoi(word)
	if err != nil {
		return 0, fmt.Errorf("item must be a number: %s", word)
	}
	return id, nil
}
