package com.suda.mychatapp.utils;

/**
 * Created by Suda on 2015/7/24.
 */

import android.util.Log;

import com.suda.mychatapp.db.pojo.Friends;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FriendSortUtil implements Comparator<Object> {

    public static void sortFriend(ArrayList<Friends> arrayList){
        String[] names = new String[arrayList.size()];
        Map<String,Friends> map = new HashMap<>();
        int i=0;
        String s ="";

        for (Friends friend:arrayList){
            names[i]=friend.getNikeName();
            map.put(friend.getNikeName(),friend);
            s += names[i];
            i++;
        }
        s="";
        Arrays.sort(names, 0, arrayList.size(), new FriendSortUtil());
        arrayList.clear();
        for (String name :names){
            s +=name;
            arrayList.add(map.get(name));
        }
    }


    @Override
    public int compare(Object arg0, Object arg1) {
        String one = (String) arg0;
        String two = (String) arg1;
        Collator ca = Collator.getInstance(Locale.CHINA);
        int flags = 0;
        if (ca.compare(one, two) < 0) {
            flags = -1;
        } else if (ca.compare(one, two) > 0) {
            flags = 1;
        } else {
            flags = 0;
        }
        return flags;
    }
}
