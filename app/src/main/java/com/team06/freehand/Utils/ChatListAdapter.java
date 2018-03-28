package com.team06.freehand.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.team06.freehand.Chat.UserChatInfo;
import com.team06.freehand.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class ChatListAdapter extends ArrayAdapter<UserChatInfo> {

    private static final String TAG = "ChatListAdapter";

    private Context mContext;
    int mResource;

    static class ViewHolder {
        TextView name;
        CircleImageView photo;
        TextView timestamp;
    }

    /**
     * Default constructor
     * @param context
     * @param resource
     * @param objects
     */
    public ChatListAdapter(@NonNull Context context, int resource, ArrayList<UserChatInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //get information
        String name = getItem(position).getName();
        String photo = getItem(position).getImgUrl();
        String timestamp = getItem(position).getLast_timestamp();

        //create object with the info
        UserChatInfo userChatInfo= new UserChatInfo(name, photo, timestamp);

        //Viewholder object
        ViewHolder holder = new ViewHolder();

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.person_name);
            holder.photo = (CircleImageView) convertView.findViewById(R.id.person_picture);
            holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //sets image
        ImageLoader imageLoader = ImageLoader.getInstance();
        UniversalImageLoader.setImage(photo, holder.photo, null, "");

        //sets name
        holder.name.setText(name);

        //creates simple date formats
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        SimpleDateFormat newSdf = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm a", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Eastern"));
        newSdf.setTimeZone(TimeZone.getTimeZone("Canada/Eastern"));

        //converts timestamp
        try{
            Date date = sdf.parse(timestamp);
            timestamp = newSdf.format(date);
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            timestamp = null;
        }

        //sets timestamp
        holder.timestamp.setText(String.valueOf(timestamp));

        return convertView;

    }




}
