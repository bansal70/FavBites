package co.fav.bites.models.beans;

/*
 * Created by rishav on 8/29/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageResult {
    @SerializedName("response")
    @Expose
    public String response;
}
