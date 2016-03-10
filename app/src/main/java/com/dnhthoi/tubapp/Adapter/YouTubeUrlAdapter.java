package com.dnhthoi.tubapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dnhthoi.tubapp.LoginActivity;
import com.dnhthoi.tubapp.R;
import com.dnhthoi.tubapp.data.YouTubeData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ant on 02/03/2016.
 */
public class YouTubeUrlAdapter extends RecyclerView.Adapter<YouTubeUrlViewHolder> implements YouTubeUrlViewHolder.OnItemClick{

    private Context context;
    private Realm mRealm;
    private RealmResults<YouTubeData> listData;

    public YouTubeUrlAdapter(Context context) {
        this.context = context;
        mRealm  = Realm.getInstance(context);
        mRealm.beginTransaction();
        listData = mRealm.where(YouTubeData.class).findAll();
        mRealm.commitTransaction();
    }

    @Override
    public YouTubeUrlViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new YouTubeUrlViewHolder(view,context, this) ;
    }
    public void reloadData(){
        mRealm.beginTransaction();
        listData = mRealm.where(YouTubeData.class).findAll();
        mRealm.commitTransaction();
    }
    @Override
    public void onBindViewHolder(YouTubeUrlViewHolder holder, int position) {
        final YouTubeData data = listData.get(position);
        final int index = position;
        Log.e("thumbnails",data.getThumbnails());
        Log.e("URL ::",data.getUrl());
        holder.tvTitle.setText(data.getTitle());
        Picasso.with(context)
                .load(data.getThumbnails())
                .into(holder.imgThumbal);
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(createShareForecastIntent(data.getUrl()));
            }
        });

        holder.btnDelate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRealm.beginTransaction();
                listData.remove(index);
                mRealm.commitTransaction();
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, listData.size());
            }
        });
    }

    private final Intent createShareForecastIntent(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        return shareIntent;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public void click(View view) {
        int itemPosition = ((LoginActivity)context).getmListUrl().getChildAdapterPosition(view);
        Intent videoIntent = new Intent(Intent.ACTION_VIEW);
        videoIntent.setData(Uri.parse(listData.get(itemPosition).getUrl()));
        videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(videoIntent);
    }
}
