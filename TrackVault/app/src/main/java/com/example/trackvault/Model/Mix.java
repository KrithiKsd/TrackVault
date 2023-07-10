package com.example.trackvault.Model;
/*
 * Author: Krithika Kasaragod
 * FileName: Mix.java
 */
import java.io.Serializable;
import java.util.ArrayList;

public class Mix implements Serializable {

    String did, uid, mixName, createdBy;
    int number_tracks;
    ArrayList<String> invitedUsers;

    public Mix() {
    }

    public Mix(String did, String uid, String mixName, String createdBy, int number_tracks, ArrayList<String> invitedUsers) {
        this.did = did;
        this.uid = uid;
        this.mixName = mixName;
        this.createdBy = createdBy;
        this.number_tracks = number_tracks;
        this.invitedUsers = invitedUsers;
    }

    public String getDid() {
        return did;
    }


    public String getUid() {
        return uid;
    }


    public String getMixName() {
        return mixName;
    }


    public String getCreatedBy() {
        return createdBy;
    }


    public int getNumber_tracks() {
        return number_tracks;
    }


    public ArrayList<String> getInvitedUsers() {
        return invitedUsers;
    }

}
