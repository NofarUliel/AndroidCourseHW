package com.example.addit.Model;



public class User {
    private String id;
    private String name;
    private String email;
    private String image;


    public User() {

    }
    public User(String id, String email,String name) {
        this.id = id;
        this.email = email;
        this.name=name;
        this.image="";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
