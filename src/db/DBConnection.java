package db;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public interface DBConnection {
	 /**
     * Close the connection.
     */
    public void close() ;
    
    /**
     * Insert the visited restaurants for a user.
     * @param userId
     * @param businessIds
     */
    public void setVisitedRestaurants(String userId, List<String> businessIds);

    /**
     * Delete the visited restaurants for a user.
     * @param userId
     * @param businessIds
     */
    public void unsetVisitedRestaurants(String userId, List<String> businessIds);

    /**
     * Get the visited restaurants for a user.
     * @param userId
     * @return
     */
    public Set<String> getVisitedRestaurants(String userId);

    /**
     * Get the restaurant json by id.
     * @param businessId
     * @param isVisited, set the visited field in json.
     * @return
     */
    public JSONObject getRestaurantsById(String businessId, boolean isVisited);

    /**
     * Recommend restaurants based on userId
     * @param userId
     * @return
     */
    public JSONArray recommendRestaurants(String userId);
    
    /**
     * Gets categories based on business id
     * @param businessId
     * @return
     */
    public Set<String> getCategories(String businessId);

    /**
     * Gets business id based on category
     * @param category
     * @return
     */
    public Set<String> getBusinessId(String category);
    
    /**
     * Search restaurants near a geolocation.
     * @param userId
     * @param lat
     * @param lon
     * @return
     */
    public JSONArray searchRestaurants(String userId, double lat, double lon, String term);

   /**
     * Verify if the userId matches the password.
     * @param userId
     * @param password
     * @return
     */
    public Boolean verifyLogin(String userId, String password);

    /**
     * Get user's name for the userId.
     * @param userId
     * @return First and Last Name
     */
    public String getFirstLastName(String userId);

}
