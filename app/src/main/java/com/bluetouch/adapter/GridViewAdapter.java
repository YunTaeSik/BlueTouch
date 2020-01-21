package com.bluetouch.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluetouch.R;
import com.bluetouch.blueMain.BlueMainActivity;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by User on 2016-03-08.
 */
public class GridViewAdapter extends BaseAdapter { //블루터치 터치선택 화면 어댑터

    private Context context;
    public int count = 0;
    private int MaxCount = 3;
    private ViewHolder mHolder;
    private String strid;

    public GridViewAdapter(Context c, String strid) {
        this.context = c;
        this.strid = strid;
    }

    @Override
    public int getCount() {
        count = SharedPrefsUtils.getIntegerPreference(context,strid+"C", 0);
        if (count >= MaxCount) {
            count = MaxCount;
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {   //블루터치 터치선택 view 부분
        View v = convertView;
        if (v == null) {
            mHolder = new ViewHolder();
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.grid_item, null);
            mHolder.textview = (TextView) v.findViewById(R.id.grid_item_text);
            mHolder.grid_item_image = (ImageView) v.findViewById(R.id.grid_item_image);
            mHolder.grid_item_layout = (LinearLayout) v.findViewById(R.id.grid_item_layout);
            String countText = Integer.toString(position + 1);
            String touchText5 = SharedPrefsUtils.getStringPreference(context, strid + "T" + countText);
            mHolder.textview.setText(touchText5);

            mHolder.grid_item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BlueMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("strid", strid);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                    ;
                }
            });
        }
        return v;
    }

    public static class ViewHolder { //viewholder 정의
        TextView textview;
        ImageView grid_item_image;
        LinearLayout grid_item_layout;
    }

}
