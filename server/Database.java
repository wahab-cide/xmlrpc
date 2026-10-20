import java.sql.*;
import java.util.*;

// All the SQL lives here. Every method opens its own connection,
// so requests running on different threads don't share one.
public class Database {
   static final String DB = "jdbc:sqlite:bookstore.db";

   public static ArrayList<HashMap<String, Object>> searchByTopic(String topic) {
      ArrayList<HashMap<String, Object>> books = new ArrayList<HashMap<String, Object>>();
      try {
         Class.forName("org.sqlite.JDBC");
         Connection c = DriverManager.getConnection(DB);
         PreparedStatement stmt = c.prepareStatement(
            "SELECT ID, TITLE FROM BOOKS WHERE TOPIC = ? COLLATE NOCASE");
         stmt.setString(1, topic);
         ResultSet rs = stmt.executeQuery();
         while (rs.next()) {
            HashMap<String, Object> book = new HashMap<String, Object>();
            book.put("id", rs.getInt("ID"));
            book.put("title", rs.getString("TITLE"));
            books.add(book);
         }
         rs.close();
         stmt.close();
         c.close();
      } catch (Exception e) {
         System.err.println("searchByTopic: " + e.getMessage());
      }
      return books;
   }

   // returns an empty map if there is no book with this id
   public static HashMap<String, Object> getBook(int id) {
      HashMap<String, Object> book = new HashMap<String, Object>();
      try {
         Class.forName("org.sqlite.JDBC");
         Connection c = DriverManager.getConnection(DB);
         PreparedStatement stmt = c.prepareStatement("SELECT * FROM BOOKS WHERE ID = ?");
         stmt.setInt(1, id);
         ResultSet rs = stmt.executeQuery();
         if (rs.next()) {
            book.put("id", rs.getInt("ID"));
            book.put("title", rs.getString("TITLE"));
            book.put("topic", rs.getString("TOPIC"));
            book.put("price", rs.getDouble("PRICE"));
            book.put("stock", rs.getInt("STOCK"));
         }
         rs.close();
         stmt.close();
         c.close();
      } catch (Exception e) {
         System.err.println("getBook: " + e.getMessage());
      }
      return book;
   }

   // Checks stock and takes one copy in a single statement, so two
   // buyers can't both get the last copy. Returns true if it worked.
   public static boolean decrementStock(int id) {
      int rows = 0;
      try {
         Class.forName("org.sqlite.JDBC");
         Connection c = DriverManager.getConnection(DB);
         PreparedStatement stmt = c.prepareStatement(
            "UPDATE BOOKS SET STOCK = STOCK - 1 WHERE ID = ? AND STOCK > 0");
         stmt.setInt(1, id);
         rows = stmt.executeUpdate();
         stmt.close();
         c.close();
      } catch (Exception e) {
         System.err.println("decrementStock: " + e.getMessage());
      }
      return rows == 1;
   }

   public static void restockAll(int amount) {
      try {
         Class.forName("org.sqlite.JDBC");
         Connection c = DriverManager.getConnection(DB);
         PreparedStatement stmt = c.prepareStatement("UPDATE BOOKS SET STOCK = STOCK + ?");
         stmt.setInt(1, amount);
         stmt.executeUpdate();
         stmt.close();
         c.close();
      } catch (Exception e) {
         System.err.println("restockAll: " + e.getMessage());
      }
   }

   // returns false if there is no book with this id
   public static boolean updatePrice(int id, double price) {
      int rows = 0;
      try {
         Class.forName("org.sqlite.JDBC");
         Connection c = DriverManager.getConnection(DB);
         PreparedStatement stmt = c.prepareStatement("UPDATE BOOKS SET PRICE = ? WHERE ID = ?");
         stmt.setDouble(1, price);
         stmt.setInt(2, id);
         rows = stmt.executeUpdate();
         stmt.close();
         c.close();
      } catch (Exception e) {
         System.err.println("updatePrice: " + e.getMessage());
      }
      return rows == 1;
   }
}
