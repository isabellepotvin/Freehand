package com.team06.freehand.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.team06.freehand.R;

import java.util.ArrayList;

/**
 * Created by isabellepotvin on 2018-02-25.
 */

public class GridImageAdapter extends ArrayAdapter<String>{

    private static final String TAG = "GridImageAdapter";  //I added this for testing purposes

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imgURLs;
    private String imgType;

    //constructor
    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs, String imgType) {
        super(context, layoutResource, imgURLs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Log.d(TAG, "GridImageAdapter: Inflating Layout.");  //I added this for testing purposes

        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;
        this.imgType = imgType;
    }


    private static class ViewHolder<SquareImageView>{
        SquareImageView image;
        ProgressBar mProgressBar;
    }

    //(does not load all the widgets at once --> faster)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder<SquareImageView> holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressBar);

//            if(imgType.equals(R.string.img_imageView)) { //if regular image view
//                holder.image = (ImageView) convertView.findViewById(R.id.grid_imageView);
//            }
//            else{ //if square image view
                holder.image = (SquareImageView) convertView.findViewById(R.id.grid_squareImageView);
//            }

            convertView.setTag(holder); //stores the entire ViewHolder
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        String imgURL = getItem(position);

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(mAppend + imgURL, holder.image, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(holder.mProgressBar != null){
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                    //Log.d(TAG, "onLoadingStarted: Loading Started.");  //I added this for testing purposes
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(holder.mProgressBar != null){
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(holder.mProgressBar != null){
                    holder.mProgressBar.setVisibility(View.GONE);
                    //Log.d(TAG, "onLoadingComplete: Loading Complete.");  //I added this for testing purposes
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(holder.mProgressBar != null){
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }
}
