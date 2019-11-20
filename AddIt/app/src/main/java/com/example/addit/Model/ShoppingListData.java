package com.example.addit.Model;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListData {
    private String id;
    private String name;
    private String note;
    private String date;
    private String manager;
    private List<String> members;


    public ShoppingListData() {
    }


    public ShoppingListData(String id, String name, String note, String date,String manager) {
        this.id = id;
        this.name = name;
        this.note = note;
        this.date = date;
        this.manager=manager;
        members=new ArrayList<>();
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        return "ShoppingListData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", date='" + date + '\'' +
                ", manager='" + manager + '\'' +
                ", members=" + members +
                '}';
    }
}
