package com.up.study;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.up.common.conf.Constants;
import com.up.common.utils.SPUtil;
import com.up.study.adapter.FragmentAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.ui.ErrorsFragment;
import com.up.study.ui.HomeFragment;
import com.up.study.ui.MyFragment;
import com.up.study.ui.login.LoginActivity;
import com.up.study.weight.MyViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseFragmentActivity {
    private MyViewPager viewPager;
    private ErrorsFragment fra01;
    private HomeFragment fra02;
    private MyFragment fra03;
    private long exitTime = 0;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //setBottomSelected(0);
                    viewPager.setCurrentItem(0,false);
                    return true;
                case R.id.navigation_dashboard:
//                    setBottomSelected(1);
                    viewPager.setCurrentItem(1,false);
                    return true;
                case R.id.navigation_notifications:
//                    setBottomSelected(2);
                    viewPager.setCurrentItem(2,false);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int index = intent.getIntExtra(Constants.KEY,-1);
        showLog("===============onNewIntent================index="+index);
        if (index==0) {
            viewPager.setCurrentItem(0);
            navigation.setSelectedItemId(R.id.navigation_home);
        }
        else if(index==1){
            viewPager.setCurrentItem(1);
            navigation.setSelectedItemId(R.id.navigation_dashboard);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        String userId = SPUtil.getString(this,"userId","");
        if (TextUtils.isEmpty(userId)){
            gotoActivity(LoginActivity.class,null);
            finish();
        }
        viewPager = bind(R.id.viewpager);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
    }

    @Override
    protected void initEvent() {
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void initData() {
        fra01 = new ErrorsFragment();
        fra02 = new HomeFragment();
        fra03 = new MyFragment();
        fragmentList.add(fra01);
        fragmentList.add(fra02);
        fragmentList.add(fra03);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setCurrentItem(1);
        viewPager.setNoScroll(true);
        viewPager.setOffscreenPageLimit(3);

        //拷贝语言包从asset到sd卡
       /* String path = "/mnt/sdcard/tesseract/tessdata/chi_sim.traineddata";
        File file = new File(path);
        //判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            Logger.i(Logger.TAG, "OCR语言包不存在，正在拷贝===================================");
           // Toast.makeText(this, "OCR语言包不存在，正在拷贝", Toast.LENGTH_SHORT).show();
            new copeFileAsync().execute(path);
        } else {
            Logger.i(Logger.TAG, "OCR语言包已存在===================================");
            //Toast.makeText(this, "OCR语言包已存在", Toast.LENGTH_SHORT).show();
        }*/

    }

    @Override
    public void onClick(View v) {}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {

            if((System.currentTimeMillis()-exitTime) > 2000)
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else
            {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 异步任务，拷贝文件
     *
     * @author duanbokan
     */
    /*public class copeFileAsync extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = params[0];
            Logger.i(Logger.TAG,"OCR中文包地址："+result);
            try {
                OcrUtils.copyBigDataToSD(MainActivity.this, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }*/


}
