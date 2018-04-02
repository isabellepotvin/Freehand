package com.team06.freehand.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.team06.freehand.R;
import com.xw.repo.BubbleSeekBar;

/**
 * Created by isabellepotvin on 2018-03-11.
 */

public class BrushSettingsDialog extends Dialog {

    private static final String TAG = "BrushSettingsDialog";

    //vars
    private float lastBrushSize = 1;
    private float newBrushSize = 20;

    //INTERFACE
    public interface brushSettingsListener {
        void updatedBrushSettings(float brushSize);
    }
    brushSettingsListener mBrushSettingsListener;

    //CONSTRUCTOR
    public BrushSettingsDialog(@NonNull Context context, float lastBrushSize, brushSettingsListener mBrushSettingsListener) {
        super(context);
        this.lastBrushSize = lastBrushSize;
        this.mBrushSettingsListener = mBrushSettingsListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_brush_settings);

        Log.d(TAG, "onCreateView: started.");

        //Sets listener for bubble seek bar

        BubbleSeekBar brushSize = (BubbleSeekBar) findViewById(R.id.brush_size);
        brushSize.setProgress(lastBrushSize);
        newBrushSize = lastBrushSize;
        brushSize.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
            }
            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                newBrushSize = progressFloat;
            }
            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
            }
        });

        //CONFIRM BUTTON
        TextView confirmDialog = (TextView) findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: saving brush settings.");
                Log.d(TAG, "onClick: newBrushSize: " + mBrushSettingsListener);

                mBrushSettingsListener.updatedBrushSettings(newBrushSize);
                dismiss();
            }
        });

        //CANCEL BUTTON (closes the dialog)
        TextView cancelDialog = (TextView) findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing the dialog");
                dismiss();
            }
        });


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

}
