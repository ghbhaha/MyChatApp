package com.suda.mychatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.suda.mychatapp.R;
import com.suda.mychatapp.activity.FriendInfoActivity;
import com.suda.mychatapp.db.pojo.User;
import com.suda.mychatapp.utils.DisplayImageOptionsUtil;
import com.suda.mychatapp.utils.TextUtil;
import com.suda.mychatapp.utils.UserPropUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Suda on 2015/7/21.
 */
public class FriendsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<User> arrayList;

    public FriendsAdapter(Context context,
                          ArrayList<User> arrayList) {
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

            convertView = mInflater.inflate(R.layout.friend_item, null);
            holder.mHeadIcon = (CircleImageView) convertView.findViewById(R.id.icon);
            holder.mTvnikeName = (TextView) convertView.findViewById(R.id.tv_nikename);
            holder.mTvsign = (TextView) convertView.findViewById(R.id.tv_sign);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        bindDataToView(holder, position);
        return convertView;
    }

    public void bindDataToView(final ViewHolder holder, final int position) {
        holder.mTvsign.setVisibility(TextUtil.isTextEmpty(arrayList.get(position).getSign()) ? View.INVISIBLE : View.VISIBLE);
        holder.mTvsign.setText(arrayList.get(position).getSign());

        holder.mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo(position);
            }
        });

        ImageLoader.getInstance().displayImage(arrayList.get(position).getIconUrl(), holder.mHeadIcon, DisplayImageOptionsUtil.OPTION_1);
/*
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
*/

        holder.mTvnikeName.setText(UserPropUtil.getNikeNameByUser(arrayList.get(position)));
    }

    public void showInfo(int position) {
        Intent it = new Intent(context, FriendInfoActivity.class);
        it.putExtra(EXTRA_USERNAME, arrayList.get(position).getUserName());
        context.startActivity(it);
    }

    public class ViewHolder {
        public TextView mTvnikeName;
        public TextView mTvsign;
        public CircleImageView mHeadIcon;
    }

    private static final String EXTRA_USERNAME = "username";
}
