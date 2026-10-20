import java.util.*;

// Log of all purchases. The methods are synchronized so two threads
// can't add to (or print) the list at the same time.
public class PurchaseLog {
   static ArrayList<String> entries = new ArrayList<String>();

   public static synchronized void add(int id, String title) {
      entries.add(new Date() + "  item " + id + "  " + title);
   }

   public static synchronized void print() {
      if (entries.isEmpty()) {
         System.out.println("no purchases yet");
      }
      for (String entry : entries) {
         System.out.println(entry);
      }
   }
}
