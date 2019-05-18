package com.yousef.facedetection;
public class image {
    private int id;
    private String name;
    private String price;
    private String num;
    private byte[] image;

    public image(String name, String price, byte[] image ,String num, int id) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.num=num;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }
    public void setnum(String name) {
        this.num = num;
    }

    public String getnum() {
        return num;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}