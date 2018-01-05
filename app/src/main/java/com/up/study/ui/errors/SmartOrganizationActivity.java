package com.up.study.ui.errors;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.BaseBean;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.Logger;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.ZjAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.ErrorSubjectModel;
import com.up.study.model.JcAndJZModel;
import com.up.study.model.ZnzjModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.qqtheme.framework.widget.WheelView;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 智能组卷
 */
public class SmartOrganizationActivity extends BaseFragmentActivity {

    private LinearLayout ll_gjsz;
    private TextView tv_sz_type;
    private Button btn_sure;

    private RelativeLayout rl_xk, rl_tl, rl_jc, rl_zj, rl_zsd, rl_tx, rl_nd;
    private TextView tv_xk, tv_tl, tv_jc, tv_zj, tv_zsd, tv_tx, tv_nd;
    private int cur_xk = -1;//当前选择的学科index
    private int cur_jc = -1;//当前选择的教材index
    private String major_id;//学科id
    private String jc_id;//教材id
    private int rows = 5;//题数
    private String subject_type = "";//题型
    private String subject_type_id = "";//题型id（多选，ID用逗号隔开，如1,2）
    private String chapter = "";//章节
    private String chapter_id = "";//章节id（多选，ID用逗号隔开，如1,2）
    private String knowledge = "";//知识点
    private String knowledge_id = "";//知识点（多选，ID用逗号隔开，如1,2）
    private String difficulty = "";//难度
    private String difficulty_id = "";//难度id（多选，ID用逗号隔开，如1,2）
    private ZnzjModel znzjModel = new ZnzjModel();

    @Override
    protected int getContentViewId() {
        return R.layout.act_smart_org;
    }

    @Override
    protected void initView() {
        ll_gjsz = bind(R.id.ll_gjsz);
        tv_sz_type = bind(R.id.tv_sz_type);
        btn_sure = bind(R.id.btn_sure);

        rl_xk = bind(R.id.rl_xk);
        rl_tl = bind(R.id.rl_tl);
        rl_jc = bind(R.id.rl_jc);
        rl_zj = bind(R.id.rl_zj);
        rl_zsd = bind(R.id.rl_zsd);
        rl_tx = bind(R.id.rl_tx);
        rl_nd = bind(R.id.rl_nd);

        tv_xk = bind(R.id.tv_xk);
        tv_tl = bind(R.id.tv_tl);
        tv_jc = bind(R.id.tv_jc);
        tv_zj = bind(R.id.tv_zj);
        tv_zsd = bind(R.id.tv_zsd);
        tv_tx = bind(R.id.tv_tx);
        tv_nd = bind(R.id.tv_nd);
    }

    @Override
    protected void initEvent() {
        tv_sz_type.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
        rl_xk.setOnClickListener(this);
        rl_tl.setOnClickListener(this);
        rl_zj.setOnClickListener(this);
        rl_zsd.setOnClickListener(this);
        rl_tx.setOnClickListener(this);
        rl_nd.setOnClickListener(this);
        rl_jc.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        znzjModel.setRows(rows + "");
    }

    @Override
    public void onClick(View v) {
        if (v == tv_sz_type) {     //高级和简易设置
            if ("高级设置".equals(tv_sz_type.getText().toString())) {
                tv_sz_type.setText("简易设置");
                ll_gjsz.setVisibility(View.VISIBLE);
                znzjModel.setEasySet(false);
            } else {
                tv_sz_type.setText("高级设置");
                ll_gjsz.setVisibility(View.GONE);
                znzjModel.setEasySet(true);
            }
        } else if (v == btn_sure) {//生成试卷
            if (!znzjModel.isEasySet()) {
                if (TextUtils.isEmpty(znzjModel.getMajor_id())) {
                    showToast("请选择学科");
                    return;
                }
                if (tv_jc.getText().toString().isEmpty() || tv_jc.getText() == null) {
                    showToast("请选择教材");
                    return;
                }
                if (tv_zj.getText().toString().isEmpty() || tv_zj.getText() == null) {
                    showToast("请选择章节");
                    return;
                }
                if (tv_zsd.getText().toString().isEmpty() || tv_zsd.getText() == null) {
                    showToast("请选择知识点");
                    return;
                }
                if (tv_tx.getText().toString().isEmpty() || tv_tx.getText() == null) {
                    showToast("请选择题型");
                    return;
                }
                if (tv_nd.getText().toString().isEmpty() || tv_nd.getText() == null) {
                    showToast("请选择难度");
                    return;
                }
                Logger.i("nandu", znzjModel + "");
                gotoActivityWithBean(SmartOrganizationResultActivity.class, znzjModel, null);
            }
            if (znzjModel.isEasySet()) {
                if (!TextUtils.isEmpty(znzjModel.getMajor_id())) {
                    gotoActivityWithBean(SmartOrganizationResultActivity.class, znzjModel, null);
                    Logger.i("nandu", znzjModel + "");
                } else {
                    showToast("请选择学科");
                }
            }
        } else if (v == rl_xk) {//学科
            getMaJorList();
        } else if (v == rl_tl) {//题量
            showDialog();
        } else if (v == rl_jc) {//教材
            getJcAndZj();
        } else if (v == rl_zj) {//章节
            showZj();
        } else if (v == rl_zsd) {//知识点
            getZsd();
        } else if (v == rl_tx) {//题型
            getTx();
        } else if (v == rl_nd) {//难度
            getNd();
        }
    }

    private AlertDialog dialog;

    /**
     * 选择题目的数量
     */
    private void showDialog() {
        dialog = new AlertDialog.Builder(ctx).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_tl);
        //初始化数据
        final ArrayList<String> lineList1 = new ArrayList<>();
        lineList1.add("1");
        lineList1.add("0");

        final ArrayList<String> lineList = new ArrayList<>();
        lineList.add("0");
        lineList.add("1");
        lineList.add("2");
        lineList.add("3");
        lineList.add("4");
        lineList.add("5");
        lineList.add("6");
        lineList.add("7");
        lineList.add("8");
        lineList.add("9");
        TextView tv_right = (TextView) window.findViewById(R.id.tv_right);

        //设置数据
        final WheelView wheelView_1 = (WheelView) window.findViewById(R.id.wheel_1);
        final WheelView wheelView_line = (WheelView) window.findViewById(R.id.wheel_line);
        final WheelView wheelView_section = (WheelView) window.findViewById(R.id.wheel_section);
        WheelView.LineConfig config = new WheelView.LineConfig();
        config.setColor(getResources().getColor(R.color.text_white));//设置分割线颜色

        wheelView_1.setLineConfig(config);
        wheelView_1.setTextColor(Color.GRAY, getResources().getColor(R.color.colorPrimary));//设置选中字体颜色
        wheelView_1.setTextSize(24);

        wheelView_line.setLineConfig(config);
        wheelView_line.setTextColor(Color.GRAY, getResources().getColor(R.color.colorPrimary));//设置选中字体颜色
        wheelView_line.setTextSize(24);
        wheelView_section.setLineConfig(config);
        wheelView_section.setTextColor(Color.GRAY, getResources().getColor(R.color.colorPrimary));
        wheelView_section.setTextSize(24);

        wheelView_1.setItems(lineList1, 0);//设置第一个WheelView
        wheelView_line.setItems(lineList, 0);//设置第一个WheelView
        wheelView_section.setItems(lineList, 0);
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lineValue1 = lineList1.get(wheelView_1.getSelectedIndex());
                String lineValue = lineList.get(wheelView_line.getSelectedIndex());
                String sectionValue = lineList.get(wheelView_section.getSelectedIndex());
                int num = Integer.parseInt(lineValue1 + lineValue + sectionValue);
                showLog("num=" + num);
                if (num < 5 || num > 100) {
                    showToast("您已超出题量范围：5-100");
                    return;
                }
                rows = num;
                znzjModel.setRows(rows + "");
                tv_tl.setText(num + "");
                dialog.cancel();

            }
        });
    }

    //List<MajorModel> majorModelList = new ArrayList<MajorModel>();//学科列表
    String[] majorNameList;//学科名称列表
    List<ErrorSubjectModel> majorModelList = new ArrayList<ErrorSubjectModel>();//学科列表

    /**
     * 获取学科
     */
    private void getMaJorList() {
        if (majorNameList != null && majorNameList.length > 0) {
            showMajor();
            return;
        }
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        J.http().post(Urls.ERROR_SUBJECT, ctx, params, new HttpCallback<Respond<List<ErrorSubjectModel>>>(null) {
            @Override
            public void success(Respond<List<ErrorSubjectModel>> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    List<ErrorSubjectModel> list = res.getData();
                    if (list != null && list.size() != 0) {
                        majorModelList.addAll(list);
                        majorNameList = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            majorNameList[i] = list.get(i).getName();
                        }
                        showMajor();
                    } else {
                        showToast("暂无学科");
                    }
                }
            }
        });
    }

    List<JcAndJZModel> jcList = new ArrayList<>();//教材列表
    HashMap<String, List<JcAndJZModel>> zjList = new HashMap<>();//章节hash
    String[] jcNameList;//教材名称列表

    /**
     * 获取教材和章节的数据
     */
    private void getJcAndZj() {
        jcList.clear();
        zjList.clear();
        if (TextUtils.isEmpty(major_id)) {
            showToast("请选择学科");
            return;
        }
        HttpParams params = new HttpParams();
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("major_id", znzjModel.getMajor_id());
        J.http().post(Urls.JC_AND_ZJ, ctx, params, new HttpCallback<Respond<List<JcAndJZModel>>>(ctx) {
            @Override
            public void success(Respond<List<JcAndJZModel>> res, Call call, Response response, boolean isCache) {

                if (Respond.SUC.equals(res.getCode())) {
                    List<JcAndJZModel> list = res.getData();
                    if (list != null && list.size() != 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if ("jc".equals(list.get(i).getType())) {
                                jcList.add(list.get(i));
                            }
                        }

                        for (int i = 0; i < list.size(); i++) {
                            for (int j = 0; j < jcList.size(); j++) {
                                if ("zj".equals(list.get(i).getType()) && list.get(i).getMaterial_id().equals(jcList.get(j).getId())) {//是章节，且教材id=教材id
                                    if (zjList.get(jcList.get(j).getId()) == null) {
                                        zjList.put(jcList.get(j).getId(), new ArrayList<JcAndJZModel>());
                                    }
                                    zjList.get(jcList.get(j).getId()).add(list.get(i));
                                }
                            }
                        }
                        showLog(zjList.size() + "");
                        jcNameList = new String[jcList.size()];
                        for (int i = 0; i < jcList.size(); i++) {
                            jcNameList[i] = jcList.get(i).getName();
                        }
                        showJc();
                    } else {
                        showToast("暂无教材");
                    }
                }
            }
        });
    }

    List<JcAndJZModel> zsdList = new ArrayList<>();//教材列表
    String[] zsdNameList;//知识点名称列表

    /**
     * 获取知识点的数据
     */
    private void getZsd() {
        zsdList.clear();
        if (TextUtils.isEmpty(chapter_id)) {
            showToast("请先选择章节");
            return;
        }
        HttpParams params = new HttpParams();
        params.put("chapter_id", chapter_id);
        J.http().post(Urls.GET_ZSD, ctx, params, new HttpCallback<Respond<List<JcAndJZModel>>>(ctx) {
            @Override
            public void success(Respond<List<JcAndJZModel>> res, Call call, Response response, boolean isCache) {

                if (Respond.SUC.equals(res.getCode())) {
                    List<JcAndJZModel> list = res.getData();
                    if (list != null && list.size() != 0) {
//                        zsdList.addAll(list);
//                        zsdNameList = new String[list.size()];
//                        for(int i = 0 ;i<list.size();i++){
//                            zsdNameList[i]=list.get(i).getName();
//                        }
                        showZsd(list);
                    } else {
                        showToast("找不到知识点");
                    }
                }
            }
        });
    }


    List<NameCode> txList = new ArrayList<NameCode>();//题型列表
    String[] txNameList;//题型名称列表

    /**
     * 获取题型的数据
     */
    private void getTx() {
        if (txNameList != null && txNameList.length > 0) {
            showTx();
            return;
        }
        HttpParams params = new HttpParams();
        J.http().post(Urls.SUBJECT_TYPE_LIST, ctx, params, new HttpCallback<Respond<List<NameCode>>>(ctx) {
            @Override
            public void success(Respond<List<NameCode>> res, Call call, Response response, boolean isCache) {

                if (Respond.SUC.equals(res.getCode())) {
                    List<NameCode> list = res.getData();
                    if (list != null && list.size() != 0) {
                        txList.addAll(list);
                        txNameList = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            txNameList[i] = list.get(i).getName();
                        }
                        showTx();
                    } else {
                        showToast("暂无题型");
                    }
                }
            }
        });
    }

    List<NameCode> ndList = new ArrayList<NameCode>();//难度列表
    String[] ndNameList;//难度名称列表

    /**
     * 获取难度的数据
     */
    private void getNd() {
        if (ndNameList != null && ndNameList.length > 0) {
            showNd();
            return;
        }
        HttpParams params = new HttpParams();
        J.http().post(Urls.DIFFICULTY_TYPE_LIST, ctx, params, new HttpCallback<Respond<List<NameCode>>>(ctx) {
            @Override
            public void success(Respond<List<NameCode>> res, Call call, Response response, boolean isCache) {

                if (Respond.SUC.equals(res.getCode())) {
                    List<NameCode> list = res.getData();
                    if (list != null && list.size() != 0) {
                        ndList.addAll(list);
                        ndNameList = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            ndNameList[i] = list.get(i).getName();
                        }
                        showNd();
                    } else {
                        showToast("暂无难度选项");
                    }
                }
            }
        });
    }

    //选择学科
    private void showMajor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择学科");
        builder.setSingleChoiceItems(majorNameList, cur_xk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cur_xk = which;
                tv_xk.setText(majorNameList[which]);
                major_id = majorModelList.get(which).getCode();
                znzjModel.setMajor_id(major_id);
                dialog.dismiss();
                //清除数据
                znzjModel.setMaterial_id("");
                znzjModel.setChapter_id("");
                znzjModel.setKnowledge("");

                jc_id = "";
                chapter_id = "";
                knowledge_id = "";

                tv_jc.setText("");
                tv_zj.setText("");
                tv_zsd.setText("");

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //选择教才
    private void showJc() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择教材");
        builder.setSingleChoiceItems(jcNameList, cur_jc, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cur_jc = which;
                tv_jc.setText(jcNameList[which]);
                jc_id = jcList.get(which).getId();
                znzjModel.setMaterial_id(jc_id);
                dialog.dismiss();

                //清除数据
                znzjModel.setChapter_id("");
                znzjModel.setKnowledge("");

                chapter_id = "";
                knowledge_id = "";

                tv_zj.setText("");
                tv_zsd.setText("");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    List<JcAndJZModel> zjs;
    String[] zjNameList;

    //选择章节
    private void showZj() {
        if (TextUtils.isEmpty(jc_id)) {
            showToast("请先选择教材");
            return;
        }
        zjs = zjList.get(jc_id);//章节列表
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(SmartOrganizationActivity.this, R.layout.view_capacity_zj, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        ListView listView = (ListView) view.findViewById(R.id.lv);
        TextView tv_left = (TextView) view.findViewById(R.id.tv_left);
        TextView tv_right = (TextView) view.findViewById(R.id.tv_right);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("选择章节");
        final ZjAdapter adapter = new ZjAdapter(SmartOrganizationActivity.this, zjs);
        listView.setAdapter(adapter);
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!adapter.flage) {
                    for (int i = 0; i < zjs.size(); i++) {
                        zjs.get(i).isCheck = true;
                    }
                    adapter.flage = true;
                    adapter.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < zjs.size(); i++) {
                        zjs.get(i).isCheck = false;
                    }
                    adapter.flage = false;
                    adapter.notifyDataSetChanged();
                }
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chapter = "";
                chapter_id = "";
                for (int i = 0; i < zjs.size(); i++) {
                    if (zjs.get(i).isCheck == true) {
                        chapter += zjs.get(i).getName() + "、";
                        chapter_id += zjs.get(i).getId() + ",";
                    }
                }
                if (chapter.length() > 0) {
                    chapter = chapter.substring(0, chapter.length() - 1);
                    chapter_id = chapter_id.substring(0, chapter_id.length() - 1);
                    znzjModel.setChapter_id(chapter_id);
                }
                tv_zj.setText(chapter);
                dialog.cancel();
                //清除数据
                znzjModel.setKnowledge("");

                knowledge_id = "";
                tv_zsd.setText("");
                dialog.dismiss();
            }
        });
        /**
         * 暂时不用
         */
        /*zjNameList = new String[zjs.size()];//章节名称列表
        for (int i = 0; i < zjs.size(); i++) {
            zjNameList[i] = zjs.get(i).getName();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择章节");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chapter = "";
                chapter_id = "";

                for (int i = 0; i < zjs.size(); i++) {
                    JcAndJZModel nameCode = zjs.get(i);
                    if (nameCode.isSelect()) {
                        chapter += zjs.get(i).getName() + "、";
                        chapter_id += zjs.get(i).getId() + ",";
                    }
                }
                if (chapter.length() > 0) {
                    chapter = chapter.substring(0, chapter.length() - 1);
                    chapter_id = chapter_id.substring(0, chapter_id.length() - 1);
                    znzjModel.setChapter_id(chapter_id);
                }
                tv_zj.setText(chapter);
                dialog.cancel();

                //清除数据
                znzjModel.setKnowledge("");

                knowledge_id = "";
                tv_zsd.setText("");

            }
        });
        boolean[] checkedItems = new boolean[zjs.size()];
        for (int i = 0; i < zjs.size(); i++) {
            checkedItems[i] = zjs.get(i).isSelect();
        }
        builder.setMultiChoiceItems(zjNameList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                zjs.get(which).setSelect(isChecked);
            }
        });*/
        dialog.show();
    }

    //选择知识点
    private void showZsd(final List<JcAndJZModel> list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(SmartOrganizationActivity.this, R.layout.view_capacity_zj, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        ListView listView = (ListView) view.findViewById(R.id.lv);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_left = (TextView) view.findViewById(R.id.tv_left);
        TextView tv_right = (TextView) view.findViewById(R.id.tv_right);
        tv_title.setText("选择知识点");
        final ZjAdapter adapter = new ZjAdapter(SmartOrganizationActivity.this, list);
        listView.setAdapter(adapter);
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!adapter.flage) {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).isCheck = true;
                    }
                    adapter.flage = true;
                    adapter.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).isCheck = false;
                    }
                    adapter.flage = false;
                    adapter.notifyDataSetChanged();
                }
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knowledge = "";
                knowledge_id = "";
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isCheck == true) {
                        knowledge += list.get(i).getName() + "、";
                        knowledge_id += list.get(i).getId() + ",";
                    }
                }
                if (knowledge.length() > 0) {
                    knowledge = knowledge.substring(0, knowledge.length() - 1);
                    knowledge_id = knowledge_id.substring(0, knowledge_id.length() - 1);
                    znzjModel.setKnowledge(knowledge_id);
                }
                tv_zsd.setText(knowledge);
                showLog(knowledge_id + "获取的id");
                dialog.dismiss();
            }
        });
        /**
         * 暂时不用
         */
        /*builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                knowledge = "";
                knowledge_id = "";

                for (int i = 0;i<zsdList.size();i++){
                    JcAndJZModel nameCode = zsdList.get(i);
                    if(nameCode.isSelect()) {
                        knowledge += zsdList.get(i).getName()+"、";
                        knowledge_id+=zsdList.get(i).getId()+",";
                    }
                }
                if(knowledge.length()>0){
                    knowledge = knowledge.substring(0,knowledge.length()-1);
                    knowledge_id = knowledge_id.substring(0,knowledge_id.length()-1);
                    znzjModel.setKnowledge(knowledge_id);
                }
                tv_zsd.setText(knowledge);
                dialog.cancel();
            }
        });
        final boolean[] checkedItems = new boolean[zsdList.size()];
        for (int i = 0;i<zsdList.size();i++){
            checkedItems[i] = zsdList.get(i).isSelect();
        }
        builder.setMultiChoiceItems(zsdNameList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                zsdList.get(which).setSelect(isChecked);
            }
        });*/
        dialog.show();
    }

    //选择题型
    private void showTx() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择题型");
        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                subject_type = "";
                subject_type_id = "";

                for (int i = 0; i < txList.size(); i++) {
                    NameCode nameCode = txList.get(i);
                    if (nameCode.isSelect()) {
                        subject_type += txList.get(i).getName() + "、";
                        subject_type_id += txList.get(i).getCode() + ",";
                    }
                }
                if (subject_type.length() > 0) {
                    subject_type = subject_type.substring(0, subject_type.length() - 1);
                    subject_type_id = subject_type_id.substring(0, subject_type_id.length() - 1);
                    znzjModel.setSubject_type(subject_type_id);
                }
                tv_tx.setText(subject_type);
                dialog.cancel();
            }
        }).setNegativeButton(R.string.dialog_off,null);
        boolean[] checkedItems = new boolean[txList.size()];
        for (int i = 0; i < txList.size(); i++) {
            checkedItems[i] = txList.get(i).isSelect();
        }
        builder.setMultiChoiceItems(txNameList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                txList.get(which).setSelect(isChecked);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //选择难度
    private void showNd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择难度");
        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                difficulty = "";
                difficulty_id = "";

                for (int i = 0; i < ndList.size(); i++) {
                    NameCode nameCode = ndList.get(i);
                    if (nameCode.isSelect()) {
                        difficulty += ndList.get(i).getName() + "、";
                        difficulty_id += ndList.get(i).getCode() + ",";
                    }
                }
                if (difficulty.length() > 0) {
                    difficulty = difficulty.substring(0, difficulty.length() - 1);
                    difficulty_id = difficulty_id.substring(0, difficulty_id.length() - 1);
                    znzjModel.setDifficulty(difficulty_id);
                }
                tv_nd.setText(difficulty);
                dialog.cancel();
            }
        }).setNegativeButton(R.string.dialog_off,null);
        boolean[] checkedItems = new boolean[ndList.size()];
        for (int i = 0; i < ndList.size(); i++) {
            checkedItems[i] = ndList.get(i).isSelect();
        }
        builder.setMultiChoiceItems(ndNameList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                ndList.get(which).setSelect(isChecked);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSingleChoiceDialog(String[] strs, String title, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setSingleChoiceItems(strs, cur_xk, listener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class NameCode extends BaseBean {
        private String name;
        private String code;
        private boolean isSelect;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }
    }
}
