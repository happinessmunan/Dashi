
package model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Restaurant {
	// * Performed data cleanup and purify from Yelp API.
	public static String parseString(String str) {
		return str.replace("\"", "\\\"").replace("/", " or ");
	}

	public static String jsonArrayToString(JSONArray array) {
		StringBuilder sb = new StringBuilder();
		try {
			for (int i = 0; i < array.length(); i++) {
				String obj = (String) array.get(i);
				sb.append(obj);
				if (i != array.length() - 1) {
					sb.append(",");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static JSONArray stringToJSONArray(String str) {
		try {
			return new JSONArray("[" + parseString(str) + "]");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String businessId;
	private String name;
	private String categories;
	private String city;
	private String state;
	private String fullAddress;
	private double stars;
	private double latitude;
	private double longitude;
	private String imageUrl;
	private String url;

	public Restaurant(JSONObject object) {
		try {
			if (object != null) {
				this.businessId = object.getString("id");
				JSONArray jsonArray = (JSONArray) object.get("categories");
				List<String> list = new ArrayList<>();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject subObejct = jsonArray.getJSONObject(i);
					list.add(subObejct.getString("title"));
				}
				this.categories = String.join(",", list);
				this.name = object.getString("name");
				this.imageUrl = object.getString("image_url");
				this.stars = object.getDouble("rating");
				JSONObject coordinates = (JSONObject) object.get("coordinates");
				this.latitude = coordinates.getDouble("latitude");
				this.longitude = coordinates.getDouble("longitude");
				JSONObject location = (JSONObject) object.get("location");
				this.city = location.getString("city");
				this.state = location.getString("state");
				this.fullAddress = jsonArrayToString((JSONArray) location.get("display_address"));
				this.url = object.getString("url");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// do not automatically generate this constructor
	public Restaurant(String businessId, String name, String categories, String city, String state, double stars,
			String fullAddress, double latitude, double longitude, String imageUrl, String url) {
		this.businessId = businessId;
		this.categories = categories;
		this.name = name;
		this.city = city;
		this.state = state;
		this.stars = stars;
		this.fullAddress = fullAddress;
		this.latitude = latitude;
		this.longitude = longitude;
		this.imageUrl = imageUrl;
		this.url = url;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("business_id", businessId);
			obj.put("name", name);
			obj.put("stars", stars);
			obj.put("latitude", latitude);
			obj.put("longitude", longitude);
			obj.put("full_address", fullAddress);
			obj.put("city", city);
			obj.put("state", state);
			obj.put("categories", stringToJSONArray(categories));
			obj.put("image_url", imageUrl);
			obj.put("url", url);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public double getStars() {
		return stars;
	}

	public void setStars(double stars) {
		this.stars = stars;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
