package com.team33.meetingmate.ui.files;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.team33.meetingmate.R;
import com.team33.meetingmate.firebase.files.FilesDownloader;
import com.team33.meetingmate.firebase.files.IFilesDownloaderCallback;

import java.util.ArrayList;
import java.util.Objects;

public class FilesListFragment extends Fragment implements IFilesDownloaderCallback {
    private final static String TAG = "FilesFragment";
    private FilesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_files_list, container, false);

        adapter = new FilesAdapter(Objects.requireNonNull(getActivity()), R.layout.file_list_item, new ArrayList<>());
        ListView listView = root.findViewById(R.id.file_list_view);
        listView.setAdapter(adapter);

        FilesDownloader downloader = new FilesDownloader(this);
        downloader.downloadAllAttachmnets();

        return root;
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

