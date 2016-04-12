package plbtw.plbtw_0274.API;

import android.content.Context;
import android.os.AsyncTask;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import plbtw.plbtw_0274.Model.address.AddressComponentBean;
import plbtw.plbtw_0274.Model.address.ArrayofSearchResultBean;
import plbtw.plbtw_0274.Model.address.SearchResultBean;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by dedeeko on 1/4/15.
 */
public abstract class TaskGetReverseRoute extends AsyncTask<LatLng, Void, Boolean> {
    private Context context;
    RestAdapter restAdapter;
    ArrayofSearchResultBean response;
    private LatLng param;

    public TaskGetReverseRoute(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        restAdapter = BaseAPI.getGoogleGeocodeAPI();
    }

    @Override
    protected Boolean doInBackground(LatLng... location) {
        GoogleSearchAPI methods = restAdapter.create(GoogleSearchAPI.class);
        param = location[0];
        try {
            response = methods.getLocation(param.latitude+","+param.longitude);
        }
        catch (RetrofitError e){
            e.printStackTrace();
            onSearchFailed(e.getResponse().getReason());
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
                SearchResultBean searchEntity = results.get(0);
                List<AddressComponentBean> address = searchEntity.getAddress_components();

                String route = "";
                String country = "";

                for(int i = 0;i<address.size();i++){
                    AddressComponentBean entity = address.get(i);
                    List<String> type = entity.getTypes();

                    if(type.contains("route")){
                        route = entity.getLong_name();
                    }
                    else if(type.contains("country")){
                        country = entity.getLong_name();
                    }
                }
                onSearchSuccess(route,country);
            }
        }
        else{
            onSearchFailed("Failed to fetch data");
        }
    }

    protected abstract void onSearchSuccess(String routeName,String countryName);

    protected abstract void onSearchFailed(String message);
}