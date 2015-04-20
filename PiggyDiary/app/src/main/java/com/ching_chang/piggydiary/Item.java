package com.ching_chang.piggydiary;

import java.io.Serializable;

/**
 * Created by Ching_Chang on 2015/4/10.
 */
public class Item implements Serializable{
    private long mId;
    private long mDate;
    private int mCategory;
    private int mSubCategory;
    private double mMoney;
    private String mNote;
    private String mImage;
    private String mImagePath;

    public Item(){
        this.mMoney = 0;
        this.mNote = "";
        this.mImage = null;
    }

    public Item(long id, long date, double money,  int category, int subCategory, String note, String image, String path) {
        mId = id;
        mDate = date;
        mCategory = category;
        mSubCategory = subCategory;
        mMoney = money;
        mNote = note;
        mImage = image;
        mImagePath = path;
    }
    public void setId(long id){
        this.mId = id;
    }

    public long getID() {
        return mId;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date){
        this.mDate = date;
    }

    public int getCategory() {
        return mCategory;

    }

    public void setCategory(int category) {
        this.mCategory = category;
    }

    public int getSubCategory() {
        return mSubCategory;
    }

    public void setSubCategory(int subCategory) {
        this.mSubCategory = subCategory;
    }

    public double getMoney(){
        return mMoney;
    }

    public void setMoney(double money){
        this.mMoney = money;
    }

    public String getNote(){
        return mNote;
    }

    public void setNote(String note) {
        this.mNote = note;
    }

    public void setImage(String image){
        this.mImage = image;
    }
    public String getImage(){
        return mImage;
    }

    public void setImagePath(String path) {
        mImagePath = path;
    }

    public String getImagePath(){
        return mImagePath;
    }
}
