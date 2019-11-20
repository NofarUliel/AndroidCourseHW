package com.example.addit.Model;

public class Item {
    private String id;
    private String name;
    private String image;
    private int amount;
    private double price;
    private boolean isMark;

    public Item(){}

    public Item(String id, String name, boolean isMark, int amount,double price) {
        this.id = id;
        this.name = name;
        this.isMark = isMark;
        this.image=" ";
        this.price=price;
        this.amount=amount;
    }
    public Item(String id, String name, boolean isMark, int amount,double price,String img) {
        this.id = id;
        this.name = name;
        this.isMark = isMark;
        this.image=img;
        this.price=price;
        this.amount=amount;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isMark() {
        return isMark;
    }

    public void setMark(boolean mark) {
        isMark = mark;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                ", isMark=" + isMark +
                '}';
    }
}
