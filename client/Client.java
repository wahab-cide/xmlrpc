import java.util.*;
import java.net.URL;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

// usage: java Client <server>
// then type commands until quit (or Ctrl-D)
public class Client {
   static final String USAGE = "commands: search <topic> | lookup <item> | buy <item> | quit";

   public static void main(String[] args) {
      if (args.length != 1) {
         System.out.println("usage: java Client <server>");
         System.exit(1);
      }

      XmlRpcClient client = new XmlRpcClient();
      try {
         XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
         config.setServerURL(new URL("http://" + args[0] + ":8888"));
         client.setConfig(config);
      } catch (Exception e) {
         System.err.println("Client error: " + e);
         System.exit(1);
      }

      System.out.println(USAGE);
      Scanner in = new Scanner(System.in);
      while (true) {
         System.out.print("> ");
         if (!in.hasNextLine()) {
            break;
         }
         String line = in.nextLine().trim();
         if (line.isEmpty()) {
            continue;
         }
         String[] words = line.split(" +");
         if (words[0].equals("quit")) {
            break;
         }
         if (words.length < 2) {
            System.out.println(USAGE);
            continue;
         }

         // an error (bad number, server down) ends this command, not the client
         try {
            run(client, words);
         } catch (NumberFormatException e) {
            System.out.println("item must be a number: " + words[1]);
         } catch (Exception e) {
            System.out.println("Client error: " + e);
         }
      }
   }

   // do one command, e.g. {"lookup", "12365"}
   static void run(XmlRpcClient client, String[] words) throws Exception {
      String command = words[0];

      if (command.equals("search")) {
         String topic = words[1];
         for (int i = 2; i < words.length; i++) {
            topic = topic + " " + words[i];
         }
         Object[] books = (Object[]) client.execute("store.search", new Object[]{topic});
         if (books.length == 0) {
            System.out.println("no books for topic: " + topic);
         }
         for (Object o : books) {
            Map book = (Map) o;
            System.out.println(book.get("id") + " " + book.get("title"));
         }

      } else if (command.equals("lookup")) {
         int id = Integer.parseInt(words[1]);
         Map book = (Map) client.execute("store.lookup", new Object[]{id});
         if (book.isEmpty()) {
            System.out.println("no such item");
         } else {
            System.out.println("Title:    " + book.get("title"));
            System.out.println("Topic:    " + book.get("topic"));
            System.out.println("Price:    " + book.get("price"));
            System.out.println("In stock: " + ((Integer) book.get("stock") > 0 ? "yes" : "no"));
         }

      } else if (command.equals("buy")) {
         int id = Integer.parseInt(words[1]);
         boolean ok = (Boolean) client.execute("store.buy", new Object[]{id});
         if (ok) {
            System.out.println("purchase complete");
         } else {
            System.out.println("purchase failed (out of stock or no such item)");
         }

      } else {
         System.out.println("unknown command: " + command);
      }
   }
}
