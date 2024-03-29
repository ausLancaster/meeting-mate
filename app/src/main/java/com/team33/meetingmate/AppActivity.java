package com.team33.meetingmate;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team33.meetingmate.ui.files.FileUploadActivity;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AppActivity extends AppCompatActivity {

    private static final String TAG = "AppActivity";
    private final static int CAMERA_RESULT_REQUEST_CODE = 100;
    private final static int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private final static int AUDIO_RECODING_PERMISSION_REQUEST_CODE = 102;
    private final static int DOCUMENT_RESULT_REQUEST_CODE = 103;
    private final static int AUDIO_RECORDING_RESULT_REQUEST_CODE = 104;
    private final static int REQUEST_ENABLE_BT = 200;

    private boolean isFabOpen;
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabMic;
    private FloatingActionButton fabCreateMeeting;
    private FloatingActionButton fabAddDocument;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;

    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver bluetoothBroadcastReceiver;

    private StorageReference mStorage;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        CustomBottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_calendar,
                R.id.navigation_files,
                R.id.navigation_notifications,
                R.id.navigation_settings
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        // Firebase storage
        mStorage = FirebaseStorage.getInstance().getReference();

        // FAB buttons
        FloatingActionButton fab = findViewById(R.id.fab_plus);
        fabCamera = findViewById(R.id.fab_camera);
        fabMic = findViewById(R.id.fab_mic);
        fabAddDocument = findViewById(R.id.fab_add_document);
        fabCreateMeeting = findViewById(R.id.fab_create_meeting);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFabOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                if (ContextCompat.checkSelfPermission(AppActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            AppActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_RESULT_REQUEST_CODE);
                }
            }
        });

        fabAddDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(intent, DOCUMENT_RESULT_REQUEST_CODE);
            }
        });

        fabMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                if (ContextCompat.checkSelfPermission(AppActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            AppActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            AUDIO_RECODING_PERMISSION_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    startActivityForResult(intent, AUDIO_RECORDING_RESULT_REQUEST_CODE);
                }
            }
        });

        fabCreateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                Intent intent = new Intent(AppActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });

        // Location services
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                setLocation(loc);
//                String lat = Double.toString(loc.getLongitude());
//                String lng = Double.toString(loc.getLatitude());
//                Toast.makeText(getBaseContext(), "Location: {Lat: " + lat + ", Lng: " + lng + "}", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        // Check for location permissions
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Check if GPS or network is enabled
            ContentResolver contentResolver = getBaseContext().getContentResolver();
            Boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
            Boolean networkEnabled = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.NETWORK_PROVIDER);

            if (!gpsEnabled && !networkEnabled) {
                // No location provider enabled
                Toast.makeText(getBaseContext(), "Unable to fetch location", Toast.LENGTH_SHORT).show();
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
            }
        }

        if ( ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_RESULT_REQUEST_CODE);
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case AUDIO_RECODING_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    startActivityForResult(intent, AUDIO_RECORDING_RESULT_REQUEST_CODE);
                } else {
                    Toast.makeText(this, "Audio recoding permission denied", Toast.LENGTH_LONG)
                            .show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // TODO: use the real meeting ID later
        String eventId = "222";

        SimpleDateFormat m_sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        String timestamp = m_sdf.format(new Date());

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_RESULT_REQUEST_CODE:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    assert photo != null;
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                    Intent intent = new Intent(this, FileUploadActivity.class);
                    intent.putExtra(Constants.EXTRAS_IMAGE_DATA, stream.toByteArray());
                    intent.putExtra(Constants.EXTRAS_FILE_NAME, timestamp);
                    intent.putExtra(Constants.EXTRAS_FILE_EXTENSION, "jpeg");
                    intent.putExtra(Constants.ExtrasFileType, Constants.IMAGE_FILE_TYPE);
                    startActivity(intent);
                    break;
                case DOCUMENT_RESULT_REQUEST_CODE:
                case AUDIO_RECORDING_RESULT_REQUEST_CODE:
                    Uri uri = data.getData();
                    intent = new Intent(this, FileUploadActivity.class);
                    intent.putExtra(Constants.EXTRAS_FILE_URL, uri);
                    intent.putExtra(Constants.EXTRAS_FILE_NAME, getFileName(uri));
                    startActivity(intent);
                    break;
                case REQUEST_ENABLE_BT:
                    Log.d(TAG, "Bluetooth: Bluetooth successfully enabled");
                    break;
//                case BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE:
//                    Log.d(TAG, "Bluetooth: Bluetooth discovery successfully enabled");
//                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_ENABLE_BT:
                    Log.d(TAG, "Bluetooth: Bluetooth enabling cancelled");
                    break;
//                case BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE:
//                    Log.d(TAG, "Bluetooth: Bluetooth discovery enabling cancelled");
//                    break;
            }
        }
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                if (ContextCompat.checkSelfPermission(AppActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            AppActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_RESULT_REQUEST_CODE);
                }
            }
        });
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (fileName == null) {
            fileName = uri.getPath();
            int cut = fileName.lastIndexOf('/');
            if (cut != -1) {
                fileName = fileName.substring(cut + 1);
            }
        }


        Log.d(TAG, "onActivityResult: Filename: " + fileName);
        return fileName;
    }

    private void showFABMenu() {
        isFabOpen = true;

        fabAddDocument.animate().translationY(-getResources().getDimension(R.dimen.standard_20))
                .translationX(-getResources().getDimension(R.dimen.standard_65));
        fabCamera.animate().translationY(-getResources().getDimension(R.dimen.standard_58))
                .translationX(-getResources().getDimension(R.dimen.standard_28));
        fabMic.animate().translationY(-getResources().getDimension(R.dimen.standard_58))
                .translationX(getResources().getDimension(R.dimen.standard_28));
        fabCreateMeeting.animate().translationY(-getResources().getDimension(R.dimen.standard_20))
                .translationX(getResources().getDimension(R.dimen.standard_65));
    }

    private void closeFABMenu() {
        isFabOpen = false;

        fabAddDocument.animate().translationY(0).translationX(0);
        fabCamera.animate().translationY(0).translationX(0);
        fabMic.animate().translationY(0).translationX(0);
        fabCreateMeeting.animate().translationY(0).translationX(0);
    }

    public Date getTime() {
        return Calendar.getInstance().getTime();
    }

    public TimeZone getTimeZone() {
        return Calendar.getInstance().getTimeZone();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location loc) {
        location = loc;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public BroadcastReceiver getBluetoothBroadcastReceiver() {
        return bluetoothBroadcastReceiver;
    }

}
