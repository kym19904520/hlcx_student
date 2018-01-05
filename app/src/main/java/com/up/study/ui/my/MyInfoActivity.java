package com.up.study.ui.my;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.SPUtil;
import com.up.common.utils.StudyUtils;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.callback.CallBack;
import com.up.study.model.ImgUrl;
import com.up.study.weight.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.Call;
import okhttp3.Response;

import static com.lzy.okgo.OkGo.getContext;

public class MyInfoActivity extends BaseFragmentActivity {
    private ImageView iv_edit_info, iv_head;
    private TextView tv_phone, tv_stu_name, tv_stu_num, tv_stu_class;

    private List<String> imageList = new ArrayList<>();
    private String imgPath,user_head;

    @Override
    protected int getContentViewId() {
        return R.layout.act_my_info;
    }

    @Override
    protected void initView() {
        iv_edit_info = bind(R.id.iv_edit_info);
        tv_phone = bind(R.id.tv_phone);
        tv_stu_name = bind(R.id.tv_stu_name);
        tv_stu_num = bind(R.id.tv_stu_num);
        tv_stu_class = bind(R.id.tv_stu_class);
        iv_head = bind(R.id.iv_head);
    }

    @Override
    protected void initEvent() {
        iv_edit_info.setOnClickListener(this);
        iv_head.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        tv_phone.setText(TApplication.getInstant().getPhone());
        tv_stu_name.setText(TApplication.getInstant().getStudentName());
        tv_stu_num.setText(TApplication.getInstant().getStudentNum());
        tv_stu_class.setText(TApplication.getInstant().getClassName());
        user_head = SPUtil.getString(getContext(),"head","");
        J.image().loadCircle01(ctx,user_head,iv_head);
    }

    @Override
    public void onClick(View v) {
        if (v == iv_edit_info) {
            Intent intent = new Intent();
            intent.setClass(this, EditMyInfoActivity.class);
            startActivityForResult(intent, 0);
        } else if (v == iv_head) {
            selectHead();
        }
    }

    /**
     * 选择头像
     */
    private void selectHead() {
        GalleryFinal.openGallerySingle(0, new GalleryFinal.OnHanlderResultCallback() {
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                imgPath = resultList.get(0).getPhotoPath();
                imageList.add(imgPath);
                StudyUtils.uploadImgUrl(imageList, ctx, new CallBack<List<ImgUrl>>() {
                    @Override
                    public void suc(List<ImgUrl> obj) {
                        SPUtil.putString(getContext(),"head",imgPath);
                        submitPicture(obj.get(0).getUrl());
                    }
                });
                Glide.with(MyInfoActivity.this).load(imgPath).
                        bitmapTransform(new GlideCircleTransform(MyInfoActivity.this)).crossFade(1000).into(iv_head);
            }

            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {

            }
        });
    }

    /**
     * 提交头像
     * @param obj
     */
    private void submitPicture(String obj) {
        HttpParams params = new HttpParams();
        params.put("head", obj);
        showLog("头像" + obj);
        J.http().post(Urls.MY_PICTURE, ctx, params, new HttpCallback<Respond>(null) {
            @Override
            public void success(Respond res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showLog("重置学生值----");
        tv_stu_name.setText(TApplication.getInstant().getStudentName());
        tv_stu_num.setText(TApplication.getInstant().getStudentNum());
        tv_stu_class.setText(TApplication.getInstant().getClassName());
    }
}
