package com.suda.mychatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suda.mychatapp.R;
import com.suda.mychatapp.activity.FriendInfoActivity;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.Message;
import com.suda.mychatapp.utils.DateFmUtil;
import com.suda.mychatapp.utils.ImageCacheUtil;
import com.suda.mychatapp.utils.TextUtil;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Suda on 2015/7/21.
 */
public class MessageAdapter extends BaseAdapter {


    public MessageAdapter(Context context,
                          List<Message> arrayList) {
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

            convertView = mInflater.inflate(R.layout.message_item, null);
            holder.mFromIcon = (CircleImageView) convertView.findViewById(R.id.from_icon);
            holder.mTvFromMsg = (TextView) convertView.findViewById(R.id.id_from_msg_info);
            holder.mTvFromUserNikeName = (TextView) convertView.findViewById(R.id.from_nikename);
            holder.mFromll = (LinearLayout) convertView.findViewById(R.id.from_ll);

            holder.mToIcon = (CircleImageView) convertView.findViewById(R.id.to_icon);
            holder.mTvToMsg = (TextView) convertView.findViewById(R.id.id_to_msg_info);
            holder.mTvToUserNikeName = (TextView) convertView.findViewById(R.id.to_nikename);
            holder.mToll = (LinearLayout) convertView.findViewById(R.id.to_ll);

            holder.mTvDate = (TextView) convertView.findViewById(R.id.msg_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        bindDataToView(holder, position);
        return convertView;
    }

    public void bindDataToView(final ViewHolder holder, final int position) {

        Date time = new Date(arrayList.get(position).getAvimTypedMessage().getTimestamp());

        boolean show = false;
        if (position > 0) {
            long beforetime = arrayList.get(position - 1).getAvimTypedMessage().getMessageType();
            show = arrayList.get(position).getAvimTypedMessage().getTimestamp() - arrayList.get(position - 1).getAvimTypedMessage().getTimestamp() > 1000 * 60 * 3;
        }

        if (show) {
            holder.mTvDate.setVisibility(View.VISIBLE);
            holder.mTvDate.setText(DateFmUtil.fmDate(time));
        } else {
            holder.mTvDate.setVisibility(View.INVISIBLE);
        }

        if (isMe(arrayList.get(position).getUsername())) {
            holder.mFromll.setVisibility(View.INVISIBLE);
            holder.mToll.setVisibility(View.VISIBLE);
            holder.mTvToMsg.setText(arrayList.get(position).getMsg());
            holder.mTvToUserNikeName.setText(TextUtil.isTextEmpty(arrayList.get(position).getNikename()) ?
                    arrayList.get(position).getUsername() : arrayList.get(position).getNikename());
            ImageCacheUtil.showPicture(context, arrayList.get(position).getIconurl(), new ImageCacheUtil.CallBack() {
                @Override
                public void done(final Bitmap bitmap) {
                    holder.mToIcon.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.mToIcon.setImageBitmap(bitmap);
                        }
                    });
                }
            });

        } else {
            holder.mFromll.setVisibility(View.VISIBLE);
            holder.mToll.setVisibility(View.INVISIBLE);
            holder.mTvFromMsg.setText(arrayList.get(position).getMsg());
            holder.mTvFromUserNikeName.setText(TextUtil.isTextEmpty(arrayList.get(position).getNikename()) ?
                    arrayList.get(position).getUsername() : arrayList.get(position).getNikename());
            ImageCacheUtil.showPicture(context, arrayList.get(position).getIconurl(), new ImageCacheUtil.CallBack() {
                @Override
                public void done(final Bitmap bitmap) {
                    holder.mFromIcon.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.mFromIcon.setImageBitmap(bitmap);
                        }
                    });
                }
            });


        }

        holder.mFromIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, FriendInfoActivity.class);
                it.putExtra(EXTRA_USERNAME, arrayList.get(position).getUsername());
                context.startActivity(it);
            }
        });

        holder.mToIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, FriendInfoActivity.class);
                it.putExtra(EXTRA_USERNAME, arrayList.get(position).getUsername());
                context.startActivity(it);
            }
        });

    }

    boolean isMe(String username) {
        return MyAVUser.getCurrentUser().getUsername().equals(username);
    }

    public class ViewHolder {
        public TextView mTvFromMsg;
        public TextView mTvToMsg;
        public TextView mTvFromUserNikeName;
        public TextView mTvToUserNikeName;
        public TextView mTvDate;
        public CircleImageView mFromIcon;
        public CircleImageView mToIcon;
        public LinearLayout mFromll;
        public LinearLayout mToll;

    }

    private Context context;
    private LayoutInflater mInflater;
    private List<Message> arrayList;
    private static final String EXTRA_USERNAME = "username";
}
