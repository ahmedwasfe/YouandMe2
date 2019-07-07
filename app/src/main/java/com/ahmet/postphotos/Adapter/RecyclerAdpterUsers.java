package com.ahmet.postphotos.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ahmet on 2/5/2018.
 */

public class RecyclerAdpterUsers extends RecyclerView.ViewHolder {

    Context context;
    View convertView;

    public RecyclerAdpterUsers(Context context, View itemView) {
        super(itemView);

        this.convertView = itemView;
        this.context = context;
    }

    public void setUsername(String name){



    }
}
