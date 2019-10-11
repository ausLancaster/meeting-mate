package com.team33.meetingmate;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CreateEventActivity extends AppCompatActivity {

    private static final String PREF_ACCOUNT_NAME = "accountName";

    static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;

    static final int REQUEST_AUTHORIZATION = 1;

    static final int REQUEST_ACCOUNT_PICKER = 2;

    private static final String APPLICATION_NAME = "Google Calendar API";

    final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();

    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    Calendar service;
    GoogleAccountCredential credential;

    List<Event> items;

    int numAsyncTasks;

    private java.util.Calendar calendar;
    private TextView dateView;
    private int year, month, day, startHour, startMin, endHour, endMin;

    private TextView startTime;
    private TextView endTime;
    private String format = "";

    private Place selectedPlace;

    public Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_create_event);

        dateView = findViewById(R.id.textView2);
        calendar = java.util.Calendar.getInstance();
        year = calendar.get(java.util.Calendar.YEAR);

        month = calendar.get(java.util.Calendar.MONTH);
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        startTime = findViewById(R.id.textView4);
        endTime = findViewById(R.id.textView7);

        startHour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        startMin = calendar.get(java.util.Calendar.MINUTE);
        endHour = startHour + 1;
        endMin = startMin;
        showTime(startTime, startHour, startMin);
        showTime(endTime, endHour, endMin);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.GET_ACCOUNTS},
                1);

        credential =
                GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        service =
                new Calendar.Builder(httpTransport, jsonFactory, credential)
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key), Locale.US);
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("Calendar", "Place: " + place.getName() + ", " + place.getId());
                selectedPlace = place;
            }

            @Override
            public void onError(Status status) {
                Log.i("Calendar", "An error occurred: " + status);
            }
        });
    }

    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        } else if (id == 998) {
            return new TimePickerDialog(this, myStartTimeListener, startHour, startMin, false);
        } else if (id == 997) {
            return new TimePickerDialog(this, myEndTimeListener, endHour, endMin, false);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private TimePickerDialog.OnTimeSetListener myStartTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            showTime(startTime, i, i1);
        }
    };

    private TimePickerDialog.OnTimeSetListener myEndTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            showTime(endTime, i, i1);
        }
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    public void setStartTime(View view) {
        showDialog(998);
    }
    public void setEndTime(View view) {
        showDialog(997);
    }

    public void showTime(TextView timeTextView, int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        timeTextView.setText(new StringBuilder().append(hour).append(" : ").append(min)
                .append(" ").append(format));
    }



    /** Check that Google Play services APK is installed and up to date. */
    private boolean checkGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    private void haveGooglePlayServices() {
        // check if there is already an account selected
        if (credential.getSelectedAccountName() == null) {
            // ask user to choose account
            chooseAccount();
        } else {
            // load calendars
            AsyncLoadTasks.run(this);
        }
    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                Dialog dialog =
                        GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, CreateEventActivity.this,
                                REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    void refreshView() {
        for (Event event : items) {
            Log.d("Calendar", event.getSummary());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkGooglePlayServicesAvailable()) {
            haveGooglePlayServices();
        }
    }

    public void createEvent(View view) {

        EditText editName = findViewById(R.id.editText);
        event = new Event()
            .setSummary(editName.getText().toString());

        if (selectedPlace != null) {
            event.setLocation(selectedPlace.toString());
        }

        Date startDate = new Date(year, month, day, startHour, endHour);
        DateTime startDateTime = new DateTime(startDate);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Australia/Melbourne");
        event.setStart(start);

        Date endDate = new Date(year, month, day, startHour, endHour);
        DateTime endDateTime = new DateTime(endDate);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Australia/Melbourne");
        event.setEnd(end);
        AsyncInsertEvent.run(this);

        Intent intent = new Intent(this, AppActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    haveGooglePlayServices();
                } else {
                    checkGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                    AsyncLoadTasks.run(this);
                } else {
                    chooseAccount();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        AsyncLoadTasks.run(this);
                    }
                }
                break;
        }
    }
}
