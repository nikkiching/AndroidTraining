package com.ching_chang.piggydiary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ching_Chang on 2015/4/10.
 */
public class ItemAdapter extends ArrayAdapter<Item> {
    private List<Item> mItems;
    private int mResource;
    private static final int THUMBNAIL_SIZE = 100;
    private String[] mLabelPayment = getContext().getResources().getStringArray(R.array.labelPayment);
    private String[] mLabelPaymentValue = getContext().getResources().getStringArray(R.array.labelPaymentValue);
    private String[] mLabelIncome = getContext().getResources().getStringArray(R.array.labelIncome);
    public ItemAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        this.mResource = resource;
        this.mItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Item item = getItem(position); //讀取目前位置的物件
        LinearLayout itemView;
        if (convertView == null){
            // Construct item view components
            itemView = new LinearLayout(getContext());
            LayoutInflater inflater = LayoutInflater.from(getContext());
            inflater.inflate(mResource, itemView, true);
        }
        else {
            itemView = (LinearLayout) convertView;
        }
        // Find view
        TextView label = (TextView) itemView.findViewById(R.id.item_label);
        TextView date = (TextView) itemView.findViewById(R.id.item_date);
        TextView money = (TextView) itemView.findViewById(R.id.item_money);
        TextView note = (TextView) itemView.findViewById(R.id.item_note);
        ImageView image = (ImageView) itemView.findViewById(R.id.item_image);
        int labelP = item.getCategory();
        if (labelP < mLabelPaymentValue.length ){
            label.setText(mLabelPayment[labelP]);
        }else{
            labelP -= mLabelPaymentValue.length;
            label.setText(mLabelIncome[labelP]);
        }
        date.setText(UpdateActivity.mDateFormat.format(new Date(item.getDate())));
        money.setText(Double.toString((item.getMoney())));
        note.setText(item.getNote());
        String imageString = item.getImage();
        if (!TextUtils.isEmpty(imageString)) {
            byte[] imageData = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bmp, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
            image.setImageBitmap(thumbnail);
            bmp.recycle();
        }else {
            image.setImageResource(R.mipmap.ic_action_photo);
        }
        return itemView;
    }

    // Set Item by Index
    public void set(int index, Item item) {
        if (index >= 0 && index < mItems.size()){
            mItems.set(index, item);
            notifyDataSetChanged();
        }
    }

    // Get Item by Index
    public Item get(int index) {
        return mItems.get(index);
    }

}
