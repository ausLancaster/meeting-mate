package com.team33.meetingmate.ui.files.firebase;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.team33.meetingmate.Constants;
import com.team33.meetingmate.ui.files.FileItem;

import java.util.Date;


public class FilesDownloader {
    private final static String TAG = "FilesDownloader";

    private FirebaseStorage storage;
    private IFilesDownloaderCallback callback;

    public FilesDownloader(IFilesDownloaderCallback callback) {
        this.callback = callback;
        this.storage = FirebaseStorage.getInstance();
    }

    public void downloadAllAttachmnets() {
        StorageReference listRef = storage.getReference().child(Constants.ATTACHMENT_REF);
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            downloadAttachmnetsForEvent(prefix.getName());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: when trying to fetch attachments :" + e.getMessage());
                        callback.onFailure(e);
                    }
                });
    }

    public void downloadAttachmnetsForEvent(String event) {
        Log.d(TAG, "Downloading files for event: " + event);
        StorageReference listRef = storage.getReference().child(Constants.ATTACHMENT_REF).child(event);
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            FileItem file = new FileItem(item.getName());
                            Log.d(TAG, "Getting file download url: " + item.toString());
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "Item " + item.getName() + " URL: " + uri.toString());
                                    file.setUrl(uri.toString());
                                    if (file.getFileType() != null) {
                                        callback.onSuccess(file);
                                    }
                                }
                            });


                            Log.d(TAG, "Downloading metadata of item: " + item.toString());
                            item.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                @Override
                                public void onSuccess(StorageMetadata storageMetadata) {
                                    file.setFileMetaData(
                                            storageMetadata.getContentType(),
                                            new Date(storageMetadata.getCreationTimeMillis()));
                                    if (file.getUrl() != null) {
                                        callback.onSuccess(file);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.d(TAG, "onFailure: when trying to fetch metadata of file:" + item + ":" + exception.getMessage());
                                    callback.onFailure(exception);
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: when trying to fetch metadata of files for event:" + listRef + ":" + e.getMessage());
                        callback.onFailure(e);
                    }
                });
    }
}
