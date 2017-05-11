package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBYelpImport {
	public static void main(String[] args) {
		try {
			// Ensure the driver is imported.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = null;

			try {
				System.out.println("Connecting to \n" + DBUtil.URL);
				conn = DriverManager.getConnection(DBUtil.URL);
			} catch (SQLException e) {
				System.out.println("SQLException " + e.getMessage());
				System.out.println("SQLState " + e.getSQLState());
				System.out.println("VendorError " + e.getErrorCode());
			}
			if (conn == null) {
				return;
			}
			// Step 1 Drop tables in case they exist.
			Statement stmt = conn.createStatement();

			String sql = "DROP TABLE IF EXISTS history";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS restaurants";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE restaurants " + "(business_id VARCHAR(255) NOT NULL, " + " name VARCHAR(255), "
					+ "categories VARCHAR(255), " + "city VARCHAR(255), " + "state VARCHAR(255), " + "stars FLOAT,"
					+ "full_address VARCHAR(255), " + "latitude FLOAT, " + " longitude FLOAT, "
					+ "image_url VARCHAR(255)," + "url VARCHAR(255)," + " PRIMARY KEY ( business_id ))";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE users " + "(user_id VARCHAR(255) NOT NULL, " + " password VARCHAR(255) NOT NULL, "
					+ " first_name VARCHAR(255), last_name VARCHAR(255), " + " PRIMARY KEY ( user_id ))";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE history " + "(visit_history_id bigint(20) unsigned NOT NULL AUTO_INCREMENT, "
					+ " user_id VARCHAR(255) NOT NULL , " + " business_id VARCHAR(255) NOT NULL, "
					+ " last_visited_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, "
					+ " PRIMARY KEY (visit_history_id),"
					+ "FOREIGN KEY (business_id) REFERENCES restaurants(business_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id))";
			stmt.executeUpdate(sql);

			// Step 3: insert data
			// Create a fake user
			sql = "INSERT INTO users " + "VALUES (\"1111\", \"3229c1097c00d497a0fd282d586be050\", \"John\", \"Smith\")";

			System.out.println("\nDBYelpImport executing query:\n" + sql);
			stmt.executeUpdate(sql);

			System.out.println("DBYelpImport: import is done successfully.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
