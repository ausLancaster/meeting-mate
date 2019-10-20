package com.team33.meetingmate.ui.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team33.meetingmate.R;
import com.team33.meetingmate.TimeUtility;
import com.team33.meetingmate.ui.files.FileItem;
import com.team33.meetingmate.ui.files.FilesAdapter;
import com.team33.meetingmate.ui.files.firebase.FilesDownloader;
import com.team33.meetingmate.ui.files.firebase.IFilesDownloaderCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EventActivity extends AppCompatActivity implements IFilesDownloaderCallback {
    private final static String TAG = "EventActivity";
    private FilesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_show_event);

        final TextView name = findViewById(R.id.eventName);
        final TextView date = findViewById(R.id.eventDate);
        final TextView time = findViewById(R.id.eventTime);
        final TextView location = findViewById(R.id.eventLocation);

        Intent intent = getIntent();
        long id = intent.getLongExtra("id", 0);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Map<String, Object>> calendarList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("id").equals(id)) {
                                    Map<String, Object> event = document.getData();
                                    name.setText((String) event.get("summary"));
                                    date.setText(TimeUtility.formatDate(new Date((long) event.get("startDate"))));
                                    time.setText(TimeUtility.formatTime(new Date((long) event.get("startDate")))
                                            + " - " + TimeUtility.formatTime(new Date((long) event.get("endDate"))));
                                    location.setText((String) event.get("location"));
                                }
                                calendarList.add(document.getData());
                            }
                        } else {
                            Log.d("Calendar", "Error getting documents: ", task.getException());
                        }
                    }
                });

        adapter = new FilesAdapter(this, R.layout.file_list_item, new ArrayList<>());
        ListView listView = this.findViewById(R.id.event_file_list_view);
        listView.setAdapter(adapter);

        FilesDownloader downloader = new FilesDownloader(this);
        downloader.downloadAttachmnetsForEvent(Long.toString(id));
    }

    @Override
    public void onSuccess(FileItem file) {
        adapter.add(file);
    }

    @Override
    public void onFailure(Exception e) {
        Log.d(TAG, "onFailure: " + e.getMessage());
    }
}
