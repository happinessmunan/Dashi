package yelp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class YelpAPI {
	private static final String API_HOST = "https://api.yelp.com";
	private static final String DEFAULT_TERM = "dinner";
	private static final int SEARCH_LIMIT = 20;
	private static final String SEARCH_PATH = "/v3/businesses/search";
	private static final String TOKEN_HOST = "https://api.yelp.com/oauth2/token";
	private static final String CLIENT_ID = "p3P18GmxZUu-TuFJ2DY2Uw";
	private static final String CLIENT_SECRET = "x6LqVgy7sWo53zbLhgOVQyQXowjrlZuG910lOKoT0BWxEelY1eTcm3Lv3uLwgB6R";
	private static final String GRANT_TYPE = "client_credentials";
	private static final String TOKEN_TYPE = "Bearer";

	public YelpAPI() {
	}

	/**
	 * Create and send a request to Yelp Token Host and return the access token
	 */
	private JSONObject obtainAccessToken() {
		try {
			String query = String.format("grant_type=%s&client_id=%s&client_secret=%s", GRANT_TYPE, CLIENT_ID,
					CLIENT_SECRET);

			HttpURLConnection connection = (HttpURLConnection) new URL(TOKEN_HOST).openConnection();

			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			// add request header
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(query.getBytes());

			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + TOKEN_HOST);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return new JSONObject(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates and sends a request to the Search API by term and location.
	 */
	public String searchForBusinessesByLocation(double lat, double lon) {
		String latitude = lat + "";
		String longitude = lon + "";
		String query = String.format("term=%s&latitude=%s&longitude=%s&limit=%s", DEFAULT_TERM, latitude, longitude,
				SEARCH_LIMIT);
		String url = API_HOST + SEARCH_PATH;
		try {
			String access_token = obtainAccessToken().getString("access_token");
			HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();

			// optional default is GET
			connection.setRequestMethod("GET");

			// add request header
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setRequestProperty("Authorization", TOKEN_TYPE + " " + access_token);

			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Queries the Search API based on the command line arguments and takes the
	 * first result to query the Business API.
	 */
	private static void queryAPI(YelpAPI yelpApi, double lat, double lon) {
		String searchResponseJSON = yelpApi.searchForBusinessesByLocation(lat, lon);
		JSONObject response = null;
		try {
			response = new JSONObject(searchResponseJSON);
			JSONArray businesses = (JSONArray) response.get("businesses");
			for (int i = 0; i < businesses.length(); i++) {
				JSONObject business = (JSONObject) businesses.get(i);
				System.out.println(business);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main entry for sample Yelp API requests.
	 */
	public static void main(String[] args) {
		YelpAPI yelpApi = new YelpAPI();
		queryAPI(yelpApi, 37.38, -122.08);
	}
}
