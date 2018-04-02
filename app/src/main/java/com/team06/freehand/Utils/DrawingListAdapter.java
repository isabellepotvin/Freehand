package com.team06.freehand.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.team06.freehand.Chat.InfoChat;
import com.team06.freehand.Models.ChatMessage;
import com.team06.freehand.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class DrawingListAdapter extends ArrayAdapter<ChatMessage> {

    private static final String TAG = "DrawingListAdapter";

    private int drawingMarging = 100;

    private Context mContext;
    int mResource;
    String currentUserID;


    static class ViewHolder {
        ImageView drawing;
        TextView timestamp;
    }

    /**
     * Default constructor
     * @param context
     * @param resource
     * @param objects
     */
    public DrawingListAdapter(@NonNull Context context, int resource, ArrayList<ChatMessage> objects, String currentUserID) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.currentUserID = currentUserID;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //get information
        String image_path = getItem(position).getImage_path();
        String timestamp = getItem(position).getTimestamp();
        String sender_user_id = getItem(position).getSender_user_id();
        String receiver_user_id = getItem(position).getReceiver_user_id();

        //create object with the info
        ChatMessage chatMessageInfo = new ChatMessage(image_path, sender_user_id, receiver_user_id, timestamp);

        //Viewholder object
        ViewHolder holder = new ViewHolder();

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder.drawing = (ImageView) convertView.findViewById(R.id.drawing);
            holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //sets image
        ImageLoader imageLoader = ImageLoader.getInstance();
        UniversalImageLoader.setImage(image_path, holder.drawing, null, "");

        //creates simple date formats
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        SimpleDateFormat newSdf = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.CANADA);
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



        //aligns drawing to the left or right

        RelativeLayout.LayoutParams paramsDrawing = (RelativeLayout.LayoutParams) holder.drawing.getLayoutParams();

        if(sender_user_id.equals(currentUserID)){ //align right
            paramsDrawing.setMargins(drawingMarging, 0, 0, 0);
            holder.timestamp.setGravity(Gravity.END); //aligns the text to the right
        }
        else{ //align left
            paramsDrawing.setMargins(0, 0, drawingMarging, 0);
            holder.timestamp.setGravity(Gravity.START); //aligns the text to the left

        }

        holder.drawing.setLayoutParams(paramsDrawing);



        return convertView;

    }




}
