package com.example.trackvault;

/*
 * Author: Krithika Kasaragod
 * FileName: Api.java
 */
import com.example.trackvault.Model.AlbumData;
import com.example.trackvault.Model.AlbumTrackData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @GET("/album/{id}/tracks")
    Call<AlbumTrackData> getAllPhotos(@Path("id") int id);


    @GET("/search/album")
    Call<AlbumData> getAlbums(@Query("q") String q);
}
