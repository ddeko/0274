package plbtw.plbtw_0274;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sa90.materialarcmenu.ArcMenu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import co.aenterhy.toggleswitch.ToggleSwitchButton;
import plbtw.plbtw_0274.API.TaskGetReverseRoute;
import plbtw.plbtw_0274.API.TaskSearchLocationByAddress;
import plbtw.plbtw_0274.Model.address.APILocationResult;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, MapboxMap.OnMapLongClickListener{

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_LOCATION = 0;

    private static final int Rute_1A_CODE = 101;
    private static final int Rute_1B_CODE = 102;
    private static final int Rute_2A_CODE = 201;
    private static final int Rute_2B_CODE = 202;
    private static final int Rute_3A_CODE = 301;
    private static final int Rute_3B_CODE = 302;

    private boolean rute1a = false;
    private boolean rute1b = false;
    private boolean rute2a = false;
    private boolean rute2b = false;
    private boolean rute3a = false;
    private boolean rute3b = false;

    private Bundle savedInstanceState;
    private Drawer result = null;



    private String jsonAssets = "";
    public String currentStreet;
    public String currentCountry;

    private LatLng[] pointsArray;
    private LatLng currentLocation;

    private DrawGeoJSON drawJson;

    private PolylineOptions polyTemp = null;

    MapView mMapView;

    ArcMenu arcMenu;

    ToggleSwitchButton toggle;
    FloatingActionButton myLocation;

    private MapboxMap mapboxMap;

    private PolylineOptions poly1a;
    private PolylineOptions poly1b;
    private PolylineOptions poly2a;
    private PolylineOptions poly2b;
    private PolylineOptions poly3a;
    private PolylineOptions poly3b;


    private ImageButton button1A;
    private ImageButton button1B;
    private ImageButton button2A;
    private ImageButton button2B;
    private ImageButton button3A;
    private ImageButton button3B;


    protected TaskSearchLocationByAddress searchTask;

    Icon icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.myMapboxMapView);
        arcMenu = (ArcMenu) findViewById(R.id.arcMenu);

        button1A = (ImageButton) findViewById(R.id.rute1a);
        button1B = (ImageButton) findViewById(R.id.rute1b);
        button2A = (ImageButton) findViewById(R.id.rute2a);
        button2B = (ImageButton) findViewById(R.id.rute2b);
        button3A = (ImageButton) findViewById(R.id.rute3a);
        button3B = (ImageButton) findViewById(R.id.rute3b);

        myLocation = (FloatingActionButton) findViewById(R.id.mylocation);

        mMapView.onCreate(savedInstanceState);

        myLocation.setOnClickListener(this);

        button1A.setOnClickListener(this);
        button1B.setOnClickListener(this);
        button2A.setOnClickListener(this);
        button2B.setOnClickListener(this);
        button3A.setOnClickListener(this);
        button3B.setOnClickListener(this);



        IconFactory iconFactory = IconFactory.getInstance(this);
        Drawable iconDrawable = ContextCompat.getDrawable(this, R.drawable.marker2);
        icon = iconFactory.fromDrawable(iconDrawable);

        mMapView.getMapAsync(this);

        setNavigationBar();

        toggle = (ToggleSwitchButton) findViewById(R.id.toggle);



    }

    @Override
    public void onMapReady(final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        startAnim();
        // Set map style
        mapboxMap.setStyleUrl(Style.DARK);
        mapboxMap.setOnMapLongClickListener(this);

        // Set the camera's starting position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(-7.7941754998553705, 110.37401503000262)) // set the camera's center position
                .zoom(11)// set the camera's zoom level
                .tilt(20)  // set the camera's tilt
                .build();

        // Move the camera to that position
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 4000);

        toggle.setOnTriggerListener(new ToggleSwitchButton.OnTriggerListener() {
            @Override
            public void toggledUp() {
                CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .zoom(mapboxMap.getCameraPosition().zoom + 0.5f)
                        .tilt(20)  // set the camera's tilt
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2), 1500);
            }

            @Override
            public void toggledDown() {
                CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .zoom(mapboxMap.getCameraPosition().zoom - 0.5f)
                        .tilt(20)  // set the camera's tilt
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2), 1500);
            }
        });

        stopAnim();
    }



    private void onSearchLocation(final double latitude, final double longitude){


        new TaskGetReverseRoute(this) {
            @Override
            protected void onSearchSuccess(String routeName, String countryName) {

                stopAnim();
                currentLocation = new LatLng(latitude, longitude);
                String country, street;
                street = routeName;
                country = countryName;

                currentCountry = country;
                currentStreet = street;

                Log.e(TAG, routeName + "dsss" + countryName);
            }

            @Override
            protected void onSearchFailed(String message) {
                stopAnim();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new com.google.android.gms.maps.model.LatLng(latitude, longitude));
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if(v==button1A)
        {
            if(rute1a) {
                poly1a.getPolyline().remove();
                rute1a = false;
                button1A.setSelected(false);
            }else{
                setJsonAssets(Rute_1A_CODE);
                setPolyline(Rute_1A_CODE);
                poly1a.getPolyline().remove();
                drawJson = new DrawGeoJSON();
                drawJson.execute();
                rute1a = true;
                button1A.setSelected(true);
            }
        }
        else if(v==button1B)
        {
            if(rute1b) {
                poly1b.getPolyline().remove();
                rute1b = false;
                button1B.setSelected(false);
            }else {
                setJsonAssets(Rute_1B_CODE);
                setPolyline(Rute_1B_CODE);
                poly1b.getPolyline().remove();
                drawJson = new DrawGeoJSON();
                drawJson.execute();
                rute1b = true;
                button1B.setSelected(true);
            }

        }
        else if(v==button2A)
        {
            if(rute2a) {
                poly2a.getPolyline().remove();
                rute2a = false;
                button2A.setSelected(false);
            }else {
                setJsonAssets(Rute_2A_CODE);
                setPolyline(Rute_2A_CODE);
                poly2a.getPolyline().remove();
                drawJson = new DrawGeoJSON();
                drawJson.execute();
                rute2a = true;
                button2A.setSelected(true);
            }
        }
        else if(v==button2B)
        {
            if(rute2b) {
                poly2b.getPolyline().remove();
                rute2b = false;
                button2B.setSelected(false);
            }else {
                setJsonAssets(Rute_2B_CODE);
                setPolyline(Rute_2B_CODE);
                poly2b.getPolyline().remove();
                drawJson = new DrawGeoJSON();
                drawJson.execute();
                rute2b = true;
                button2B.setSelected(true);
            }
        }
        else if(v==button3A)
        {
            if(rute3a) {
                poly3a.getPolyline().remove();
                rute3a = false;
                button3A.setSelected(false);
            }else {
                setJsonAssets(Rute_3A_CODE);
                setPolyline(Rute_3A_CODE);
                poly3a.getPolyline().remove();
                drawJson = new DrawGeoJSON();
                drawJson.execute();
                rute3a = true;
                button3A.setSelected(true);
            }
        }
        else if(v==button3B)
        {
            if(rute3b) {
                poly3b.getPolyline().remove();
                rute3b = false;
                button3B.setSelected(false);
            }else {
                setJsonAssets(Rute_3B_CODE);
                setPolyline(Rute_3B_CODE);
                poly3b.getPolyline().remove();
                drawJson = new DrawGeoJSON();
                drawJson.execute();
                rute3b = true;
                button3B.setSelected(true);
            }
        }
        else if(v == myLocation)
        {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);

            } else {

                mapboxMap.setMyLocationEnabled(true);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(mapboxMap.getMyLocation().getLatitude(), mapboxMap.getMyLocation().getLongitude())) // set the camera's center position
                                .zoom(14)// set the camera's zoom level
                                .tilt(20)  // set the camera's tilt
                                .build();

                // Move the camera to that position
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3000);
                currentLocation = new LatLng(mapboxMap.getMyLocation().getLatitude(), mapboxMap.getMyLocation().getLongitude());
            }
        }

    }

    private String setJsonAssets(int code){

        if(code == 101)
        {
            jsonAssets = "rute1a.geojson";
        }
        else if(code == 102)
        {
            jsonAssets = "rute1b.geojson";
        }
        else if(code == 201)
        {
            jsonAssets = "rute2a.geojson";
        }
        else if(code == 202)
        {
            jsonAssets = "rute2b.geojson";
        }
        else if(code == 301)
        {
            jsonAssets = "rute3a.geojson";
        }
        else if(code == 302)
        {
            jsonAssets = "rute3b.geojson";
        }
        return jsonAssets;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        Log.e(TAG, mapboxMap.getMarkers().size() + " LatLong : [\n" + point.getLatitude() + ", " + point.getLongitude() + "\n],");
        onSearchLocation(point.getLatitude(),point.getLongitude());
        startAnim();
        mapboxMap.getMarkers().clear();

        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(point.getLatitude(), point.getLongitude()))
                .title("Location Tag")
                .snippet("Street : " + currentStreet +"\n" + "Country : " + currentCountry)
                .icon(icon));

    }



    private class DrawGeoJSON extends AsyncTask<Void, Void, List<LatLng>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnim();
        }

        @Override
        protected List<LatLng> doInBackground(Void... voids) {

            ArrayList<LatLng> points = new ArrayList<>();

            try {
                // Load GeoJSON file
                InputStream inputStream = getAssets().open(jsonAssets);
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }

                inputStream.close();

                // Parse JSON
                JSONObject json = new JSONObject(sb.toString());
                JSONArray features = json.getJSONArray("features");
                JSONObject feature = features.getJSONObject(0);
                JSONObject geometry = feature.getJSONObject("geometry");
                if (geometry != null) {
                    String type = geometry.getString("type");

                    // Our GeoJSON only has one feature: a line string
                    if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("LineString")) {

                        // Get the Coordinates
                        JSONArray coords = geometry.getJSONArray("coordinates");
                        for (int lc = 0; lc < coords.length(); lc++) {
                            JSONArray coord = coords.getJSONArray(lc);
                            LatLng latLng = new LatLng(coord.getDouble(0), coord.getDouble(1));
                            points.add(latLng);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception Loading GeoJSON: " + e.toString());
            }

            return points;
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            super.onPostExecute(points);

            if (points.size() > 0) {
                pointsArray = points.toArray(new LatLng[points.size()]);

                polyTemp.add(pointsArray);
                mapboxMap.addPolyline(polyTemp);

                stopAnim();
            }
        }
    }

    private PolylineOptions setPolyline(int code){

        if(code == 101)
        {
            poly1a = new PolylineOptions()
                    .color(Color.parseColor("#e52530"))
                    .width(2);

            polyTemp = poly1a;
        }
        else if(code == 102)
        {
            poly1b = new PolylineOptions()
                    .color(Color.parseColor("#f48917"))
                    .width(2);

            polyTemp = poly1b;
        }
        else if(code == 201)
        {
            poly2a = new PolylineOptions()
                    .color(Color.parseColor("#e2b710"))
                    .width(2);

            polyTemp = poly2a;
        }
        else if(code == 202)
        {
            poly2b = new PolylineOptions()
                    .color(Color.parseColor("#019771"))
                    .width(2);

            polyTemp = poly2b;
        }
        else if(code == 301)
        {
            poly3a = new PolylineOptions()
                    .color(Color.parseColor("#0092df"))
                    .width(2);

            polyTemp = poly3a;
        }
        else if(code == 302)
        {
            poly3b = new PolylineOptions()
                    .color(Color.parseColor("#c267ad"))
                    .width(2);

            polyTemp = poly3b;
        }
        return polyTemp;
    }

    void startAnim(){
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
    }

    void stopAnim(){
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapboxMap.setMyLocationEnabled(true);

                }
            }
        }
    }


    public void setNavigationBar()
    {
        if(result!=null)
            result.removeAllItems();

        // Create the AccountHeader

        result = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .addDrawerItems(
//                        new PrimaryDrawerItem().withName("Route List").withIcon(FontAwesome.Icon.faw_list).withIdentifier(1),
//                        new PrimaryDrawerItem().withName("Nearby Bus Stop").withIcon(FontAwesome.Icon.faw_bus).withIdentifier(2),
                        new SectionDrawerItem().withName("Map Style"),
                        new SecondaryDrawerItem().withName("Default").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(3),
                        new SecondaryDrawerItem().withName("Map Box Streets").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(4),
                        new SecondaryDrawerItem().withName("Satellite").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(5),
                        new SecondaryDrawerItem().withName("Light").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(6),
                        new SecondaryDrawerItem().withName("Emerald").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(7)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 3) {
                                mapboxMap.setStyle(Style.DARK);
                            } else if (drawerItem.getIdentifier() == 4) {
                                mapboxMap.setStyle(Style.MAPBOX_STREETS);
                            }else if (drawerItem.getIdentifier() == 5) {
                                mapboxMap.setStyle(Style.SATELLITE_STREETS);
                            }else if (drawerItem.getIdentifier() == 6) {
                                mapboxMap.setStyle(Style.LIGHT);
                            }else if (drawerItem.getIdentifier() == 7) {
                                mapboxMap.setStyle(Style.EMERALD);
                            }
                            if (intent != null) {

                            }
                        }
                        return false;
                    }
                })
                .withSelectedItem(-1)
                .withSavedInstance(savedInstanceState)
                .build();
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

}
