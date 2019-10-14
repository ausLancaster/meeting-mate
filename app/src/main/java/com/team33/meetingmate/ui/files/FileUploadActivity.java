package com.team33.meetingmate.ui.files;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team33.meetingmate.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileUploadActivity extends AppCompatActivity {
    private final static String TAG = "FileUploadActivity";
    private final static String ATTACHMENT_REF = "attachments";


    private Uri fileURI;
    private String fileName;
    private String fileExtension;
    private String fileType;
    private byte[] fileBytes;

    private RadioGroup radioGroup;
    private Button bluetooth;
    private Button upload;
    private TextView errorMessage;

    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        bluetooth = findViewById(R.id.button_bluetooth);
        upload = findViewById(R.id.button_upload);
        errorMessage = findViewById(R.id.error_message);
        radioGroup = findViewById(R.id.events_list);
        fetchEvents();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileURI = (Uri) extras.get("file_url");
            fileName = (String) extras.get("file_name");
            fileExtension = (String) extras.get("file_ext");
            fileType = extras.get("file_type") == null ? "" : (String) extras.get("file_type");
            fileBytes = extras.get("image_data") == null ? null : (byte[]) extras.get("image_data");
        }

        // Firebase storage
        mStorage = FirebaseStorage.getInstance().getReference();

        upload.setOnClickListener(v -> {
            uploadFile(radioGroup.getCheckedRadioButtonId());
            finish();
        });
    }

    private void uploadFile(int eventID) {
        String uploadFileName = fileName;
        if (fileExtension != null && !fileExtension.isEmpty()) {
            uploadFileName = fileName + "." + fileExtension;
        }

        StorageReference mountainsRef = mStorage
                .child(ATTACHMENT_REF)
                .child(Integer.toString(eventID))
                .child(uploadFileName);

        UploadTask uploadTask;
        if (fileType != null && fileType.equals("IMAGE")) {
            uploadTask = mountainsRef.putBytes(fileBytes);
        } else {
            uploadTask = mountainsRef.putFile(fileURI);
        }

        uploadTask.addOnFailureListener(exception -> {
            Log.d(TAG, "onFailure: " + exception.getMessage());
            Toast.makeText(
                    FileUploadActivity.this,
                    "Failed to attached file.",
                    Toast.LENGTH_LONG).show();
        }).addOnSuccessListener(taskSnapshot ->
                Toast.makeText(
                        FileUploadActivity.this,
                        "Successfully attached file.",
                        Toast.LENGTH_LONG).show());
    }

    @SuppressLint("SetTextI18n")
    private void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0) {
                            Log.d("Calendar", "Calendar is empty");
                            errorMessage.setText("Calendar is empty");
                        }
                        List<Map<String, Object>> eventsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            eventsList.add(document.getData());
                            Log.d("Calendar", document.getId() + " => " + document.getData());
                        }
                        Collections.sort(
                                eventsList,
                                (Map<String, Object> e1, Map<String, Object> e2) ->
                                        Long.compare((long) e1.get("startDate"), (long) e2.get("startDate"))
                        );

                        radioGroup.setOrientation(LinearLayout.VERTICAL);

                        for (int i = eventsList.size() - 1; i >= 0; i--) {
                            Map<String, Object> event = eventsList.get(i);

                            RadioButton radioButton = new RadioButton(FileUploadActivity.this);
                            long id = (long) event.get("id");
                            radioButton.setText((String) event.get("summary"));
                            radioButton.setId((int) id);
                            radioButton.setChecked(i == eventsList.size() - 1);

                            radioGroup.addView(radioButton);
                            upload.setEnabled(true);
                            bluetooth.setEnabled(true);
                        }
                    } else {
                        Log.d("Calendar", "Error getting documents: ", task.getException());
                        errorMessage.setText("Error while fetching events.");
                    }
                });
    }
}