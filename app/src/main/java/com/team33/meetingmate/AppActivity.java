package com.team33.meetingmate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AppActivity extends AppCompatActivity {

    private final static int CAMERA_RESULT_REQUEST_CODE = 100;
    private final static int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private final static int AUDIO_RECODING_PERMISSION_REQUEST_CODE = 102;
    private final static int DOCUMENT_RESULT_REQUEST_CODE = 103;
    private final static int AUDIO_RECORDING_RESULT_REQUEST_CODE = 104;


    private boolean isFabOpen;
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabMic;
    private FloatingActionButton fabCreateMeeting;
    private FloatingActionButton fabAddDocument;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_RESULT_REQUEST_CODE:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Toast.makeText(this, "got image " + photo.getHeight() + "x" + photo.getWidth(), Toast.LENGTH_LONG).show();
                    break;
                case DOCUMENT_RESULT_REQUEST_CODE:
                    String filePath = data.getData().getPath();
                    Toast.makeText(this, "got document from " + filePath, Toast.LENGTH_LONG).show();
                    break;
                case AUDIO_RECORDING_RESULT_REQUEST_CODE:
                    Uri audioUri = data.getData();
                    Toast.makeText(this, "got audio from uri: " + audioUri, Toast.LENGTH_LONG).show();
                    break;
            }
        }
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
}
