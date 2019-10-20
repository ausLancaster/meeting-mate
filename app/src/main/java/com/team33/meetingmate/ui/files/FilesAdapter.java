package com.team33.meetingmate.ui.files;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.team33.meetingmate.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilesAdapter extends ArrayAdapter<FileItem> {

    private Context mContext;
    private List<FileItem> filesList;
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public FilesAdapter(@NonNull Context context, int resource, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<FileItem> files) {
        super(context, resource, files);

        mContext = context;
        filesList = files;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.file_list_item, parent, false);

        FileItem currentFile = filesList.get(position);

        listItem.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setDataAndType(Uri.parse(currentFile.getUrl()), currentFile.getFileType());
            mContext.startActivity(browserIntent);
        });

        WebView fileContent = listItem.findViewById(R.id.file_image);
        fileContent.setWebChromeClient(new WebChromeClient());

        switch (currentFile.getFileType()) {
            case "application/pdf":
                fileContent.setBackgroundColor(Color.WHITE);
                fileContent.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
                fileContent.loadUrl("file:///android_asset/pdf.png");
                break;
            case "audio/mp4":
                fileContent.setBackgroundColor(Color.WHITE);
                fileContent.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
                fileContent.loadUrl("file:///android_asset/sound.png");
                break;
            default:
                fileContent.loadUrl(currentFile.getUrl());
                break;
        }

        TextView name = listItem.findViewById(R.id.file_name);
        name.setText(currentFile.getFileName());

        TextView timeStamp = listItem.findViewById(R.id.file_timestamp);
        timeStamp.setText(dateFormat.format(currentFile.getCreatedTime()));


        return listItem;
    }
}
