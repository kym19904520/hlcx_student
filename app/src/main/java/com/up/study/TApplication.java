package com.up.study;

import android.text.TextUtils;
import android.widget.Toast;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.BaseApplication;
import com.up.common.conf.Constants;
import com.up.common.utils.Logger;
import com.up.common.utils.SPUtil;
import com.up.study.model.ErrorSubjectModel;
import com.up.study.model.Login;
import com.up.study.weight.GlideImageLoader;
import com.zxy.tiny.Tiny;

import java.util.HashMap;
import java.util.Map;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.jpush.android.api.JPushInterface;

/**
 * TODO:
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by 王剑洪
 * On 2017/3/12.
 */

public class TApplication extends BaseApplication {

    private static TApplication instant;
    public static TApplication getInstant(){
        return instant;
    }

    //用户参数，登录的时候记录
    private String userName;//家长用户名
    private String phone;//家长电话
    private int userId;//家长id
    private int studentId;//学生id
    /*private String studentName;//学生姓名
    private String studentNum;//学生学号
    private String className;//班级名称*/
    private int classId;//班级id
    private int gradeId;//年级id
    private Login loginInfo;

    //进人任务列表的时候记录该任务的一些参数，避免值在Activity中传递过于拖拉
    private int relationId;//试卷ID、（任务ID）
    private int relationType;//任务类型 1:试卷录入 2:线上作业

    private ErrorSubjectModel majorModel;//学科，首页
    //
    private String marjorId;//科目id

    private boolean hasGotoLogin=false;//是否已经跳到登录页
    private boolean refreshHomeData=false;//重新刷新home
    private boolean refreshError=false;//重新刷新错题

    private Map<String,Integer> errors = new HashMap<>();//错题导出错题数

    private String wxAppId="wx1a6f5b95a2f08a6d";

    /**
     * 清除数据
     */
    public void clear(){
        userName = "";
        //phone= "";//家长电话
        userId=0;//家长id
        studentId=0;//学生id
        classId=0;//班级id
        gradeId=0;//年级id
        //进人任务列表的时候记录该任务的一些参数，避免值在Activity中传递过于拖拉
        relationId=0;//试卷ID、（任务ID）
        relationType=0;//任务类型 1:试卷录入 2:线上作业

        majorModel=null;//学科，首页
        marjorId = "";//科目id
        hasGotoLogin=false;//是否已经跳到登录页
        refreshHomeData=false;//重新刷新home
        refreshError=false;//重新刷新错题
        errors.clear();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instant=this;
        HttpParams params = new HttpParams();
        params.put("rows", 20);//默认每页20条
        J.initHttp(this, null, params);
        initGalleryFinal();
        Tiny.getInstance().init(this);

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    private void initGalleryFinal(){
        //设置主题
        //ThemeConfig.CYAN
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(getResources().getColor(R.color.colorPrimary))
        .build();
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setForceCrop(true)
//                .setEnableRotate(true)
//                .setCropSquare(true)
//                .setEnablePreview(true)
                .build();

        //配置imageloader
        ImageLoader imageloader = new GlideImageLoader();
        CoreConfig coreConfig = new CoreConfig.Builder(instant, imageloader, theme)
                .setFunctionConfig(functionConfig)
                .build();
        GalleryFinal.init(coreConfig);
    }

    public Login getLoginInfo(){

        if(loginInfo==null) {
            Logger.i(Logger.TAG,"111111111111111");
            return  new Login().get();
        }
        else{
            Logger.i(Logger.TAG,"22222222222222");
            return loginInfo;
        }
    }

    public ErrorSubjectModel getMajorModel() {
        if (majorModel==null){
            majorModel = new ErrorSubjectModel();
            majorModel.setCode("0");
            majorModel.setName("未知");
        }
        return majorModel;
    }

    public void setMajorModel(ErrorSubjectModel majorModel) {
        this.majorModel = majorModel;
    }

    public String getUserName() {
        if(TextUtils.isEmpty(userName)){
            userName = SPUtil.getString(this, Constants.SP_USER_NAME,"");
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        if(TextUtils.isEmpty(phone)){
            phone = SPUtil.getString(this, Constants.SP_USER_PHONE,"");
        }
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public int getUserId() {
        if(userId == 0){
            String uId= SPUtil.getString(this, Constants.SP_USER_ID,"");
            if (!TextUtils.isEmpty(uId)){
                userId=Integer.parseInt(uId);
            }
        }
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClassId() {
        if(classId == 0){
            String id= SPUtil.getString(this, Constants.SP_CLASS_ID,"");
            if (!TextUtils.isEmpty(id)){
                classId=Integer.parseInt(id);
            }
        }
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getRelationId() {
        if(relationId == 0){
            String id= SPUtil.getString(this, Constants.SP_RELATION_ID,"");
            if (!TextUtils.isEmpty(id)){
                relationId=Integer.parseInt(id);
            }
        }
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    public int getRelationType() {
        if(relationType == 0){
            String id= SPUtil.getString(this, Constants.SP_RELATION_TYPE,"");
            if (!TextUtils.isEmpty(id)){
                relationType=Integer.parseInt(id);
            }
        }
        return relationType;
    }

    public void setRelationType(int relationType) {
        this.relationType = relationType;
    }

    public int getStudentId() {
        if(studentId == 0){
            String id= SPUtil.getString(this, Constants.SP_STUDENT_ID,"");
            if (!TextUtils.isEmpty(id)){
                studentId=Integer.parseInt(id);
            }
        }
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
       /* if(TextUtils.isEmpty(studentName)){
            studentName = SPUtil.getString(this, Constants.SP_STUDENT_NAME,"");
        }
        return studentName;*/
        return  SPUtil.getString(this, Constants.SP_STUDENT_NAME,"");
    }

    public void setStudentName(String studentName) {
        //this.studentName = studentName;
    }

    public String getStudentNum() {
        /*if(TextUtils.isEmpty(studentNum)){
            studentNum = SPUtil.getString(this, Constants.SP_STUDENT_NUM,"");
        }
        return studentNum;*/
        return SPUtil.getString(this, Constants.SP_STUDENT_NUM,"");
    }

    public void setStudentNum(String studentNum) {
        //this.studentNum = studentNum;
    }

    public String getClassName() {
        /*if(TextUtils.isEmpty(className)){
            className = SPUtil.getString(this, Constants.SP_CLASS_NAME,"");
        }
        return className;*/
        return SPUtil.getString(this, Constants.SP_CLASS_NAME,"");
    }

    public void setClassName(String className) {
        //this.className = className;
    }

    public int getGradeId() {
        if(gradeId == 0){
            String id= SPUtil.getString(this, Constants.SP_GRADE_ID,"");
            if (!TextUtils.isEmpty(id)){
                gradeId=Integer.parseInt(id);
            }
        }
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public String getMarjorId() {
        return marjorId;
    }

    public void setMarjorId(String marjorId) {
        this.marjorId = marjorId;
    }

    public boolean isHasGotoLogin() {
        return hasGotoLogin;
    }

    public void setHasGotoLogin(boolean hasGotoLogin) {
        this.hasGotoLogin = hasGotoLogin;
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public boolean isRefreshHomeData() {
        return refreshHomeData;
    }

    public void setRefreshHomeData(boolean refreshHomeData) {
        this.refreshHomeData = refreshHomeData;
    }

    public boolean isRefreshError() {
        return refreshError;
    }

    public void setRefreshError(boolean refreshError) {
        this.refreshError = refreshError;
    }

    public Map<String, Integer> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, Integer> errors) {
        this.errors = errors;
    }

    public String getWxAppId() {
        return wxAppId;
    }

    public void setWxAppId(String wxAppId) {
        this.wxAppId = wxAppId;
    }
}
