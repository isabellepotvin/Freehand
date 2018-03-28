package com.team06.freehand.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.team06.freehand.Chat.UserChatInfo;
import com.team06.freehand.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class PersonListAdapter extends ArrayAdapter<UserChatInfo> {

    private static final String TAG = "PersonListAdapter";

    private Context mContext;
    int mResource;

    static class ViewHolder {
        TextView name;
        CircleImageView photo;
    }

    /**
     * Default constructor
     * @param context
     * @param resource
     * @param objects
     */
    public PersonListAdapter(@NonNull Context context, int resource, ArrayList<UserChatInfo> objects) {
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

        //create object with the info
        UserChatInfo userChatInfo= new UserChatInfo(name, photo);

        //Viewholder object
        ViewHolder holder = new ViewHolder();

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.person_name);
            holder.photo = (CircleImageView) convertView.findViewById(R.id.person_picture);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        ImageLoader imageLoader = ImageLoader.getInstance();
        UniversalImageLoader.setImage(photo, holder.photo, null, "");

        holder.name.setText(name);

        return convertView;

    }




}
