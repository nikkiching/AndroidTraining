package com.ching_chang.piggydiary;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Ching_Chang on 2015/4/10.
 */
public class Item implements Serializable{
    private long id;
    private long date;
    private int category;
    private int subCategory;
    private double money;
    private String note;
    private String image ;

    public Item(){
        this.money = 0;
        this.note = "";
        this.image = null;
    }

    public Item(long id, long date, double money,  int category, int subCategory, String note, String image ) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.subCategory = subCategory;
        this.money = money;
        this.note = note;
        this.image = image;
    }
    public void setId(long id){
        this.id = id;
    }

    public long getID() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date){
        this.date = date;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(int subCategory) {
        this.subCategory = subCategory;
    }

    public double getMoney(){
        return money;
    }

    public void setMoney(double money){
        this.money = money;
    }

    public String getNote(){
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return image;
    }
}
