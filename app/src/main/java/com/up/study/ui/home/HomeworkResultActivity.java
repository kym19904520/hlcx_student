package com.up.study.ui.home;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.model.HttpParams;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.ImageUtil;
import com.up.common.utils.Logger;
import com.up.common.utils.SPUtil;
import com.up.common.utils.StudyUtils;
import com.up.common.widget.MyListView;
import com.up.study.MainActivity;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.callback.CallBack;
import com.up.study.model.HomeworkAnalysisModel;
import com.up.study.model.ImgUrl;
import com.up.study.model.Know;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作业结果
 */
public class HomeworkResultActivity extends BaseFragmentActivity {
    MyListView lv_know;
    CommonAdapter<Know> adapter_know;
    private List<Know> knowList = new ArrayList<Know>();

    private  TextView tv_title,tv_date,tv_user_time,tv_right_rate,tv_result_right_num,tv_result_class_right;
    private  TextView tv_goto_error,tv_goto_home;
    private  ImageView iv_fx;

    private ScrollView sv_view;
    private IWXAPI api;
    @Override
    protected int getContentViewId() {
        return R.layout.act_homework_result;
    }

    @Override
    protected void initView() {
        lv_know = bind(R.id.lv);
        tv_title = bind(R.id.tv_title);
        tv_date = bind(R.id.tv_date);
        tv_user_time = bind(R.id.tv_user_time);
        tv_right_rate = bind(R.id.tv_right_rate);
        tv_result_right_num = bind(R.id.tv_result_right_num);
        tv_result_class_right = bind(R.id.tv_result_class_right);
        tv_goto_error = bind(R.id.tv_goto_error);
        tv_goto_home = bind(R.id.tv_goto_home);
        iv_fx = bind(R.id.iv_fx);
        sv_view = bind(R.id.sv_view);

        api = WXAPIFactory.createWXAPI(ctx, TApplication.getInstant().getWxAppId(), false);
        api.registerApp(TApplication.getInstant().getWxAppId());
    }

    @Override
    protected void initEvent() {
        tv_goto_error.setOnClickListener(this);
        tv_goto_home.setOnClickListener(this);
        iv_fx.setOnClickListener(this);
        registerForContextMenu(iv_fx);//btn是要点击的控件
    }

    @Override
    protected void initData() {

        adapter_know = new CommonAdapter<Know>(ctx, knowList, R.layout.item_homework_result) {
            @Override
            public void convert(ViewHolder vh, Know item, int position) {
                TextView tv_result_title = vh.getView(R.id.tv_result_title);
                TextView tv_result_right_num = vh.getView(R.id.tv_result_right_num);
                TextView tv_result_all_right = vh.getView(R.id.tv_result_all_right);
                TextView tv_result_class_right = vh.getView(R.id.tv_result_class_right);
                TextView tv_result_my_right = vh.getView(R.id.tv_result_my_right);
                tv_result_title.setText(item.getKnowName());
                tv_result_right_num.setText(item.getMyKnowCorrect());
                tv_result_class_right.setText((item.getClassCorrectRate()==null?0:item.getClassCorrectRate())+"%");
                tv_result_my_right.setText((item.getMyCorrectRate()==null?0:item.getMyCorrectRate())+"%");
                tv_result_all_right.setText((item.getAllCorrectRate()==null?0:item.getAllCorrectRate())+"%");
            }
        };
        lv_know.setAdapter(adapter_know);
        getResult();
    }

    @Override
    public void onClick(View v) {
        if (v==tv_goto_error){
            showLog("回到错题库");
            gotoActivity(MainActivity.class,0);
        }
        else if(v==tv_goto_home){
            showLog("回到首页");
            gotoActivity(MainActivity.class,1);
        }
        else if(v==iv_fx){//分享
            v.showContextMenu();//单击直接显示Context菜单
        }
    }
    private void beginShare(final int type){
        if (!api.isWXAppInstalled()) {
            Toast.makeText(HomeworkResultActivity.this, "您还未安装微信",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final Bitmap bitmap = shotScrollView(sv_view);
        //直接分享图片
        /*if(bitmap!=null){
            wxShare(type,bitmap);
        }
        else{
            showToast("图片未截屏成功");
        }*/
        //分享图片链接
        String imgPath = ImageUtil.saveImg(bitmap);
        if (!TextUtils.isEmpty(imgPath)){
            List<String> imgList = new ArrayList<>();
            imgList.add(imgPath);
            showLoading("分享图片...");
            StudyUtils.uploadImgUrl(imgList, this, new CallBack<List<ImgUrl>>() {
                @Override
                public void suc(List<ImgUrl> obj) {
                    HomeworkResultActivity.this.hideLoading();
                    if (obj!=null&&obj.size()>0){
                        String host = SPUtil.getString(ctx, Constants.SP_IMG_URL,"");
                        Logger.i(Logger.TAG,"分享图片地址添加域名头:"+host);
                        String load = "http://" + host + obj.get(0).getUrl();
                        showLog("分享地址："+load);
//                        wxShare(type,load,bitmap);
                        wxShare(type,bitmap);
                    }
                }
            });
        }
        else{
            showToast("图片未截屏成功");
        }
    }

    /**
     * 分享图片链接
     * @param type
     * @param url
     * @param bitmap
     */
    private void wxShare(int type,String url,Bitmap bitmap){
        try {
            WXWebpageObject wxWebpageObject = new WXWebpageObject();
            wxWebpageObject.webpageUrl = url;
            WXMediaMessage wxMediaMessage = new WXMediaMessage(wxWebpageObject);
            wxMediaMessage.title = "轻松学-作业结果";
            wxMediaMessage.description = tv_title.getText().toString();
//        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
//            wxMediaMessage.thumbData = baos.toByteArray();//不能大于32kb
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "webpage";
            req.message = wxMediaMessage;
            if (type == 1) {//发送到聊天界面
                req.scene = SendMessageToWX.Req.WXSceneSession;
            } else {//朋友圈
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
            }
            api.sendReq(req);
        }
        catch (Exception e){
            e.printStackTrace();
            showToast("分享失败");
        }

    }

    /**
     * 直接分享图片
     * @param type
     * @param bitmap
     */
    private void wxShare(int type,Bitmap bitmap){
        try {
            WXImageObject imageObject = new WXImageObject(bitmap);
            WXMediaMessage message = new WXMediaMessage();
            message.mediaObject = imageObject;
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "webpage";
            req.message = message;
            if (type == 1) {//发送到聊天界面
                req.scene = SendMessageToWX.Req.WXSceneSession;
            } else {//朋友圈
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
            }
            api.sendReq(req);
        }
        catch (Exception e){
            e.printStackTrace();
            showToast("分享失败");
        }

    }

    /**
     * scrollView截屏
     * @param scrollView
     * @return
     */
    public Bitmap shotScrollView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
        }
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }
    /**
     * 获取提交作业结果
     */
    private void getResult() {
        knowList.clear();
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        J.http().post(Urls.ONLINE_ANALYSIS, ctx, params, new HttpCallback<Respond<HomeworkAnalysisModel>>(ctx) {
            @Override
            public void success(Respond<HomeworkAnalysisModel> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    //showToast(res.getData().getKnow().get(0).getKnowName());
                    HomeworkAnalysisModel analysisModel= res.getData();
                    List<Know> knows = analysisModel.getKnow();
                    knowList.addAll(knows);
                    adapter_know.notifyDataSetChanged();


                    tv_title.setText(analysisModel.getTitle());
                    tv_date.setText("("+analysisModel.getDeadline()+")");
                    tv_user_time.setText(analysisModel.getTimeConsuming());
//                    tv_user_time.setText("23:34:12");
                    tv_right_rate.setText(analysisModel.getMyAllCorrectRate()+"%");
                    tv_result_right_num.setText(analysisModel.getMyCorrectSum());
                    tv_result_class_right.setText(analysisModel.getCorrectRate()+"%");

                }
                else{
                    showToast(res.getMsg());
                    HomeworkResultActivity.this.finish();
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.share,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //布局文件里面对应的id，当点击时，根据id区别那个被点击
        switch (item.getItemId()){
            case R.id.text1:
                showLog("=========分享给朋友=================");
                beginShare(1);
                break;
            case R.id.text2:
                showLog("=========分享给朋友圈=================");
                beginShare(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
