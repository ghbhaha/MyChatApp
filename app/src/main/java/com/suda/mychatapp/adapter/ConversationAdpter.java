package com.suda.mychatapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suda.mychatapp.R;
import com.suda.mychatapp.db.pojo.LastMessage;
import com.suda.mychatapp.utils.DateFmUtil;
import com.suda.mychatapp.utils.ImageCacheUtil;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Suda on 2015/7/26.
 */
public class ConversationAdpter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<LastMessage> arrayList;

    public ConversationAdpter(Context context,
                              ArrayList<LastMessage> arrayList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {

            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.conversation_item, null);
            holder.mHeadIcon = (CircleImageView) convertView.findViewById(R.id.icon);
            holder.mTvnikeName = (TextView) convertView.findViewById(R.id.tv_nikename);
            holder.mTvLastMsg = (TextView) convertView.findViewById(R.id.tv_last_msg);
            holder.mTvLastTime = (TextView) convertView.findViewById(R.id.tv_last_time);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        bindDataToView(holder, position);
        return convertView;
    }

    public void bindDataToView(final ViewHolder holder, int position) {
        holder.mTvLastMsg.setText(arrayList.get(position).getLastMsg());

        ImageCacheUtil.showPicture(context, arrayList.get(position).getIconUrl(), new ImageCacheUtil.CallBack() {
            @Override
            public void done(final Bitmap bitmap) {
                holder.mHeadIcon.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.mHeadIcon.setImageBitmap(bitmap);
                    }
                });
            }
        });

        holder.mTvLastTime.setText(DateFmUtil.fmDate(new Date(arrayList.get(position).getLastTime())));
        holder.mTvnikeName.setText(arrayList.get(position).getNikeName());
    }

    public class ViewHolder {
        public TextView mTvnikeName;
        public TextView mTvLastMsg;
        public TextView mTvLastTime;
        public CircleImageView mHeadIcon;
    }
}
