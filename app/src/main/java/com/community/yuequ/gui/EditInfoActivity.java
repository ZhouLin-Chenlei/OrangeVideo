package com.community.yuequ.gui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.R;

public class EditInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar mToolbar;
    private TextView mTitleView;
    private Button btn_ok;

    private TextView la_nickname;
    private EditText et_nickname;


    private TextView la_desc;
    private EditText et_desc;

    private int code;
    private String nickname;
    private String desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        btn_ok = (Button) mToolbar.findViewById(R.id.btn_ok);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mTitleView.setText(R.string.edit_profile);
        btn_ok.setOnClickListener(this);

        la_nickname = (TextView) findViewById(R.id.la_nickname);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        la_desc = (TextView) findViewById(R.id.la_desc);
        et_desc = (EditText) findViewById(R.id.et_desc);


        Intent intent = getIntent();
        if(intent!=null){
            code =  intent.getIntExtra("code",0);
            nickname = intent.getStringExtra("nickname");
            desc = intent.getStringExtra("desc");
        }


        if(code==MyprofileActivity.requestCode_nickname){
            la_nickname.setVisibility(View.VISIBLE);
            et_nickname.setVisibility(View.VISIBLE);
            la_desc.setVisibility(View.GONE);
            et_desc.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(nickname)){
                et_nickname.setText(nickname);

            }

        }else if(code==MyprofileActivity.requestCode_desc){
            la_nickname.setVisibility(View.GONE);
            et_nickname.setVisibility(View.GONE);
            la_desc.setVisibility(View.VISIBLE);
            et_desc.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(desc)){
                et_desc.setText(desc);
            }
        }else{

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_ok:
                Intent intent = new Intent();
                if(code==MyprofileActivity.requestCode_nickname){
                    String nick = et_nickname.getText().toString();
                    if(!TextUtils.isEmpty(nick)&&nick.length() > 20){
                        Toast.makeText(this, R.string.prompt_nickname, Toast.LENGTH_SHORT).show();
                    }else{
                        intent.putExtra("nickname",nick);
                    }

                }else if(code==MyprofileActivity.requestCode_desc){
                    String desc = et_desc.getText().toString();
                    if(!TextUtils.isEmpty(desc) && desc.length() > 140){
                        Toast.makeText(this, R.string.prompt_desc, Toast.LENGTH_SHORT).show();
                    }else{
                        intent.putExtra("desc",desc);
                    }
                }
                setResult(RESULT_OK,intent);
                finish();

                break;


        }
    }
}
