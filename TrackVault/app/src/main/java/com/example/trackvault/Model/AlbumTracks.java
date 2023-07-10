package com.example.trackvault.Model;
/*
 * Author: Krithika Kasaragod
 * FileName: AlbumTracks.java
 */
import java.io.Serializable;

public class AlbumTracks implements Serializable {
    String title,preview,duration;

    public AlbumTracks() {
    }

    public String getTitle() {
        return title;
    }

    public String getPreview() {
        return preview;
    }

    public String getDuration() {
        return duration;
    }

}
