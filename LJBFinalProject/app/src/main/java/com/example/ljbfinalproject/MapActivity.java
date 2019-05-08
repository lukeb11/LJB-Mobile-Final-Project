package com.example.ljbfinalproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 42;
    private static final float DEFAULT_ZOOM = 0.5f;

    //Widgets
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private EditText mSearchText;
    private ImageView mGPS;
    private ImageView mSearchButton;
    private ImageView mAreaButton;
    private ImageView mUnitButton;


    //Variables
    private boolean mLocationPermissionGranted;
    private String unit;
    private List<LatLng> points;
    private List<Marker> markers;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mSearchText = findViewById(R.id.input_search);
        mSearchButton = findViewById(R.id.ic_magnify);
        mGPS = findViewById(R.id.ic_gps);
        mAreaButton = findViewById(R.id.area_button);
        mUnitButton = findViewById(R.id.ic_unit);
        points = new ArrayList<>();
        markers = new ArrayList<>();
        builder = new AlertDialog.Builder(this);
        unit = "meter";

        getLocationPermission();

        initSearch();

        Toast.makeText(this, "Click a marker to remove", Toast.LENGTH_LONG).show();
    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    private void initSearch() {
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();

            }
        });
        mGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        mAreaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePolygon();
            }
        });

        mUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUnit();
            }
        });
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: Locating");

        List<Address> list = new ArrayList<>();
        String searchText = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);

        try {
            list = geocoder.getFromLocationName(searchText, 1);
        } catch (IOException e) {
            Log.d(TAG, "geoLocate: Could not find the location" + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: location found = " + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                    DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private void makePolygon() {
        if (points.size() >= 3) {
            PolygonOptions polygonOptions = new PolygonOptions();
            for (int i = 0; i < points.size(); i++) {

                if (i != points.size()) {
                    Log.d(TAG, "makePolygon: Point " + i + " Lat=" + points.get(i).latitude
                            + " Lng=" + points.get(i).longitude);
                    polygonOptions.add(points.get(i));
                } else {
                    Log.d(TAG, "makePolygon: Point " + i + " Lat=" + points.get(0).latitude
                            + " Lng=" + points.get(0).longitude);
                    polygonOptions.add(points.get(0));
                }
            }
            polygonOptions.strokeColor(Color.RED);
            polygonOptions.fillColor(Color.GREEN);
            Polygon polygon = mMap.addPolygon(polygonOptions);
            displayArea();
        } else
            return;
    }

    private void displayArea() {
        DecimalFormat outputFormat = new DecimalFormat("#0.000");
        if (unit.compareTo("meter") == 0) {
            Toast.makeText(this, "Area: " +
                            outputFormat.format(SphericalUtil.computeArea(points)) + "m",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Area=" + SphericalUtil.computeArea(points));
        } else if (unit.compareTo("kilo") == 0) {
            Toast.makeText(this, "Area: " +
                            outputFormat.format(SphericalUtil.computeArea(points) / 1000) + "km",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Area=" + SphericalUtil.computeArea(points) / 1000);
        } else if (unit.compareTo("mile") == 0) {
            Toast.makeText(this, "Area: " +
                            outputFormat.format(SphericalUtil.computeArea(points) / 1609.344) + "mi",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Area=" + SphericalUtil.computeArea(points) / 1609.344);
        } else
            Toast.makeText(this, "INPUT ERROR",
                    Toast.LENGTH_LONG).show();
    }

    private void changeUnit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Unit");
        builder.setItems(new CharSequence[]{"Meters", "Kilometers", "Miles"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        unit = "meter";
                        Toast.makeText(getApplicationContext(),
                                "Measuring in Meters", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        unit = "kilo";
                        Toast.makeText(getApplicationContext(),
                                "Measuring in Kilometers", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        unit = "mile";
                        Toast.makeText(getApplicationContext(),
                                "Measuring in Miles", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void getLocationPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                init();
            } else {
                ActivityCompat.requestPermissions(this, permission,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permission,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;

                    //Initialize map
                    init();
                    hideSoftKeyboard();
                }
            }
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "GetDeviceLocation: Getting current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation == null) {
                                getDeviceLocation();
                            } else {
                                Log.d(TAG, "OnComplete: Found Location");
                                moveCamera(new LatLng(currentLocation.getLatitude(),
                                        currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                            }
                        } else {
                            Toast.makeText(MapActivity.this,
                                    "Cannot find current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "it's not working");
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void moveCamera(LatLng latlng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Moving camera to: Lat = " + latlng.latitude + " Lng = " +
                latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
        if(points.size() == 1 && markers.size() == 1) {
            markers.get(0).remove();
            points.remove(0); //Removes point
            markers.remove(0); //Removes marker
        }
        points.add(latlng);

        MarkerOptions options = new MarkerOptions()
                .position(latlng)
                .title(title);
        Marker marker = mMap.addMarker(options);
        markers.add(marker);

        //Removes a marker when it's clicked on
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                points.remove(markers.indexOf(marker));
                markers.remove(markers.indexOf(marker));
                marker.remove();
                return true;
            }
        });

    }

    public void endActivity(View view){
        Intent intent = new Intent();
        intent.putExtra("address", mSearchText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

}