package com.suda.mychatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suda.mychatapp.R;
import com.suda.mychatapp.db.pojo.Friends;
import com.suda.mychatapp.util.TextUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Suda on 2015/7/21.
 */
public class FriendsAdpter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<Friends> arrayList;

    public FriendsAdpter(Context context,
                         ArrayList<Friends> arrayList) {
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

        return convertView;
    }

    public void bindDataToView(ViewHolder holder, int position) {
        holder.mTvsign.setVisibility(TextUtil.isTextEmpty(arrayList.get(position).getSign()) ? View.INVISIBLE : View.VISIBLE);
        holder.mTvsign.setText(arrayList.get(position).getSign());
        holder.mHeadIcon.setImageBitmap(arrayList.get(position).getIcon());
        holder.mTvnikeName.setText(TextUtil.isTextEmpty(arrayList.get(position).getNikeName()) ?
                arrayList.get(position).getUserName() : arrayList.get(position).getNikeName());
    }

    public class ViewHolder {
        public TextView mTvnikeName;
        public TextView mTvsign;
        public CircleImageView mHeadIcon;
    }
}
