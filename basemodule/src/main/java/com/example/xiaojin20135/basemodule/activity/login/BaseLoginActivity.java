package com.example.xiaojin20135.basemodule.activity.login;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.xiaojin20135.basemodule.R;
import com.example.xiaojin20135.basemodule.activity.BaseActivity;
import com.example.xiaojin20135.basemodule.retrofit.bean.CboUserEntity;
import com.example.xiaojin20135.basemodule.retrofit.bean.ResponseBean;
import com.example.xiaojin20135.basemodule.retrofit.bean.UserBean;
import com.example.xiaojin20135.basemodule.retrofit.presenter.PresenterImpl;
import com.example.xiaojin20135.basemodule.util.ConstantUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

/**
 * @author lixiaojin
 * @create 2018-07-13
 * @Describe 基础登陆类，需要设置登录地址以及登陆成功后跳转页面
 */
public abstract class BaseLoginActivity extends BaseActivity {
    private AutoCompleteTextView loginName_TV;
    private EditText password_ET;
    private Button sign_in_button;
    private String loginName,password;
    //记住密码
    private CheckBox remember_password_checkBox;
    //是否自动登录
    private boolean autoLogin = false;


    //登录地址，必须设置
    private String loginUrl = "";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

    }

    /**
     * 初始化完成，开始业务逻辑
     */
    public void canStart(){
        requestPermission();
    }

    @Override
    protected int getLayoutId () {
        return R.layout.activity_base_login;
    }

    @Override
    protected void initView () {
        loginName_TV = (AutoCompleteTextView) findViewById(R.id.loginName_TV);
        password_ET = (EditText) findViewById(R.id.password_ET);
        sign_in_button = (Button)findViewById(R.id.sign_in_button);
        remember_password_checkBox = (CheckBox)findViewById(R.id.remember_password_checkBox);
    }

    @Override
    protected void initEvents () {
        //登录
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(beforeLogin()){
                    attemptLogin();
                }
            }
        });
        //自动登录
        remember_password_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("LoginActivity", "isChecked=" + isChecked);
                autoLogin = isChecked;
            }
        });
    }

    @Override
    protected void loadData () {

    }

    public void autoLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("autoLogin",false)){
            loginName = sharedPreferences.getString("loginName","");
            password = sharedPreferences.getString("password","");
            loginName_TV.setText(loginName);
            password_ET.setText(password);
            remember_password_checkBox.setChecked(true);
            attemptLogin();
        }
    }

    public boolean beforeLogin(){
        boolean result = true;
        loginName = loginName_TV.getText().toString();
        password = password_ET.getText().toString();
        if(TextUtils.isEmpty(loginName)){
            loginName_TV.setError(getString(R.string.error_field_required));
            result = false;
        }
        if(TextUtils.isEmpty(password)){
            password_ET.setError(getString(R.string.error_field_required));
            result = false;
        }
        return result;
    }

    private void attemptLogin() {
        Map paraMap = new HashMap ();
        paraMap.put(ConstantUtil.loginName,loginName);
        paraMap.put(ConstantUtil.password,password);
        if(TextUtils.isEmpty (loginUrl)){
            showToast (this,R.string.login_url_null);
            return;
        }
        tryToGetData (loginUrl,paraMap);
    }

    @Override
    public void loadDataSuccess (Object tData) {
        super.loadDataSuccess (tData);
        saveLoginInfo(((ResponseBean)tData).getUserBean ());
        loginSuccess();
    }

    /**
     * 保存个人登录信息
     * @param userBean
     */
    private void saveLoginInfo(UserBean userBean){
        CboUserEntity cboUserEntity = userBean.getUser();
        if(cboUserEntity != null){
            SharedPreferences.Editor editor = getSharedPreferences("loginInfo",MODE_PRIVATE).edit();
            editor.putString(ConstantUtil.loginName,cboUserEntity.getLoginname());
            editor.putString(ConstantUtil.password,password);
            editor.putBoolean("autoLogin",autoLogin);
            editor.putString("code",cboUserEntity.getCode());
            editor.putString(ConstantUtil.mobile,cboUserEntity.getMobile());
            BigDecimal id = new BigDecimal(cboUserEntity.getId());
            editor.putString("id",id.toPlainString());
            editor.putString(ConstantUtil.name,cboUserEntity.getName());
            editor.commit();
        }



    }

    public void setLoginUrl (String loginUrl) {
        this.loginUrl = loginUrl;
    }

    /**
     * 登陆成功跳转
     */
    public abstract void loginSuccess ();

    /**
     * 获取权限
     */
    public void requestPermission(){
        List<PermissionItem> permissonItems = new ArrayList<PermissionItem> ();
        permissonItems.add(new PermissionItem(android.Manifest.permission.CAMERA,getString (R.string.camera),R.drawable.permission_ic_camera));
        permissonItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE,getString (R.string.file),R.drawable.permission_ic_storage));
        permissonItems.add(new PermissionItem(android.Manifest.permission.ACCESS_FINE_LOCATION,getString (R.string.location),R.drawable.permission_ic_location));
        HiPermission.create(this)
            .permissions(permissonItems)
            .title(getString (R.string.permission_needed))
            .checkMutiPermission(new PermissionCallback () {
                @Override
                public void onClose() {
                    Log.d(TAG,"用户关闭权限申请");
                }
                @Override
                public void onFinish() {
                    Log.d(TAG,"所有权限申请完成");
                    autoLogin();
                }
                @Override
                public void onDeny(String permisson, int position) {
                    Log.d(TAG, "onDeny");
                }
                @Override
                public void onGuarantee(String permisson, int position) {
                    Log.d(TAG, "onGuarantee");
                }
            });

    }
}