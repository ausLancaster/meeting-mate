package com.team33.meetingmate;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String PREF_ACCOUNT_NAME = "accountName";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    private Events events;
    private Calendar service;

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, Context context) throws IOException {
        // Load client secrets.
        AssetManager assetManager = context.getAssets();
        InputStream in = assetManager.open(CREDENTIALS_FILE_PATH);;
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        Log.d("Calendar", "Done Credentials");

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        Log.d("Calendar", "NExt");

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Log.d("Calendar", "NExt");

        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        Log.d("Calendar", "Credentials succeeded");
        return credential;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            // Build a new authorized API client service.
            /*final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, getApplicationContext()))
                    .setApplicationName(APPLICATION_NAME)
                    .build();*/
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(this, SCOPES);
            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
            //credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
            credential.setSelectedAccount(new Account("aus.lancaster@gmail.com", "com.team33.meetingmate"));

            final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
            service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            Log.d("Calendar", "Execute");
            new GetEvents().execute();

    }

    private class GetEvents extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());

            try {
                events = service.events().list("primary")
                        .setMaxResults(10)
                        .setTimeMin(now)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
            } catch (IOException e) {
                Log.d("Calendar", events != null ? events.toString() : "null");
            }

            return "";
        }

        @Override
        protected void onPostExecute(String token) {
            List<Event> items = events.getItems();
            if (items.isEmpty()) {
                Log.d("Calendar", "No upcoming events found.");
            } else {
                Log.d("Calendar", "Upcoming events");
                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        start = event.getStart().getDate();
                    }
                    Log.d("Calendar", event.getSummary() + " " + start);
                }
            }
        }
    }
}
