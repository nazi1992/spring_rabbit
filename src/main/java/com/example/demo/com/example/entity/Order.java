package com.example.demo.com.example.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/1/6 0006.
 */
public class Order implements Serializable{

    private static final long serialVersionUID = -1377984008223208923L;
    private String id;
    private String price;
    private int num;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", price='" + price + '\'' +
                ", num=" + num +
                '}';
    }
}
