package com.suda.mychatapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suda.mychatapp.MyApplication;
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
public class ConversationAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<LastMessage> arrayList;

    public ConversationAdapter(Context context,
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
            holder.mTvNikeName = (TextView) convertView.findViewById(R.id.tv_nikename);
            holder.mTvLastMsg = (TextView) convertView.findViewById(R.id.tv_last_msg);
            holder.mTvLastTime = (TextView) convertView.findViewById(R.id.tv_last_time);
            holder.mTvUnreadCount = (TextView) convertView.findViewById(R.id.tv_unreadCount);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        bindDataToView(holder, position);
        return convertView;
    }

    public void bindDataToView(final ViewHolder holder, int position) {
        holder.mTvLastMsg.setText(arrayList.get(position).getLastMsg());

        ImageCacheUtil.showPicture(context, MyApplication.getDBHelper().
                getIconUrl(arrayList.get(position).getUserName()), new ImageCacheUtil.CallBack() {
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

        if (arrayList.get(position).getUnreadCount() > 99) {
            holder.mTvUnreadCount.setText("99+");
        } else {
            holder.mTvUnreadCount.setVisibility(arrayList.get(position).getUnreadCount() == 0 ? View.INVISIBLE : View.VISIBLE);
            holder.mTvUnreadCount.setText((arrayList.get(position).getUnreadCount() == 0 ? 0 : arrayList.get(position).getUnreadCount()) + "");
        }
        holder.mTvLastTime.setText(DateFmUtil.fmDate(new Date(arrayList.get(position).getLastTime())));
        holder.mTvNikeName.setText(arrayList.get(position).getNikeName());
    }

    public class ViewHolder {
        public TextView mTvNikeName;
        public TextView mTvLastMsg;
        public TextView mTvLastTime;
        public TextView mTvUnreadCount;
        public CircleImageView mHeadIcon;
    }
}
