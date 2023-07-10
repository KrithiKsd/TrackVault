package com.example.trackvault.Model;
/*
 * Author: Krithika Kasaragod
 * FileName: AlbumData.java
 */
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AlbumData {

    @SerializedName("data")
    private ArrayList<Album> albums;

    public List<Album> getAlbums() {
        return albums;
    }

}
