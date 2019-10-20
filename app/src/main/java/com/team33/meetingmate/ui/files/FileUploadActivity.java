package com.team33.meetingmate.ui.files;

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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team33.meetingmate.Constants;
import com.team33.meetingmate.R;
import com.team33.meetingmate.firebase.events.EventsFetcher;
import com.team33.meetingmate.firebase.events.IEventsFetcherCallback;

import java.util.List;
import java.util.Map;

public class FileUploadActivity extends AppCompatActivity implements IEventsFetcherCallback {
    private final static String TAG = "FileUploadActivity";


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

        EventsFetcher eventsFetcher = new EventsFetcher(this);
        eventsFetcher.getAllEvents();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileURI = (Uri) extras.get(Constants.EXTRAS_FILE_URL);
            fileName = (String) extras.get(Constants.EXTRAS_FILE_NAME);
            fileExtension = (String) extras.get(Constants.EXTRAS_FILE_EXTENSION);

            Object fileTypeObject = extras.get(Constants.ExtrasFileType);
            fileType = fileTypeObject == null ? "" : (String) fileTypeObject;

            Object imageDataObject = extras.get(Constants.EXTRAS_IMAGE_DATA);
            fileBytes = imageDataObject == null ? null : (byte[]) imageDataObject;
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
                .child(Constants.ATTACHMENT_REF)
                .child(Integer.toString(eventID))
                .child(uploadFileName);

        UploadTask uploadTask;
        if (fileType != null && fileType.equals(Constants.IMAGE_FILE_TYPE) && fileBytes != null) {
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

    @Override
    public void onComplete(List<Map<String, Object>> eventsList) {
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
    }

    @Override
    public void onError(String error) {
        Log.d("Calendar", "Error getting documents: " + error);
        errorMessage.setText("Error while fetching events.");
    }
}