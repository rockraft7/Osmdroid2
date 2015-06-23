package com.faizalsidek.osmdroid;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorGeocodeParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import com.faizalsidek.osmdroid.custom.MyTileProvider;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.PathOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements LocationListener, MapEventsReceiver {
    public static final String TAG = "MainActivity";

    private MapView mapView;
    private EditText editText;
    private boolean init;
    private GeoPoint startPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init = false;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d("MainActivity", "Loading from xysource...");
        ITileSource tileSource = new XYTileSource("Mapnik", ResourceProxy.string.mapnik, 1, 20, 256, ".png", new String[] {"https://api.smartmapsapi.com/api/efa8ec1d-8379-40a6-bcbd-0471d8c01b53/tile/street/"});
        //ITileSource tileSource = new XYTileSource("Mapnik", ResourceProxy.string.mapnik, 1, 20, 256, "", new String[] {"http://58.26.53.55/gasset1/rest/services/AF/AF/MapServer/tile/"});
        MapTileProviderBase tileProvider = new MyTileProvider(getApplicationContext(), tileSource);
        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        mapView = new MapView(getApplicationContext(), 256, resourceProxy, tileProvider);

        MapView map = (MapView) findViewById(R.id.mapview);
        ViewGroup parent = (ViewGroup) map.getParent();
        int index = parent.indexOfChild(map);
        parent.removeView(map);
        parent.addView(mapView, index);

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

        mapView.setTileSource(tileSource);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        editText = (EditText) findViewById(R.id.address);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    executeLocatorTask(editText.getText().toString());
                    return true;
                }
                return false;
            }
        });
/*
        startPoint = new GeoPoint(3.01510286, 101.58992363);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(getResources().getDrawable(R.drawable.bonuspack_bubble));
        startMarker.setTitle("Start point");

        mapView.getOverlays().add(startMarker);
        mapView.invalidate();

        RoadManager roadManager = new OSRMRoadManager();
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(startPoint);
        waypoints.add(new GeoPoint(2.9352662740004121, 101.69111605100056));

        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, this);
        mapView.getOverlays().add(roadOverlay);
        mapView.invalidate();
*/
        resetLocation(3.141696, 101.668674);
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        mapView.getOverlays().add(0, mapEventsOverlay);
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        Log.d(TAG, "GEO: " + geoPoint.toDoubleString());

        GeoPoint northEast = (GeoPoint) mapView.getProjection().getNorthEast();
        GeoPoint southWest = (GeoPoint) mapView.getProjection().getSouthWest();

        Log.d(TAG, "BoundingBox: " + (northEast.getLatitudeE6()/1E6) + "," + (northEast.getLongitudeE6()/1E6));
        Log.d(TAG, "BoundingBox: " + (southWest.getLatitudeE6() / 1E6) + "," + (southWest.getLongitudeE6() / 1E6));

        List<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(new GeoPoint(3.139859619000049,101.66420801800007));
        waypoints.add(new GeoPoint(3.1398144680000541, 101.66471300600006));
        waypoints.add(new GeoPoint(3.1398234590000698, 101.66512229000006));
        waypoints.add(new GeoPoint(3.1399036480000291, 101.66536885500005));
        waypoints.add(new GeoPoint(3.140016097000057, 101.66554663600004));
        waypoints.add(new GeoPoint(3.1401802740000448, 101.66569538700008));
        waypoints.add(new GeoPoint(3.1403198770000245, 101.66578261200004));
        waypoints.add(new GeoPoint(3.1406613700000321, 101.66594099200006));

        Polyline jalanMedanKapas = new Polyline(getApplicationContext());
        jalanMedanKapas.setColor(Color.RED);
        jalanMedanKapas.setPoints(waypoints);
        mapView.getOverlays().add(jalanMedanKapas);
        mapView.invalidate();
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        Log.d(TAG, "GEO: " + geoPoint.toDoubleString());
        GeoPoint northEast = (GeoPoint) mapView.getProjection().getNorthEast();
        GeoPoint southWest = (GeoPoint) mapView.getProjection().getSouthWest();

        Log.d(TAG, "BoundingBox: " + (northEast.getLatitudeE6()/1E6) + "," + (northEast.getLongitudeE6()/1E6));
        Log.d(TAG, "BoundingBox: " + (southWest.getLatitudeE6()/1E6) + "," + (southWest.getLongitudeE6()/1E6));
        return false;
    }

    private void executeLocatorTask(String address) {
        Locator locator = Locator.createOnlineLocator("http://services.faizalsidek.com/geocoder/");

        Map<String, String> addressField = new HashMap<>();
        addressField.put("SingleLine", address);

        LocatorGeocodeParameters parameters = new LocatorGeocodeParameters();
        parameters.setAddressFields(addressField);
        try {
            List<LocatorGeocodeResult> results = locator.geocode(parameters);
            if (results != null && results.size() != 0) {
                LocatorGeocodeResult result = results.get(0);
                Log.d(TAG, "X: " + result.getLocation().getX());
                Log.d(TAG, "Y: " + result.getLocation().getY());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    private class LocatorAsyncTask extends AsyncTask<LocatorFindParameters, Void, List<LocatorGeocodeResult>> {
        private Exception mException;

        @Override
        protected List<LocatorGeocodeResult> doInBackground(LocatorFindParameters... params) {
            mException = null;
            List<LocatorGeocodeResult> results = null;
            Locator locator = Locator.createOnlineLocator("http://services.faizalsidek.com/geocoder/");

            try {
                LocatorSuggestionParameters parameters = new LocatorSuggestionParameters("")
                results = locator.find(params[0]);
                locator.suggest()
            } catch (Exception e) {
                mException = e;
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<LocatorGeocodeResult> results) {
            if (mException != null) {
                Log.w("PlaceSearch", "LocatorSyncTask failed with:");
                mException.printStackTrace();
                return;
            }

            if (results.size() == 0) {
                return;
            }

            for (LocatorGeocodeResult result : results) {
                Log.d(TAG, "X: " + result.getLocation().getX());
                Log.d(TAG, "Y: " + result.getLocation().getY());
                resetLocation(result.getLocation().getY(), result.getLocation().getX());
            }
        }
    }
    */

    private void resetLocation(double latitude, double longitude) {
        IMapController mapController = mapView.getController();
        mapController.setZoom(17);
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!init) {
            //Log.d(TAG, "Reset to location: Latitude[" + location.getLatitude() + "], Longitude[" + location.getLongitude() + "]");
            //resetLocation(location.getLatitude(), location.getLongitude());
            //init = true;
            //Log.d(TAG, "New Boundingbox: " + mapView.getBoundingBox().toString());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
