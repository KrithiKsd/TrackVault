package com.example.trackvault.Model;
/*
 * Author: Krithika Kasaragod
 * FileName: Track.java
 */
import java.io.Serializable;

public class Track implements Serializable {
    String trackId, docID, uID,mixName,track, duration, preview, image;

    public Track() {
    }


    public Track(String trackId, String docID, String uID, String mixName, String track, String duration, String preview, String image) {
        this.trackId = trackId;
        this.docID = docID;
        this.uID = uID;
        this.mixName = mixName;
        this.track = track;
        this.duration = duration;
        this.preview = preview;
        this.image= image;
    }

    public String getImage() {
        return image;
    }

    public String getPreview() {
        return preview;
    }


    public String getDocID() {
        return docID;
    }


    public String getuID() {
        return uID;
    }

    public String getTrack() {
        return track;
    }


    public String getDuration() {
        return duration;
    }

    public String getTrackId() {
        return trackId;
    }

}
