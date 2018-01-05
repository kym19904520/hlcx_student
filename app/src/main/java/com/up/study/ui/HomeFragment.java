package com.up.study.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.Logger;
import com.up.common.utils.SPUtil;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.TopicPagerAdapter;
import com.up.study.base.BaseFragment;
import com.up.study.model.ErrorNumberModel;
import com.up.study.model.ErrorSubjectModel;
import com.up.study.model.HomeDialogModel;
import com.up.study.model.LineDataModel;
import com.up.study.ui.errors.ErrorsClearActivity;
import com.up.study.ui.home.MessageActivity;
import com.up.study.ui.home.SearchActivity;
import com.up.study.ui.home.TaskActivity;
import com.up.study.weight.ScaleCircleNavigator;
import com.up.study.weight.camera.TakePhotoActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by dell on 2017/4/20.
 */

public class HomeFragment extends BaseFragment {

    private AlertDialog dialog;
    private ImageView iv_renwu, iv_renwu_num, iv_mes, iv_mes_num, iv_head;
    private TextView tv_1,tv_2,tv_3;
    private LinearLayout ll_not_clear,ll_already_clear,ll_already_grasp;

    private ViewPager viewPager;
    private MagicIndicator magicIndicator;
    private LayoutInflater mInflater;
    private List<View> mViews = new ArrayList<View>();

    private TextView tv_search;

    //List<MajorModel> majorModelList = new ArrayList<MajorModel>();//学科列表
    List<LineDataModel> lineDataModelList = new ArrayList<LineDataModel>();

    List<HomeDialogModel> homeDialogModelList = new ArrayList<>();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && TApplication.getInstant().isRefreshHomeData()) {//可见
            showLog("----------刷新HOME-----------可见");
            getDialogDatas();
            getMaJorList();
            TApplication.getInstant().setRefreshHomeData(false);
        } else {

        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fra_home;
    }

    @Override
    protected void initView() {
        iv_renwu = bind(R.id.iv_renwu);
        iv_renwu_num = bind(R.id.iv_renwu_num);
        iv_mes = bind(R.id.iv_mes);
        iv_mes_num = bind(R.id.iv_mes_num);
        iv_head = bind(R.id.iv_head);
        viewPager = bind(R.id.viewpager);
        magicIndicator = bind(R.id.magic_indicator);
        tv_search = bind(R.id.tv_search);
        ll_not_clear = bind(R.id.ll_not_clear);
        ll_already_clear = bind(R.id.ll_already_clear);
        ll_already_grasp = bind(R.id.ll_already_grasp);
        tv_1 = bind(R.id.tv_1);
        tv_2 = bind(R.id.tv_2);
        tv_3 = bind(R.id.tv_3);
        getDialogDatas();
        getMaJorList();
    }

    @Override
    protected void initEvent() {
        iv_renwu.setOnClickListener(this);
        iv_mes.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        ll_not_clear.setOnClickListener(this);
        ll_already_clear.setOnClickListener(this);
        ll_already_grasp.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        getErrorsNumber();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1://任务列表返回
                iv_renwu_num.setVisibility(View.GONE);
                break;
            case 2://消息列表返回
                iv_mes_num.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == iv_renwu) {
            //gotoActivity(TaskActivity.class,null);
            Intent intent = new Intent();
            intent.setClass(getActivity(), TaskActivity.class);
            startActivityForResult(intent, 1);
        } else if (v == iv_mes) {
            //gotoActivity(MessageActivity.class,null);
            Intent intent = new Intent();
            intent.setClass(getActivity(), MessageActivity.class);
            startActivityForResult(intent, 2);
        } else if (v == iv_head) {
//            gotoActivity(FacePhotoActivity.class,null);
            gotoActivity(TakePhotoActivity.class, null);
        } else if (v == tv_search) {
            gotoActivity(SearchActivity.class, null);
        }else if (v == ll_not_clear){                  //未扫除
            gotoActivity(ErrorsClearActivity.class,"type","1");
        }else if (v == ll_already_clear){               //已扫除
            gotoActivity(ErrorsClearActivity.class,"type","2");
        }else if (v == ll_already_grasp){               //已掌握
            gotoActivity(ErrorsClearActivity.class,"type","3");
        }
    }

    private void showDialog() {
        if (mParentActivity.isFinishing()) {
            Logger.i(Logger.TAG, "Activity已经关闭，dialog不用展示");
            return;
        }
        if (TApplication.getInstant().getStudentId() == 0) {
            showToast("请先绑定学生！");
            return;
        }
        if (dialog == null) {
            dialog = new AlertDialog.Builder(ctx, R.style.NoBackGroundDialog).create();
        }
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_home);
        ImageView iv_close = (ImageView) window.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        if (homeDialogModelList.size() == 3) {
            if (homeDialogModelList.get(0).getTotal() == 0 && homeDialogModelList.get(1).getTotal() == 0 && homeDialogModelList.get(2).getTotal() == 0) {
                dialog.cancel();
                return;
            }
            //1:试卷录入,2:线上作业,3:线下作业
            for (int i = 0; i < homeDialogModelList.size(); i++) {
                if (homeDialogModelList.get(i).getRelationTypeId() == 1) {
                    TextView tv_2_name = (TextView) window.findViewById(R.id.tv_2_name);
                    tv_2_name.setText(homeDialogModelList.get(i).getRelationType());
                    TextView tv_2_num = (TextView) window.findViewById(R.id.tv_2_num);
                    tv_2_num.setText("x" + homeDialogModelList.get(i).getTotal());
                    final TextView tv_2_btn = (TextView) window.findViewById(R.id.tv_2_btn);
                    if (homeDialogModelList.get(i).getTotal() > 0) {
                        iv_renwu_num.setVisibility(View.VISIBLE);
                        tv_2_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HomeFragment.this.gotoActivity(TaskActivity.class, null);
                                dialog.cancel();
                            }
                        });
                    } else {
                        tv_2_btn.setBackgroundResource(R.drawable.round_gray_small_background);
                    }
                } else if (homeDialogModelList.get(i).getRelationTypeId() == 2) {
                    TextView tv_3_name = (TextView) window.findViewById(R.id.tv_3_name);
                    tv_3_name.setText(homeDialogModelList.get(i).getRelationType());
                    TextView tv_3_num = (TextView) window.findViewById(R.id.tv_3_num);
                    tv_3_num.setText("x" + homeDialogModelList.get(i).getTotal());
                    TextView tv_3_btn = (TextView) window.findViewById(R.id.tv_3_btn);
                    if (homeDialogModelList.get(i).getTotal() > 0) {
                        iv_renwu_num.setVisibility(View.VISIBLE);
                        tv_3_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HomeFragment.this.gotoActivity(TaskActivity.class, null);
                                dialog.cancel();
                            }
                        });
                    } else {
                        tv_3_btn.setBackgroundResource(R.drawable.round_gray_small_background);
                    }
                } else if (homeDialogModelList.get(i).getRelationTypeId() == 3) {
                    TextView tv_1_name = (TextView) window.findViewById(R.id.tv_1_name);
                    tv_1_name.setText(homeDialogModelList.get(i).getRelationType());
                    TextView tv_1_num = (TextView) window.findViewById(R.id.tv_1_num);
                    tv_1_num.setText("x" + homeDialogModelList.get(i).getTotal());
                    TextView tv_1_btn = (TextView) window.findViewById(R.id.tv_1_btn);
                    if (homeDialogModelList.get(i).getTotal() > 0) {
                        iv_mes_num.setVisibility(View.VISIBLE);
                        tv_1_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HomeFragment.this.gotoActivity(MessageActivity.class, null);
                                dialog.cancel();
                            }
                        });
                    } else {
                        tv_1_btn.setBackgroundResource(R.drawable.round_gray_small_background);
                    }
                }
            }

        } else {
            showToast("窗口数据量不对");
            dialog.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getErrorsNumber();
    }

    private void initViewPager() {
        mViews.clear();
        mInflater = mParentActivity.getLayoutInflater();
        for (int i = 0; i < majorModelList.size(); i++) {
            View view = mInflater.inflate(R.layout.view_chart, null);
            initHelloChart(view, i);
            mViews.add(view);
        }

        viewPager.setAdapter(new TopicPagerAdapter(mViews));
        viewPager.setOffscreenPageLimit(mViews.size());//限制存储在内存的页数
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                showLog("onPageSelected:" + position);
                getLineDataList(majorModelList.get(position).getCode(), position);
                TApplication.getInstant().setMajorModel(majorModelList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
       /* CircleNavigator circleNavigator = new CircleNavigator(this);
        circleNavigator.setCircleCount(mViews.size());
        circleNavigator.setCircleColor(Color.GRAY);
        circleNavigator.setRadius(13);*/
        ScaleCircleNavigator scaleCircleNavigator = new ScaleCircleNavigator(mParentActivity);
        scaleCircleNavigator.setCircleCount(mViews.size());
        scaleCircleNavigator.setNormalCircleColor(Color.LTGRAY);
        scaleCircleNavigator.setSelectedCircleColor(Color.DKGRAY);
        scaleCircleNavigator.setMinRadius(10);
        scaleCircleNavigator.setMaxRadius(12);
        magicIndicator.setNavigator(scaleCircleNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    /**
     * 获取错题个数
     */
    public void getErrorsNumber(){
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id","2");
        params.put("grade_id", TApplication.getInstant().getGradeId());
        J.http().post(Urls.ERROR_NUMBER, ctx, params, new HttpCallback<Respond<ErrorNumberModel>>(ctx,true,true) {
            @Override
            public void success(Respond<ErrorNumberModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    ErrorNumberModel errorNumberModel = res.getData();
                    tv_1.setText(errorNumberModel.getCleaning() + "题");
                    tv_2.setText(errorNumberModel.getCleaned() + "题");
                    tv_3.setText( errorNumberModel.getKnowed() + "题");
                }
            }
        });
    }

    /**
     * 获取折线图学科 废弃
     */
    /*private void getMaJorList() {
        majorModelList.clear();
        HttpParams params = new HttpParams();
        params.put("classsId", TApplication.getInstant().getClassId());
        J.http().post(Urls.GET_MAJOR, ctx, params, new HttpCallback<Respond<List<MajorModel>>>(ctx) {
            @Override
            public void success(Respond<List<MajorModel>> res, Call call, Response response, boolean isCache) {

                if(Respond.SUC.equals(res.getCode())){
                    List<MajorModel> list = res.getData();
                    if(list!=null){
                        majorModelList.addAll(list);
                        initViewPager();
                        getLineDataList(majorModelList.get(0).getCode(),0);
                    }
                }
            }
        });
    }*/

    List<ErrorSubjectModel> majorModelList = new ArrayList<ErrorSubjectModel>();//学科列表

    /**
     * 获取学科
     */
    private void getMaJorList() {
        majorModelList.clear();
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        J.http().post(Urls.ERROR_SUBJECT, ctx, params, new HttpCallback<Respond<List<ErrorSubjectModel>>>(ctx, true) {
            @Override
            public void success(Respond<List<ErrorSubjectModel>> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    List<ErrorSubjectModel> list = res.getData();
                    if (list != null && list.size() > 0) {
                        majorModelList.addAll(list);
                        TApplication.getInstant().setMajorModel(majorModelList.get(0));
                        initViewPager();
                        getLineDataList(majorModelList.get(0).getCode(), 0);
                    }
                }
            }
        });
    }

    /**
     * 获取折线图学科数据
     */
    private void getLineDataList(String majorId, final int index) {
        lineDataModelList.clear();
        HttpParams params = new HttpParams();
        params.put("majorId", majorId);
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());

        J.http().post(Urls.LEARNING_LINE_GRAPH, ctx, params, new HttpCallback<Respond<List<LineDataModel>>>(ctx, true) {
            @Override
            public void success(Respond<List<LineDataModel>> res, Call call, Response response, boolean isCache) {

                if (Respond.SUC.equals(res.getCode())) {
                    List<LineDataModel> list = res.getData();
                    if (list != null) {
                        lineDataModelList.addAll(list);
                        initHelloChart(mViews.get(index), index);
                    }
                }
            }
        });
    }

    /**
     * 获取弹窗数据
     */
    private void getDialogDatas() {
        homeDialogModelList.clear();
        showLog("getDialogDatas============");
        HttpParams params = new HttpParams();
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("readTaskID", SPUtil.getTaskMesId(mParentActivity, 1));
        params.put("readOffTask", SPUtil.getTaskMesId(mParentActivity, 2));
        J.http().post(Urls.GET_HTASK_TOTAL, getActivity(), params, new HttpCallback<Respond<List<HomeDialogModel>>>(ctx) {
            @Override
            public void success(Respond<List<HomeDialogModel>> res, Call call, Response response, boolean isCache) {

                if (Respond.SUC.equals(res.getCode())) {
                    List<HomeDialogModel> list = res.getData();
                    if (list != null) {
                        homeDialogModelList.addAll(list);
                        showDialog();
                    }
                }
            }
        });
    }

    private void initHelloChart(View view, int index) {
        if (getActivity() == null) {
            return;
        }
        //设置节点、X、Y轴属性及添加数据：
        /*List<String> list = new ArrayList<String>();
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        List<PointValue> pointValues = new ArrayList<PointValue>();// 节点数据结合
        Axis axisY = new Axis().setHasLines(true);// Y轴属性
        Axis axisX = new Axis();// X轴属性
        axisY.setName("y");//设置Y轴显示名称
        axisX.setName("x");//设置X轴显示名称
        ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();//定义X轴刻度值的数据集合
        ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();//定义Y轴刻度值的数据集合
        axisX.setValues(axisValuesX);//为X轴显示的刻度值设置数据集合
        axisX.setLineColor(Color.BLACK);// 设置X轴轴线颜色
        axisY.setLineColor(Color.BLACK);// 设置Y轴轴线颜色
        axisX.setTextColor(Color.BLUE);// 设置X轴文字颜色
        axisY.setTextColor(Color.GREEN);// 设置Y轴文字颜色
        axisX.setTextSize(14);// 设置X轴文字大小
        axisX.setTypeface(Typeface.DEFAULT);// 设置文字样式，此处为默认
        //axisX.setHasTiltedLabels(bolean isHasTit);// 设置X轴文字向左旋转45度
        axisX.setHasLines(true);// 是否显示X轴网格线
        axisY.setHasLines(false);// 是否显示Y轴网格线
        axisX.setHasSeparationLine(true);// 设置是否有分割线
        axisX.setInside(false);// 设置X轴文字是否在X轴内部
        for (int j = 0; j < list.size(); j++) {//循环为节点、X、Y轴添加数据
            pointValues.add(new PointValue(j,j));// 添加节点数据
            axisValuesY.add(new AxisValue(j).setValue(j));// 添加Y轴显示的刻度值
            axisValuesX.add(new AxisValue(j).setValue(j).setLabel(""+j));// 添加X轴显示的刻度值
        }

        //设置折线Line的属性：
        List<Line> lines = new ArrayList<Line>();//定义线的集合
        Line line = new Line(pointValues);//将值设置给折线
        line.setColor(Color.RED);// 设置折线颜色
        line.setStrokeWidth(20);// 设置折线宽度
        line.setFilled(false);// 设置折线覆盖区域是否填充
        line.setCubic(false);// 是否设置为立体的
        line.setPointColor(Color.GREEN);// 设置节点颜色
        line.setPointRadius(50);// 设置节点半径
        line.setHasLabels(true);// 是否显示节点数据
        line.setHasLines(true);// 是否显示折线
        line.setHasPoints(true);// 是否显示节点
        line.setShape(ValueShape.CIRCLE);// 节点图形样式 DIAMOND菱形、SQUARE方形、CIRCLE圆形
        line.setHasLabelsOnlyForSelected(false);// 隐藏数据，触摸可以显示
        //lines.add(line);
        //设置LineChartData属性及为chart设置数据：
        LineChartData chartData = new LineChartData(lines);//将线的集合设置为折线图的数据
        chartData.setAxisYLeft(axisY);// 将Y轴属性设置到左边
        chartData.setAxisXBottom(axisX);// 将X轴属性设置到底部
        //chartData.setAxisYRight(axisYRight);//设置右边显示的轴
        //chartData.setAxisXTop(axisXTop);//设置顶部显示的轴
        chartData.setBaseValue(20);// 设置反向覆盖区域颜色
        chartData.setValueLabelBackgroundAuto(true);// 设置数据背景是否跟随节点颜色
        chartData.setValueLabelBackgroundColor(Color.BLUE);// 设置数据背景颜色
        chartData.setValueLabelBackgroundEnabled(true);// 设置是否有数据背景
        chartData.setValueLabelsTextColor(Color.BLACK);// 设置数据文字颜色
        chartData.setValueLabelTextSize(15);// 设置数据文字大小
        chartData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式
        lineChart.setLineChartData(chartData);//最后为图表设置数据，数据类型为LineChartData*/

        LineChartView lineChart = (LineChartView) view.findViewById(R.id.linechart);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        if (majorModelList.get(index) != null) {
            tv_title.setText(majorModelList.get(index).getName());
        }

        List<PointValue> values = new ArrayList<>();
        ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();//定义X轴刻度值的数据集合
        for (int i = 0; i < lineDataModelList.size(); i++) {
            values.add(new PointValue(i, lineDataModelList.get(i).getY()));
            axisValuesX.add(new AxisValue(i).setLabel((int) lineDataModelList.get(i).getX() + ""));// 添加X轴显示的刻度值
        }
        /*values.add(new PointValue(1, 10));
        values.add(new PointValue(2, 30));
        values.add(new PointValue(3, 50));
        values.add(new PointValue(4, 60));
        values.add(new PointValue(5, 80));
        values.add(new PointValue(6, 10));

        axisValuesX.add(new AxisValue(1).setLabel("9月"));
        axisValuesX.add(new AxisValue(2).setLabel("10月"));
        axisValuesX.add(new AxisValue(3).setLabel("11月"));
        axisValuesX.add(new AxisValue(4).setLabel("12月"));
        axisValuesX.add(new AxisValue(5).setLabel("1月"));
        axisValuesX.add(new AxisValue(6).setLabel("2月"));*/
        /**
         * 简单模拟的数据
         */
        /*values.add(new PointValue(1, 0));
        values.add(new PointValue(2, 0));
        values.add(new PointValue(3, 0));
        values.add(new PointValue(4, 0));
        values.add(new PointValue(5, 0));
        values.add(new PointValue(6, 0));*/
        //setCubic(true),true是曲线型，false是直线连接
        Line line = new Line(values).setColor(getResources().getColor(R.color.line_pink)).setCubic(false);
        line.setStrokeWidth(2);// 设置折线宽度
        line.setPointRadius(4);// 设置节点半径
        //line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据 （设置了这个line.setHasLabels(true);就无效）
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);
        Axis axisX = new Axis();
        //axisX.setHasLines(true);//设定是否有网格线
        Axis axisY = new Axis();
        axisY.setMaxLabelChars(6);//max label length, for example 60
        axisY.setHasLines(true);
        //为两个坐标系设定名称
        axisX.setTextColor(Color.DKGRAY);// 设置X轴文字颜色
        axisY.setTextColor(Color.DKGRAY);// 设置Y轴文字颜色
        axisX.setTextSize(12);// 设置X轴文字大小
        axisY.setTextSize(12);

        /*for (int j = 1; j < 7; j++) {//循环为节点、X、Y轴添加数据
            // axisValuesY.add(new AxisValue(j).setValue(j));// 添加Y轴显示的刻度值
            axisValuesX.add(new AxisValue(j).setValue(j).setLabel("0"+j));// 添加X轴显示的刻度值
        }*/
        ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();//定义Y轴刻度值的数据集合
        axisValuesY.add(new AxisValue(0).setValue(0).setLabel("0"));
        axisValuesY.add(new AxisValue(25).setValue(25).setLabel("25"));
        axisValuesY.add(new AxisValue(50).setValue(50).setLabel("50"));
        axisValuesY.add(new AxisValue(75).setValue(75).setLabel("75"));
        axisValuesY.add(new AxisValue(100).setValue(100).setLabel("100"));


        axisX.setValues(axisValuesX);//为X轴显示的刻度值设置数据集合
        //axisY.setAutoGenerated(false);
        axisY.setValues(axisValuesY);


        //设置图标所在位置
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        //将数据添加到View中
        lineChart.setLineChartData(data);
        lineChart.setZoomEnabled(false);

        final Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        lineChart.setMaximumViewport(v);
        lineChart.setCurrentViewport(v);
    }
}
