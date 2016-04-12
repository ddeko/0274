package plbtw.plbtw_0274.API;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.List;

import plbtw.plbtw_0274.Model.address.APILocationResult;
import plbtw.plbtw_0274.Model.address.AddressComponentBean;
import plbtw.plbtw_0274.Model.address.ArrayofSearchResultBean;
import plbtw.plbtw_0274.Model.address.SearchResultBean;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by dedeeko on 3/29/16..
 */
public abstract class TaskSearchLocationByAddress extends AsyncTask<String, Void, Boolean> {
    private Context context;
    RestAdapter restAdapter;
    ArrayofSearchResultBean response;
    private String param;

    public TaskSearchLocationByAddress(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        restAdapter = BaseAPI.getGoogleGeocodeAPI();
    }

    @Override
    protected Boolean doInBackground(String... address) {
        GoogleSearchAPI methods = restAdapter.create(GoogleSearchAPI.class);
        param = address[0];
        try {
            response = methods.getLatLng(param);
        }
        catch (RetrofitError e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if(aBoolean == true){
            List<SearchResultBean> results = response.getResults();
            if(results == null){
                onSearchFailed("Failed to fetch data");
            }
            else if(results.isEmpty()){
                onSearchFailed("Data not found");
            }
            else{
                ArrayList<APILocationResult> result = new ArrayList<>();

                for(SearchResultBean searchEntity:results) {
                    List<AddressComponentBean> address = searchEntity.getAddress_components();

                    String route = "";
                    String country = "";
                    String info = "";
                    String admin1 = "";
                    String admin2 = "";

                    for (int i = 0; i < address.size(); i++) {
                        AddressComponentBean entity = address.get(i);
                        List<String> type = entity.getTypes();

                        if (type.contains("route")) {
                            route = entity.getLong_name();
                        } else if (type.contains("country")) {
                            country = entity.getLong_name();
                        } else if (type.contains("administrative_area_level_1")) {
                            admin1 = entity.getLong_name();
                        }
                        else if (type.contains("administrative_area_level_2")) {
                            admin2 = entity.getLong_name();
                        }
                    }

                    if(admin2.isEmpty() == false){
                        admin2 += ", ";
                    }
                    info = admin2+admin1;

                    float latitude = searchEntity.getGeometry().getLocation().getLat();
                    float longitude = searchEntity.getGeometry().getLocation().getLng();


                    if (info.isEmpty()) {
                        info = "---";
                    }

                    APILocationResult entityOfResult = new APILocationResult();
                    entityOfResult.setCountry_name(country);
                    entityOfResult.setCity(admin1);
                    entityOfResult.setStreetName(route);
                    entityOfResult.setInfo(info);
                    entityOfResult.setFormattedAddress(searchEntity.getFormatted_address());
                    entityOfResult.setLocation(new LatLng(latitude, longitude));
                    result.add(entityOfResult);

                }

                onSearchSuccess(result);
            }
        }
        else{
            onSearchFailed("Failed to fetch data");
        }
    }

    protected abstract void onSearchSuccess(List<APILocationResult> result);

    protected abstract void onSearchFailed(String message);
}