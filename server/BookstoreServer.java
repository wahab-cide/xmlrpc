import java.util.*;
import org.apache.xmlrpc.webserver.WebServer;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.PropertyHandlerMapping;

public class BookstoreServer {
   static final int PORT = 8888;
   static final int RESTOCK_SECONDS = 60;
   static final int RESTOCK_AMOUNT = 5;

   public static void main(String[] args) {
      // start the XML-RPC server; clients call store.search / store.lookup / store.buy
      try {
         PropertyHandlerMapping phm = new PropertyHandlerMapping();
         phm.addHandler("store", Bookstore.class);
         WebServer server = new WebServer(PORT);
         XmlRpcServer xmlRpcServer = server.getXmlRpcServer();
         xmlRpcServer.setHandlerMapping(phm);
         server.start();
         System.out.println("Server started on port " + PORT);
      } catch (Exception e) {
         System.err.println("Server: " + e);
         System.exit(1);
      }

      // restock every book on a timer
      Thread restocker = new Thread() {
         public void run() {
            while (true) {
               try {
                  Thread.sleep(RESTOCK_SECONDS * 1000);
               } catch (InterruptedException e) {
                  return;
               }
               Database.restockAll(RESTOCK_AMOUNT);
               System.out.println("restocked every book by " + RESTOCK_AMOUNT);
            }
         }
      };
      restocker.start();

      // admin commands, typed into the server's terminal (clients can't run these)
      System.out.println("commands: log | restock <amount> | update <item> <price>");
      Scanner in = new Scanner(System.in);
      while (in.hasNextLine()) {
         String[] cmd = in.nextLine().trim().split(" +");
         try {
            if (cmd[0].equals("log")) {
               PurchaseLog.print();
            } else if (cmd[0].equals("restock") && cmd.length == 2) {
               int amount = Integer.parseInt(cmd[1]);
               Database.restockAll(amount);
               System.out.println("restocked every book by " + amount);
            } else if (cmd[0].equals("update") && cmd.length == 3) {
               int id = Integer.parseInt(cmd[1]);
               double price = Double.parseDouble(cmd[2]);
               if (Database.updatePrice(id, price)) {
                  System.out.println("item " + id + " now costs " + price);
               } else {
                  System.out.println("no item " + id);
               }
            } else if (!cmd[0].isEmpty()) {
               System.out.println("commands: log | restock <amount> | update <item> <price>");
            }
         } catch (NumberFormatException e) {
            System.out.println("bad number");
         }
      }
   }
}
