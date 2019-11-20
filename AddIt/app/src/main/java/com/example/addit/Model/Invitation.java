package com.example.addit.Model;

import java.util.Objects;

public class Invitation {

    private String id;
    private String senderID;
    private String receiverID;
    private boolean isAccepted;
    private String listID;

    public Invitation(){

    }

    public Invitation(String id, String senderID, String receiverID, boolean isAccepted, String listID) {
        this.id = id;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.isAccepted = isAccepted;
        this.listID = listID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    @Override
    public String toString() {
        return "Invitation{" +
                "id='" + id + '\'' +
                ", senderID='" + senderID + '\'' +
                ", receiverID='" + receiverID + '\'' +
                ", isAccepted=" + isAccepted +
                ", listID='" + listID + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invitation that = (Invitation) o;
        return isAccepted == that.isAccepted &&
                Objects.equals(senderID, that.senderID) &&
                Objects.equals(receiverID, that.receiverID) &&
                Objects.equals(listID, that.listID);
    }

}
