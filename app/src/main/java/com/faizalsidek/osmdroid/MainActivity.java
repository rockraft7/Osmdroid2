package com.faizalsidek.osmdroid;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SearchView;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.CallbackListener;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import com.esri.core.tasks.geocode.LocatorSuggestionParameters;
import com.esri.core.tasks.geocode.LocatorSuggestionResult;
import com.faizalsidek.osmdroid.custom.MyTileProvider;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.modules.GEMFFileArchive;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.modules.TileWriter;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


public class MainActivity extends ActionBarActivity implements LocationListener {
    public static final String TAG = "MainActivity";

    private MapView mapView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d("MainActivity", "Loading from xysource...");
        ITileSource tileSource = new XYTileSource("Mapnik", ResourceProxy.string.mapnik, 1, 20, 256, ".png", new String[] {"https://api.smartmapsapi.com/api/efa8ec1d-8379-40a6-bcbd-0471d8c01b53/tile/street/"});
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

        resetLocation(3.01510286,101.58992363);

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

    private class MyCallbackListener implements CallbackListener<List<LocatorGeocodeResult>> {
        @Override
        public void onCallback(List<LocatorGeocodeResult> results) {
            if (results.size() != 0) {
                LocatorGeocodeResult result = results.get(0);
                Log.d(TAG, "X: " + result.getLocation().getX());
                Log.d(TAG, "Y: " + result.getLocation().getY());
            }
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
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
        //Log.d(TAG, "Reset to location: Latitude[" + location.getLatitude() + "], Longitude[" + location.getLongitude() + "]");
        //resetLocation(location.getLatitude(), location.getLongitude());
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
