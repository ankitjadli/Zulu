package com.example.ankit.supbruh;

public class users {

    public String name;
    public String image;
    public String status;
    public String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public users(String name, String image, String status, String id) {

        this.name = name;
        this.image = image;
        this.status = status;
        this.id = id;
    }

    public users() {

    }
}
