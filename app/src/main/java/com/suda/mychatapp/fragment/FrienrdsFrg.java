package com.suda.mychatapp.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.suda.mychatapp.R;
import com.suda.mychatapp.activity.SearchNewFriendActivity;
import com.suda.mychatapp.adapter.FriendsAdpter;
import com.suda.mychatapp.db.pojo.Friends;

import java.util.ArrayList;


public class FrienrdsFrg extends Fragment {

	public FrienrdsFrg() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFriendslist = new ArrayList<Friends>();
		Bitmap bm = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
		mFriendslist.add(new Friends("ghbhaha","你好","ghbhaha",bm));
		mFriendslist.add(new Friends("ghbhaha2","你好","ghbhaha2",bm));
		mFriendslist.add(new Friends("ghbhaha3","你好","ghbhaha3",bm));

	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.friend, container, false);
		mLvfriends = (ListView)view.findViewById(R.id.lv_friends);
		FriendsAdpter friendsAdpter = new FriendsAdpter(getActivity(),mFriendslist);
		mLvfriends.setAdapter(friendsAdpter);
		return view;

	}



	private ListView mLvfriends;

	private ArrayList<Friends> mFriendslist;

}
