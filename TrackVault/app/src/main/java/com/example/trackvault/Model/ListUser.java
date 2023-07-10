package com.example.trackvault.Model;
/*
 * Author: Krithika Kasaragod
 * FileName: ListUser.java
 */

public class ListUser {
    String docId, UserId, name, userMail;

    public ListUser() {

    }

    public ListUser(String docId, String userId, String name, String userMail) {
        this.docId = docId;
        UserId = userId;
        this.name = name;
        this.userMail = userMail;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
