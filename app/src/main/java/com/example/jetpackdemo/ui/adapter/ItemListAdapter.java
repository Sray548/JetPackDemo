package com.example.jetpackdemo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.databinding.DataBindingUtil;

import com.example.jetpackdemo.R;
import com.example.jetpackdemo.databinding.ListItemBinding;
import com.example.jetpackdemo.ui.setting.SettingFragment;

import java.util.ArrayList;
import java.util.List;


public class ItemListAdapter extends BaseAdapter {

    private List<String> mData = new ArrayList<>();
    private Context mContext;
    private int mSelectPosition;


    public ItemListAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemBinding bind;
        if (convertView == null) {
            bind = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_item, parent, false);
            convertView = bind.getRoot();
            convertView.setTag(bind);
        } else {
            bind = (ListItemBinding) convertView.getTag();
        }

        bind.name.setText(mData.get(position));
        bind.name.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        if (mSelectPosition == position) {
            bind.selectImg.setVisibility(View.VISIBLE);
        } else {
            bind.selectImg.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void select(int position) {
        this.mSelectPosition = position;
        notifyDataSetChanged();
    }
}
