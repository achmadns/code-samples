import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * Backup of <a href="https://academy.datastax.com/resources/getting-started-apache-cassandra-and-java-part-i?unit=2201">Getting Started Part I</a>
 */
public class GettingStarted {
    public static void main(String[] args) {

        Cluster cluster;
        Session session;

        // Connect to the cluster and prepare keyspace "demo"
        cluster = Cluster.builder().addContactPoint(null == args || args.length < 1 ? "127.0.0.1" : args[0]).build();
        {
            cluster.connect().execute("CREATE KEYSPACE IF NOT EXISTS demo WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 }");
        }
        session = cluster.connect("demo");

        // Prepare table
        try {
            session.execute("CREATE TABLE IF NOT EXISTS users (firstname text, lastname text, age int, email text, city text, PRIMARY KEY (lastname))");
        } catch (Exception e) {
            System.out.println("Table 'users' already there.");
        }
        // Insert one record into the users table
        session.execute("INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Jones', 35, 'Austin', 'bob@example.com', 'Bob')");

        // Use select to get the user we just entered
        ResultSet results = session.execute("SELECT * FROM users WHERE lastname='Jones'");
        for (Row row : results) {
            System.out.format("%s %d\n", row.getString("firstname"), row.getInt("age"));
        }

        // Update the same user with a new age
        session.execute("update users set age = 36 where lastname = 'Jones'");

        // Select and show the change
        results = session.execute("select * from users where lastname='Jones'");
        for (Row row : results) {
            System.out.format("%s %d\n", row.getString("firstname"), row.getInt("age"));
        }

        // Delete the user from the users table
        session.execute("DELETE FROM users WHERE lastname = 'Jones'");

        // Show that the user is gone
        results = session.execute("SELECT * FROM users");
        for (Row row : results) {
            System.out.format("%s %d %s %s %s\n", row.getString("lastname"), row.getInt("age"), row.getString("city"), row.getString("email"), row.getString("firstname"));
        }

        // Clean up the connection by closing it
        cluster.close();
    }
}
