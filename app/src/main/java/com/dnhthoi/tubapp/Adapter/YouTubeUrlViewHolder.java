package com.dnhthoi.tubapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dnhthoi.tubapp.R;

/**
 * Created by ant on 02/03/2016.
 */
public class YouTubeUrlViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    public TextView tvTitle;
    public Button btnShare;
    public Button btnDelate;
    public ImageView imgThumbal;
    private OnItemClick listoner;
    public YouTubeUrlViewHolder(View itemView, Context context, OnItemClick listoner) {
        super(itemView);
        this.context = context;
        tvTitle = (TextView)itemView.findViewById(R.id.tv_url);
        btnDelate = (Button)itemView.findViewById(R.id.btn_item_delete);
        btnShare = (Button)itemView.findViewById(R.id.btn_item_share);
        imgThumbal = (ImageView)itemView.findViewById(R.id.img_thumbal);
        this.listoner = listoner;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listoner.click(v);
    }

    interface OnItemClick{
        public void click(View view);
    }
}
