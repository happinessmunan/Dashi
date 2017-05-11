
package db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

import org.bson.Document;

import java.text.ParseException;

public class MongoDBImport {
	public static void main(String[] args) throws ParseException {
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase(DBUtil.DB_NAME);
		db.getCollection("users").insertOne(new Document().append("first_name", "John").append("last_name", "Smith")
				.append("password", "3229c1097c00d497a0fd282d586be050").append("user_id", "1111"));
		// make sure user_id is unique.
		IndexOptions indexOptions = new IndexOptions().unique(true);

		// use 1 for ascending index , -1 for descending index
		db.getCollection("users").createIndex(new Document("user_id", 1), indexOptions);

		// make sure business_id is unique.
		db.getCollection("restaurants").createIndex(new Document("business_id", 1), indexOptions);

		// use a compound text index of name, full_address and categories for
		// search.
		db.getCollection("restaurants").createIndex(
				new Document().append("categories", "text").append("full_address", "text").append("name", "text"));
		mongoClient.close();
	}
}
