package com.team06.freehand.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.team06.freehand.R;

/**
 * Created by isabellepotvin on 2018-03-11.
 */

public class ConfirmPasswordDialog extends Dialog {

    private static final String TAG = "ConfirmPasswordDialog";

    //vars
    TextView tvPassword, tvTitle, tvEmail;
    Context mContext;
    String mTitle, mNewEmail;

    //Interface
    public interface OnConfirmPasswordListener{
        void onConfirmPasswordPassword(String password);
    }
    ConfirmPasswordDialog.OnConfirmPasswordListener mOnConfirmPasswordListener;

    //constructor
    public ConfirmPasswordDialog(@NonNull Context context, OnConfirmPasswordListener mOnConfirmPasswordListener, String title, String newEmail) {
        super(context);
        mContext = context;
        this.mOnConfirmPasswordListener = mOnConfirmPasswordListener;
        mTitle = title;
        mNewEmail = newEmail;
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_password);

        tvPassword = (TextView) findViewById(R.id.confirm_password);
        tvTitle = (TextView) findViewById(R.id.dialogChangeTitle);
        tvEmail = (TextView) findViewById(R.id.dialogChangeValue);

        tvTitle.setText(mTitle);
        tvEmail.setText(mNewEmail);


        Log.d(TAG, "onCreateView: started.");

        //CONFIRM BUTTON
        TextView confirmDialog = (TextView) findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: captured password and confirming.");

                String password = tvPassword.getText().toString();
                if(!password.equals("")){ //checks if they enter a password
                    mOnConfirmPasswordListener.onConfirmPasswordPassword(password);
                    dismiss();
                }else{  //if they don't enter a password
                    Toast.makeText(mContext, "You must enter a password", Toast.LENGTH_SHORT).show();
                }
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

}
