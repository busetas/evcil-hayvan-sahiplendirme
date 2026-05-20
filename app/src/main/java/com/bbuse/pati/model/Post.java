package com.bbuse.pati.model;

public class Post {

    public String useremail;
    public String comment;
    public String imageName;
    public String adoptionType;
    public String animalType;
    public String gender;
    public String postId;

    public Post() {
        // Firebase için boş constructor şart
    }

    public Post(String email,
                String comment,
                String imageName,
                String adoptionType,
                String animalType,
                String gender) {

        this.useremail = email;
        this.comment = comment;
        this.imageName = imageName;
        this.adoptionType = adoptionType;
        this.animalType = animalType;
        this.gender = gender;
    }

}