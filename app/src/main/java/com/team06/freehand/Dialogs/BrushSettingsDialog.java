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
    private float opacity = 15;

    //INTERFACE
    public interface brushSettingsListener {
        void updatedBrushSettings(float brushSize, int opacity);
    }
    brushSettingsListener mBrushSettingsListener;

    //CONSTRUCTOR
    public BrushSettingsDialog(@NonNull Context context, float lastBrushSize, brushSettingsListener mBrushSettingsListener, int opacity) {
        super(context);
        this.lastBrushSize = lastBrushSize;
        this.mBrushSettingsListener = mBrushSettingsListener;
        this.opacity = opacity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_brush_settings);

        Log.d(TAG, "onCreateView: started.");

        //Sets listener for size bubble seek bar

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

        //Sets listener for opacity bubble seek bar

        BubbleSeekBar brushOpacity = (BubbleSeekBar) findViewById(R.id.brush_opacity);
        Log.d(TAG, "onCreate: opacity: " + opacity);
        brushOpacity.setProgress(opacity);
        brushOpacity.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
            }
            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                opacity = progress;

                Log.d(TAG, "getProgressOnActionUp: opacity: " + opacity);
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

                mBrushSettingsListener.updatedBrushSettings(newBrushSize, (int)opacity);

                Log.d(TAG, "onClick: opacity: " + opacity);

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
