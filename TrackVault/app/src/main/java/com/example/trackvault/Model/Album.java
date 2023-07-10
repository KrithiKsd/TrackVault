package com.example.trackvault.Model;
/*
 * Author: Krithika Kasaragod
 * FileName: Album.java
 */
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Album implements Serializable {
    String id;
    @SerializedName("cover_big")
    String albumImage;
    @SerializedName("title")
    String albumTitle;

   @SerializedName("nb_tracks")
    String albumNumber;
   @SerializedName("artist")
    artist artist;


    public Album.artist getArtist() {
        return artist;
    }

    public Album() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getAlbumNumber() {
        return albumNumber;
    }


    public class artist implements Serializable{
        @SerializedName("name")
        String artistName;

        @SerializedName("picture_small")
        String artistImage;

        public String getArtistName() {
            return artistName;
        }

        public String getArtistImage() {
            return artistImage;
        }

    }
}
