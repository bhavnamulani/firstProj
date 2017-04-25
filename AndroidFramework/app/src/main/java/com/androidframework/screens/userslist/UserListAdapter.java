package com.androidframework.screens.userslist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidframework.R;
import com.androidframework.entities.UserDO;

import java.util.ArrayList;

/**
 * Created by Administrator on 12/28/2016.
 */
public class UserListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<UserDO> userDAOArrayList;

    public UserListAdapter(Context context, ArrayList<UserDO> userDOArrayList) {
        this.context = context;
        this.userDAOArrayList = userDOArrayList;
    }

    @Override
    public int getCount() {
        if (userDAOArrayList.size() != 0) {
            return userDAOArrayList.size();
        }
        return 0;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.users_list_layout, parent, false);
        }

        TextView mUserName = (TextView) convertView.findViewById(R.id.nameTextView);
        TextView lastMsgTextView = (TextView) convertView.findViewById(R.id.lastMsgTextView);
        TextView unReadMsgCount = (TextView) convertView.findViewById(R.id.unReadMsgCount);

        UserDO userDO = userDAOArrayList.get(position);
        mUserName.setText(userDO.getUsername());
        lastMsgTextView.setText(userDO.getLastMessage());

        if (userDO.getUnreadMsgCount() == 0) {
            unReadMsgCount.setText("");
        } else {
            unReadMsgCount.setText(userDO.getUnreadMsgCount() + "");
        }


        return convertView;
    }

    public void refreshAdapter(ArrayList<UserDO> userDAOArrayList) {
        this.userDAOArrayList = userDAOArrayList;
        notifyDataSetChanged();
    }
}
