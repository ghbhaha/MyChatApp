package com.suda.mychatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.suda.mychatapp.Conf;
import com.suda.mychatapp.R;
import com.suda.mychatapp.activity.FriendInfoActivity;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.Message;
import com.suda.mychatapp.db.pojo.User;
import com.suda.mychatapp.utils.DateFmUtil;
import com.suda.mychatapp.utils.DisplayImageOptionsUtil;
import com.suda.mychatapp.utils.FaceUtil;
import com.suda.mychatapp.utils.TextUtil;
import com.suda.mychatapp.utils.gif.AnimatedGifDrawable;
import com.suda.mychatapp.utils.gif.AnimatedImageSpan;
import com.suda.mychatapp.utils.msg.MessageUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Suda on 2015/7/21.
 */
public class MessageAdapter extends BaseAdapter {


    public MessageAdapter(Context context,
                          List<AVIMTypedMessage> arrayList) {
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

        isGChat = Conf.GROUP_CONVERSATION_ID.equals(arrayList.get(position).getConversationId());

        bindDataToView(holder, position);
        return convertView;
    }

    public void bindDataToView(final ViewHolder holder, final int position) {

        Date time = new Date(arrayList.get(position).getTimestamp());
        boolean show;

        if (position > 0) {
            show = arrayList.get(position).getTimestamp()
                    - arrayList.get(position - 1).getTimestamp() > 1000 * 60 * 4;
        } else {
            //当前消息第一个显示时间
            show = true;
        }

        if (show) {
            holder.mTvDate.setVisibility(View.VISIBLE);
            holder.mTvDate.setText(DateFmUtil.fmDate(time));
        } else {
            holder.mTvDate.setVisibility(View.INVISIBLE);
        }

        if (isMe(arrayList.get(position).getFrom())) {
            holder.mFromll.setVisibility(View.GONE);
            holder.mToll.setVisibility(View.VISIBLE);


            SpannableStringBuilder sb = FaceUtil.handler(holder.mTvToMsg,
                    MessageUtil.getMsg(arrayList.get(position)), context);

            holder.mTvToMsg.setText(sb);
            holder.mTvToMsg.setTextColor(Color.WHITE);
            holder.mTvToUserNikeName.setVisibility(isGChat ? View.VISIBLE : View.GONE);

            UserBus.findUser(arrayList.get(position).getFrom(), new UserBus.CallBack2() {
                @Override
                public void done(User user) {
                    Message message = MessageUtil.aviMsgtoMsg(arrayList.get(position), user);
                    holder.mTvToUserNikeName.setText(TextUtil.isTextEmpty(message.getNikename()) ?
                            message.getUsername() : message.getNikename());
                    ImageLoader.getInstance().displayImage(message.getIconurl(), holder.mToIcon, DisplayImageOptionsUtil.OPTION_1);
                }
            });



   /*         ImageCacheUtil.showPicture(context, arrayList.get(position).getIconurl(), new ImageCacheUtil.CallBack() {
                @Override
                public void done(final Bitmap bitmap) {
                    holder.mToIcon.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.mToIcon.setImageBitmap(bitmap);
                        }
                    });
                }
            });*/

        } else {
            holder.mFromll.setVisibility(View.VISIBLE);
            holder.mToll.setVisibility(View.GONE);

            SpannableStringBuilder sb = FaceUtil.handler(holder.mTvFromMsg,
                    MessageUtil.getMsg(arrayList.get(position)), context);

            holder.mTvFromMsg.setText(sb);
            holder.mTvFromMsg.setTextColor(Color.BLACK);
            holder.mTvFromUserNikeName.setVisibility(isGChat ? View.VISIBLE : View.GONE);

            UserBus.findUser(arrayList.get(position).getFrom(), new UserBus.CallBack2() {
                @Override
                public void done(User user) {
                    Message message = MessageUtil.aviMsgtoMsg(arrayList.get(position), user);
                    holder.mTvFromUserNikeName.setText(TextUtil.isTextEmpty(message.getNikename()) ?
                            message.getUsername() : message.getNikename());
                    ImageLoader.getInstance().displayImage(message.getIconurl(), holder.mFromIcon, DisplayImageOptionsUtil.OPTION_1);
                }
            });

/*            ImageCacheUtil.showPicture(context, arrayList.get(position).getIconurl(), new ImageCacheUtil.CallBack() {
                @Override
                public void done(final Bitmap bitmap) {
                    holder.mFromIcon.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.mFromIcon.setImageBitmap(bitmap);
                        }
                    });
                }
            });*/

        }

        holder.mFromIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo(position);
            }
        });

        holder.mToIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo(position);
            }
        });

    }

    public void showInfo(int position) {
        Intent it = new Intent(context, FriendInfoActivity.class);
        it.putExtra(EXTRA_USERNAME, arrayList.get(position).getFrom());
        context.startActivity(it);
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

    private boolean isGChat = false;
    private Context context;
    private LayoutInflater mInflater;
    private List<AVIMTypedMessage> arrayList;
    private static final String EXTRA_USERNAME = "username";
}
