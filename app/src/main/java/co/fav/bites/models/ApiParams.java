package co.fav.bites.models;

/*
 * Created by rishav on 2/9/2018.
 */

import java.util.HashMap;

public class ApiParams {

    public static HashMap<String, String> getSearchRestaurantParams(String search, int page) {
        HashMap<String, String> mapParams = new HashMap<>();
        mapParams.put("search", search);
        mapParams.put("page", String.valueOf(page));

        return mapParams;
    }

    public static HashMap<String, String> getRestaurantDetailsParams(String restaurant_id, String user_id) {
        HashMap<String, String> mapParams = new HashMap<>();
        mapParams.put("restaurant_id", restaurant_id);
        mapParams.put("user_id", user_id);

        return mapParams;
    }
}
