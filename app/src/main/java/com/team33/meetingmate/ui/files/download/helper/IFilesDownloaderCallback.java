package com.team33.meetingmate.ui.files.download.helper;

import com.team33.meetingmate.ui.files.FileItem;

public interface IFilesDownloaderCallback {
    public void onSuccess(FileItem file);

    public void onFailure(Exception e);
}
