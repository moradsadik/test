package me.application.java.createobject;

import lombok.Data;

public class CreationObject {

    public static void main(String[] args) {
        // string to uppecase
    }




}

@Data
class Person{
    private int id;
    private int age;
    private Adresse adresse;
}
record Adresse(String rue, String ville, String pays){}