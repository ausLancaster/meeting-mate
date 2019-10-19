package com.team33.meetingmate.ui.files;

import java.util.Date;

public class FileItem {
    private String fileName;
    private int eventId;
    private String url;
    private String fileType;
    private Date createdTime;

    public FileItem(String fileName, int eventId, String url, String fileType, Date createdTime) {
        this.fileName = fileName;
        this.eventId = eventId;
        this.url = url;
        this.fileType = fileType;
        this.createdTime = createdTime;
    }

    public FileItem(String fileName) {
        this.fileName = fileName;
    }

    public synchronized void setFileMetaData(String fileType, Date createdTime) {
        this.fileType = fileType;
        this.createdTime = createdTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
