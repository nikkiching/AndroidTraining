package com.ching_chang.piggydiary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ching_Chang on 2015/4/10.
 */
public class ItemAdapter extends ArrayAdapter<Item> {
    private List<Item> items;
    private int resource;

    private String[] mLabel = new String[]
            {"食物", "家用", "交通",  "娛樂", "學習", "醫療", "雜項", "工作", "其他"};

    public ItemAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Item item = getItem(position); //讀取目前位置的物件
        LinearLayout itemView;
        if (convertView == null){
            // Construct item view components
            itemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater layout = (LayoutInflater) getContext().getSystemService(inflater);
            layout.inflate(resource, itemView, true);
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
        label.setText(mLabel[item.getCategory()]);

        date.setText(UpdateActivity.mDateFormat.format(new Date(item.getDate())));
        money.setText(Double.toString((item.getMoney())));
        note.setText(item.getNote());
        String imageString = item.getImage();
        if (imageString != null && !TextUtils.isEmpty(imageString)) {
            byte imageData[] = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            image.setImageBitmap(bmp);
        }
        return itemView;
    }

    // Set Item by Index
    public void set(int index, Item item) {
        if (index >= 0 && index < items.size()){
            items.set(index, item);
            notifyDataSetChanged();
        }
    }

    // Get Item by Index
    public Item get(int index) {
        return items.get(index);
    }

}
