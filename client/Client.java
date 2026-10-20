import java.util.*;
import java.net.URL;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

// usage: java Client <server> search <topic>
//        java Client <server> lookup <item>
//        java Client <server> buy <item>
public class Client {
   public static void main(String[] args) {
      if (args.length < 3) {
         System.out.println("usage: java Client <server> search|lookup|buy <topic or item>");
         System.exit(1);
      }

      try {
         XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
         config.setServerURL(new URL("http://" + args[0] + ":8888"));
         XmlRpcClient client = new XmlRpcClient();
         client.setConfig(config);

         String command = args[1];

         if (command.equals("search")) {
            String topic = args[2];
            for (int i = 3; i < args.length; i++) {
               topic = topic + " " + args[i];
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
            int id = Integer.parseInt(args[2]);
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
            int id = Integer.parseInt(args[2]);
            boolean ok = (Boolean) client.execute("store.buy", new Object[]{id});
            if (ok) {
               System.out.println("purchase complete");
            } else {
               System.out.println("purchase failed (out of stock or no such item)");
            }

         } else {
            System.out.println("unknown command: " + command);
         }
      } catch (Exception e) {
         System.err.println("Client error: " + e);
      }
   }
}
