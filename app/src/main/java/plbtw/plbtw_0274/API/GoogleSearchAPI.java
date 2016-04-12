package plbtw.plbtw_0274.API;





import plbtw.plbtw_0274.Model.address.ArrayofSearchResultBean;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by dedeeko on 1/4/16.
 */
public interface GoogleSearchAPI {

    @GET("/maps/api/geocode/json?result_type=route&key="+ "AIzaSyD5DmVoxPVbnHe242CMcdMMnmvo3Bn54Jk")
    public ArrayofSearchResultBean getLocation(@Query("latlng") String latlng);

    @GET("/maps/api/geocode/json?result_type=route&key="+ "AIzaSyD5DmVoxPVbnHe242CMcdMMnmvo3Bn54Jk")
    public ArrayofSearchResultBean getLatLng(@Query("address") String address);

    @GET("/maps/api/geocode/json?result_type=locality&key="+ "AIzaSyD5DmVoxPVbnHe242CMcdMMnmvo3Bn54Jk")
    public ArrayofSearchResultBean getCity(@Query("latlng") String address);

}
