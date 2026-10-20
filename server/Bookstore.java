import java.util.*;

// The methods clients can call over XML-RPC.
// XML-RPC makes a new Bookstore for every request, so don't keep
// anything in fields here -- shared stuff lives in Database and PurchaseLog.
public class Bookstore {

   public Object[] search(String topic) {
      return Database.searchByTopic(topic).toArray();
   }

   // returns an empty map if the item doesn't exist
   public HashMap<String, Object> lookup(int id) {
      return Database.getBook(id);
   }

   public boolean buy(int id) {
      boolean bought = Database.decrementStock(id);
      if (bought) {
         String title = (String) Database.getBook(id).get("title");
         PurchaseLog.add(id, title);
      }
      return bought;
   }
}
