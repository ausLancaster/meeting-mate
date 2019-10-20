package com.team33.meetingmate.ui.files;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team33.meetingmate.service.AcceptThread;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.team33.meetingmate.AppActivity;
import com.team33.meetingmate.Constants;
import com.team33.meetingmate.R;

import static com.team33.meetingmate.MainActivity.TAG;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class FilesFragment extends Fragment {
    private final static String TAG = "FilesFragment";

    private FilesViewModel filesViewModel;
    private View view;
    private AppActivity activity;
    private FilesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        filesViewModel =
                ViewModelProviders.of(this).get(FilesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_files, container, false);
/*
        ArrayList<FileItem> files = new ArrayList<>();
        adapter = new FilesAdapter(Objects.requireNonNull(getActivity()), R.layout.file_list_item, files);
        ListView listView = root.findViewById(R.id.file_list_view);
        listView.setAdapter(adapter);


        // Fetch links to all files
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child(Constants.ATTACHMENT_REF);
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                            Log.d(TAG, "Downloading files for event: " + prefix.getPath());
                            StorageReference listRef = storage.getReference().child(prefix.getPath());
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
                                                            adapter.add(file);
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
                                                            adapter.add(file);
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Log.d(TAG, "onFailure: when trying to fetch metadata of file:" + item + ":" + exception.getMessage());
                                                    }
                                                });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: when trying to fetch metadata of files for event:" + listRef + ":" + e.getMessage());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: when trying to fetch attachments :" + e.getMessage());
                    }
                });


         */
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        view = getView();
        activity = ((AppActivity) getActivity());

        ImageView imageView = (ImageView)view.findViewById(R.id.test_image);
        System.out.println(imageView.toString());

        ListView listReceivedFiles = (ListView) view.findViewById(R.id.list_received_files);
        listReceivedFiles.setAdapter(activity.getReceivedFilesArrayAdapter());
        listReceivedFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                byte[] imageByteArray = (byte[]) parent.getAdapter().getItem(position);
                System.out.println(imageByteArray.length);

//                try {
//                    adapter.add((FileItem) new ObjectInputStream(new ByteArrayInputStream(imageByteArray)).readObject());
//                } catch (IOException e) {
//                    e.printStack
                try {
                    Bitmap bm = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                    DisplayMetrics dm = new DisplayMetrics();
                    activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                    imageView.setMinimumHeight(dm.heightPixels);
                    imageView.setMinimumWidth(dm.widthPixels);
                    imageView.setImageBitmap(bm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button receive = view.findViewById(R.id.button_receive);
        receive.setOnClickListener(v -> {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }
            Log.d(TAG, "Bluetooth: Device discoverable");
            AcceptThread acceptThread = new AcceptThread();
            acceptThread.start();
        });
    }
}