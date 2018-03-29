package com.team06.freehand.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.team06.freehand.R;
import com.xw.repo.BubbleSeekBar;

/**
 * Created by isabellepotvin on 2018-03-11.
 */

public class BrushSettingsDialog extends DialogFragment {

    private static final String TAG = "BrushSettingsDialog";

    //vars
    private float lastBrushSize = 1;
    private float newBrushSize = 20;

    //INTERFACE
    public interface brushSettingsListener{
        void updatedBrushSettings(float brushSize);
    }
    brushSettingsListener mBrushSettingsListener;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_brush_chooser, container, false);

        Log.d(TAG, "onCreateView: started.");

        //Sets listener for bubble seek bar

        BubbleSeekBar brushSize = (BubbleSeekBar) view.findViewById(R.id.brush_size);
        brushSize.setProgress(lastBrushSize);
        brushSize.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                newBrushSize = progressFloat;
            }
            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) { }
            @Override
            public void getProgressOnFinally(int progress, float progressFloat) { }
        });

        //CONFIRM BUTTON
        TextView confirmDialog = (TextView) view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: saving brush settings.");
                Log.d(TAG, "onClick: newBrushSize: " + mBrushSettingsListener);

                mBrushSettingsListener.updatedBrushSettings(newBrushSize);
                getDialog().dismiss();
            }
        });

        //CANCEL BUTTON (closes the dialog)
        TextView cancelDialog = (TextView) view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing the dialog");
                getDialog().dismiss();
            }
        });

        return view;
    }


    /*
     * I added this override method to remove the title area on the dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


//    //allows us to pass variables from this interface directly to the target fragment
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        try{
//            mBrushSettingsListener = (brushSettingsListener) getTargetFragment();
//        }catch (ClassCastException e){
//            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
//        }
//    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mBrushSettingsListener = (brushSettingsListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

}
