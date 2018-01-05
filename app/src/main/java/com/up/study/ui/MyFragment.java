package com.up.study.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.BaseBean;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.SPUtil;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragment;
import com.up.study.ui.my.MyInfoActivity;
import com.up.study.ui.my.OpinionActivity;
import com.up.study.ui.my.SettingActivity;
import com.up.study.ui.my.StudyStatusAnalysisActivity;
import com.up.study.ui.my.TeacherMesActivity;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by dell on 2017/4/20.
 */

public class MyFragment extends BaseFragment {

    private ImageView iv_sz, iv_head;
    private RelativeLayout rl_xqfx, rl_wdsj, rl_lsly, rl_qz, rl_yjfk;
    private TextView tv_zql, tv_ct, tv_zy;
    private TextView tv111, tv222;

    private String user_head;

    @Override
    protected int getContentViewId() {
        return R.layout.fra_my;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {//可见
            showLog("---------------------可见");
            getMyDatas();
        } else {

        }
    }

    @Override
    protected void initView() {
        iv_sz = bind(R.id.iv_sz);
        iv_head = bind(R.id.iv_head);
        rl_xqfx = bind(R.id.rl_xqfx);
        rl_wdsj = bind(R.id.rl_wdsj);
        rl_lsly = bind(R.id.rl_lsly);
        rl_qz = bind(R.id.rl_qz);
        rl_yjfk = bind(R.id.rl_yjfk);

        tv111 = bind(R.id.tv111);
        tv222 = bind(R.id.tv222);

        tv_ct = bind(R.id.tv_ct);
        tv_zy = bind(R.id.tv_zy);
        tv_zql = bind(R.id.tv_zql);
        TextView phone = bind(R.id.tv_phone);
        phone.setText(TApplication.getInstant().getPhone());
    }

    @Override
    protected void initEvent() {
        iv_sz.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        rl_xqfx.setOnClickListener(this);
        rl_wdsj.setOnClickListener(this);
        rl_lsly.setOnClickListener(this);
        rl_qz.setOnClickListener(this);
        rl_yjfk.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        user_head = SPUtil.getString(getContext(), "head", "");
        J.image().loadCircle01(ctx, user_head, iv_head);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View v) {
        if (v == iv_sz) {
            gotoActivity(SettingActivity.class, null);
        } else if (v == iv_head) {
            gotoActivity(MyInfoActivity.class, null);
        } else if (v == rl_xqfx) {//学情分析
            gotoActivity(StudyStatusAnalysisActivity.class, null);
        } else if (v == rl_wdsj) {//我的试卷
            //gotoActivity(MyTestActivity.class,null);
            //换成设置了
            gotoActivity(SettingActivity.class, null);
        } else if (v == rl_lsly) {//老师留言
            gotoActivity(TeacherMesActivity.class, null);
        } else if (v == rl_qz) {//求助

        } else if (v == rl_yjfk) {//意见反馈
            gotoActivity(OpinionActivity.class, null);
        }
    }

    private void getMyDatas() {
        HttpParams params = new HttpParams();
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("classsId", TApplication.getInstant().getClassId());
        if (TApplication.getInstant().getMajorModel() != null) {
            params.put("majorId", TApplication.getInstant().getMajorModel().getCode());
        }
        J.http().post(Urls.MY_DATA, ctx, params, new HttpCallback<Respond<MyData>>(ctx, true) {
            @Override
            public void success(Respond<MyData> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    MyData myData = res.getData();
                    tv_ct.setText(myData.getWrongTotal() + "");
                    tv_zy.setText(myData.getWorkTotal() + "");
                    tv_zql.setText(myData.getKnowledgePoint() + "");

                    tv111.setText("在本班级" + TApplication.getInstant().getMajorModel().getName() + "排第" + myData.getMyOrderNumByClass() + "名");
                    tv222.setText("在本年级" + TApplication.getInstant().getMajorModel().getName() + "排第" + myData.getMyOrderNumByGrade() + "名");
                }
            }
        });
    }

    class MyData extends BaseBean {
        private int workTotal;
        private int wrongTotal;
        private double knowledgePoint;
        private int myOrderNumByClass;
        private int myOrderNumByGrade;

        public int getWorkTotal() {
            return workTotal;
        }

        public void setWorkTotal(int workTotal) {
            this.workTotal = workTotal;
        }

        public int getWrongTotal() {
            return wrongTotal;
        }

        public void setWrongTotal(int wrongTotal) {
            this.wrongTotal = wrongTotal;
        }

        public double getKnowledgePoint() {
            return knowledgePoint;
        }

        public void setKnowledgePoint(double knowledgePoint) {
            this.knowledgePoint = knowledgePoint;
        }

        public int getMyOrderNumByClass() {
            return myOrderNumByClass;
        }

        public void setMyOrderNumByClass(int myOrderNumByClass) {
            this.myOrderNumByClass = myOrderNumByClass;
        }

        public int getMyOrderNumByGrade() {
            return myOrderNumByGrade;
        }

        public void setMyOrderNumByGrade(int myOrderNumByGrade) {
            this.myOrderNumByGrade = myOrderNumByGrade;
        }
    }


}
