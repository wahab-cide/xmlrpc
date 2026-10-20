import java.sql.*;

public class BookstoreMigration {
   public static void main (String args[]) {
      Connection c = null;
      Statement stmt = null;
      int rows;

      try{
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:bookstore.db");
         System.out.println("Opened Database Successfully");
         stmt = c.createStatement();
         String sql = "CREATE TABLE IF NOT EXISTS BOOKS " + 
                        "(ID INT PRIMARY KEY, " +
                        "TITLE CHAR(100) NOT NULL, " +
                        "TOPIC CHAR(50) NOT NULL, " +
                        "STOCK INT NOT NULL, " +
                        "PRICE FLOAT NOT NULL)";
         stmt.executeUpdate(sql);

         sql = "INSERT OR REPLACE INTO BOOKS(ID, TITLE, TOPIC, STOCK, PRICE) VALUES" +
               "(53477, 'Achieve Less Bugs and More Hugs in CSCI 339', 'Distributed systems', 4, 119.99), " +
               "(53573, 'Distributed Systems for Dummies', 'Distributed systems', 6, 120.99), " +
               "(12365, 'Surviving College', 'College life', 24, 45.99), " +
               "(12498, 'Cooking for the Impatient Undergraduate',  'College life', 7, 46.99) ";
         rows = stmt.executeUpdate(sql);
         System.out.println(rows + " rows inserted");
         stmt.close();
         c.close();

      } catch (Exception e) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage());
         System.exit(0);
      }
      System.out.println("Migration succeeded!");
   }
}