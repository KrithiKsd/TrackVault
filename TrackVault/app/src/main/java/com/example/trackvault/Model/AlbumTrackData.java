package com.example.trackvault.Model;
/*
 * Author: Krithika Kasaragod
 * FileName: AlbumTrackData.java
 */
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AlbumTrackData {
    @SerializedName("data")
    private ArrayList<AlbumTracks> albums;

    public List<AlbumTracks> getSponsors() {
        return albums;
    }

}
