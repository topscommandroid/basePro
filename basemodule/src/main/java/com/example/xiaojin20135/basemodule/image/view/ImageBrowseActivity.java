package com.example.xiaojin20135.basemodule.image.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.example.xiaojin20135.basemodule.R;
import com.example.xiaojin20135.basemodule.activity.BaseActivity;
import com.example.xiaojin20135.basemodule.activity.ToolBarActivity;
import com.example.xiaojin20135.basemodule.dialog.MyItemDialog;
import com.example.xiaojin20135.basemodule.image.adapter.ImageBrowseAdapter;
import com.example.xiaojin20135.basemodule.image.listener.ImageLongClick;

import java.util.ArrayList;

import static com.example.xiaojin20135.basemodule.image.ImageConstant.FROMNET;

public class ImageBrowseActivity extends BaseActivity implements ImageLongClick {
    private TextView mNumberTextView;
    private ArrayList<String> imageList = new ArrayList<> ();
    private ViewPager imageBrowseViewPager;
    private ImageBrowseAdapter imageBrowseAdapter;
    private int currentIndex = 0;
    //是否是网络图片
    private boolean fromNet = false;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        //出现动画
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //展示数据
        showData();
    }

    @Override
    protected int getLayoutId () {
        return R.layout.activity_image_browse;
    }

    @Override
    protected void initView () {
        mNumberTextView = (TextView)findViewById(R.id.number_textview);
        imageBrowseViewPager = (ViewPager)findViewById (R.id.imageBrowseViewPager);
        imageBrowseAdapter = new ImageBrowseAdapter (this,imageList,this);
        imageBrowseViewPager.setAdapter (imageBrowseAdapter);
        imageBrowseViewPager.setCurrentItem (currentIndex);
    }

    @Override
    protected void initEvents () {
        imageBrowseViewPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener () {
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
                currentIndex = position;
                Log.d (TAG,"currentIndex = " + currentIndex);
            }
            @Override
            public void onPageSelected (int position) {
                updateBottomIndex(position + 1);
            }
            @Override
            public void onPageScrollStateChanged (int state) {

            }
        });
    }

    @Override
    protected void loadData () {
        Intent intent = getIntent ();
        if(intent != null){
            currentIndex = intent.getIntExtra("index", 0);
            fromNet = intent.getBooleanExtra(FROMNET,false);
            imageList = (ArrayList<String>) intent.getStringArrayListExtra("imageList");
        }
    }

    /*
    * @author lixiaojin
    * create on 2019/8/1 16:17
    * description: 展示数据
    */
    private void showData(){
        if(imageList.size() > 0) {
            updateBottomIndex(currentIndex + 1);
        }
    }

    /*
    * @author lixiaojin
    * create on 2019/8/1 16:13
    * description: 底部数字索引
    */
    private void updateBottomIndex(int count){
        mNumberTextView.setText(count + "/" + imageList.size());
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.deletemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if(item.getItemId () == R.id.delete_item){
            if(imageList != null && imageList.size () > currentIndex){
                imageList.remove (currentIndex);
                imageBrowseAdapter.notifyDataSetChanged ();
            }
            if(imageList == null || imageList.size () == 0){
                back();
            }
        }
        return super.onOptionsItemSelected (item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK == keyCode){
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void back(){
        Intent intent = new Intent ();
        intent.putStringArrayListExtra ("imageList",imageList);
        setResult (RESULT_OK,intent);
        this.finish ();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_disappear,R.anim.fade_out_disappear);
    }

    @Override
    public void longClickImage(int position) {
        //如果是网络图片
        if(fromNet){
            String[] items = new String[]{"下载"};
            MyItemDialog myItemDialog = new MyItemDialog(this, getString(R.string.app_name), items, 1, new MyItemDialog.OnDialogItemClickListener() {
                @Override
                public void onDialogItemClick(int requestCode, int position, String item) {

                }
            });

        }else{//如果是本地图片预览

        }
    }
}
