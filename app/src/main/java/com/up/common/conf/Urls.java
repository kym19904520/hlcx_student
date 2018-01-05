package com.up.common.conf;

/**
 * TODO:
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by 王剑洪
 * On 2017/3/16.
 */

public class Urls {

    private static int interfaceDefaultType = 0;

    private static String head(){
        return getHead();
    }

    private static String getHead() {
        String HTTP_HEAD = "";
        switch (interfaceDefaultType){
            case 0:
                //测试接口工程地址
                HTTP_HEAD = "http://120.27.224.231:8081/easylearn/api/";
                break;
            case 1:
                //发布接口地址
                HTTP_HEAD = "http://116.62.243.203:8081/easylearn/api/";
                break;
            case 2:
                HTTP_HEAD = "http://192.168.1.39:8081/easylearn/api/";
                break;
            case 3:
                HTTP_HEAD = "http://120.27.224.231:8081/administrator/Luwq123456!@#";
                break;
        }
        return HTTP_HEAD;
    }

    /**
     * 平台滚动公告
     */
    public static String BANNER = head() + "home/bannerList";
    //登陆
    public static String LOGIN = head() + "parent/login";
    //退出登录
    public static String LOGIN_OUT = head() + "parent/logout";
    //获取验证码
    public static String GET_CODE = head() + "parent/getphonecode";
    //验证验证码
    public static String VERIFICATION_PHONE = head() + "parent/verificationphone";
    //注册获取验证码
    public static String REGISTER_GET_CODE = head() + "parent/getphonecodebyregister";
    //绑定学生
    public static String BINDING_STU = head() + "parent/bindingstudent";
    //意见反馈
    public static String FEEDBACK_SUBMIT = head() + "feedback/submit";
    //修改密码
    public static String UPDATE_PSW = head() + "parent/modifypassword";
    //获取任务列表
    public static String TASK_LIST = head() + "task/getTask";
    //获取试卷详情
    public static String GET_PAPERS_DETAILS = head() + "task/getPapersDetails";
    //获取学生试卷
    public static String GET_STU_PAPERS = head() + "task/getStudentPapers";
    //试卷图片上传
    public static String UPLOAD_PAPERS = head() + "task/saveStudentPapers";
    //试卷错题录入列表/答题卡
    public static String GET_ERROR_PAPERS = head() + "task/getErrorPapers";
    //设置题目正确
    public static String SAVE_CORRECT_PAPERS = head() + "task/saveCorrectPapers";
    //错题录入
    public static String SAVE_ERROR_PAPERS = head() + "task/saveErrorPapers";
    //试题解析
    public static String SAVE_SUBJECT_ANALYSIS = head() + "task/getSubjectAnalysis";
    //试卷提交
    public static String SAVE_PAPERS = head() + "task/savePapers";
    //提交结果
    public static String GET_PAPER_ANALYSIS = head() + "task/getPapersAnalysis";
    //根据试卷id获取老师信息
    public static String GET_TEACHER_INFO = head() + "task/getTeacherByRelationId";
    //线上作业-试题详情(列表)
    public static String SUBJECT_DETAILS = head() + "task/subjectDetails";
    //开始做题-提交答案
    public static String START_DOING = head() + "task/startDoing";
    //线上作业-完成作业
    public static String FINISH_ONLINE = head() + "task/finishOnlie";
    //作业结果
    public static String ONLINE_ANALYSIS = head() + "task/getOnlineAnalysis";
    //获得消息列表
    public static String GET_MES_LIST = head() + "task/getOfflineTaskByPage";
    //首页弹出层数据
    public static String GET_HTASK_TOTAL = head() + "task/getTaskTotalByGroup";
    //折线图-分析
    public static String LEARNING_LINE_GRAPH = head() + "analysis/getLearningLineGraph";
    //折线图分析-获取学科（废弃）
    public static String GET_MAJOR = head() + "analysis/getMajor";
    //我的学情分析--获取教材数据
    public static String LEARINGIN_ANALYSIS = head() + "analysis/getLearningAnalysis";
    //我的学情分析--获取章节数据
    public static String LEARINGIN_ANALYSIS_ZJ = head() + "analysis/getCurrentRateByMajorId";
    //我的学情分析--获取知识点数据
    public static String LEARINGIN_ANALYSIS_ZSD = head() + "analysis/getKnowPointList";

    //导出错题图片
    public static String EXPORT_PIC = head() + "wrongsubjectapp/exportPic.json";
    //导出错题word文档
    public static String EXPORT_DOC = head() + "wrongsubjectapp/exportWrongSubjectWord";
    //线上作业，导出图片
    public static String HOMEWORK_EXPORT_PIC = head() + "task/exportPapersByImg";
    //线上作业，导出doc
    public static String HOMEWORK_EXPORT_DOC = head() + "task/exportWordPaper";
    //智能组卷，导出试卷图片
    public static String ZNZJ_EXPORT_PIC = head() + "wrongsubjectapp/joinPapersByPic.json";
    //智能组卷，导出试卷DOC
    public static String ZNZJ_EXPORT_DOC = head() + "wrongsubjectapp/exportWordIntelligentPaper.json";
    //错题库---知识点分析---获取教材列表
    public static String GET_JC = head() + "wrongsubjectapp/knowledgeAnalysisForMaterial.json";
    //错题库---知识点分析---获取章节列表
    public static String GET_ZJ = head() + "wrongsubjectapp/knowledgeAnalysisForZj.json";
    //错题学科
    public static String ERROR_SUBJECT=head() + "wrongsubjectapp/getMajorsByParent.json";
    //错题库--学科--错题数量
    public static String ERROR_SUBJECT_CONTENT=head() + "wrongsubjectapp/getCountForMajorsByParent.json";
    //错题库-获取教材和章节
    public static String JC_AND_ZJ=head() + "wrongsubjectapp/getZjList.json";
    //智能组卷---获取知识点列表
    public static String GET_ZSD=head() + "wrongsubjectapp/getKonwledgeListByParent.json";
    //错题库，获取题型
    public static String SUBJECT_TYPE_LIST=head() + "wrongsubjectapp/getSubjectTypeList.json";
    //智能组卷(高级)
    public static String SMART_PAPERS=head() + "wrongsubjectapp/joinPapers.json";
    //智能组卷(简易)
    public static String SMART_EASY=head() + "wrongsubjectapp/intelligentPaper.json";
    //错题库，获取难度
    public static String DIFFICULTY_TYPE_LIST=head() + "wrongsubjectapp/getDifficultyTypeList.json";
    //错题库-错题扫除列表
    public static String ERROR_CLEAR_LIST=head() + "wrongsubjectapp/getWrongSubjectByParent.json";
    //提交错题图片
    public static String ERROR_CLEAR_PICTURE=head() + "wrongsubjectapp/updateWrongSubjectInfoByParent.json";
    //首页获取错题题数
    public static String ERROR_NUMBER=head() + "wrongsubjectapp/getWrongSubjectNumByParent.json";
    //获取错题库知识点解析列表
    public static String ERROR_KNOW_LIST=head() + "wrongsubjectapp/getjcZjZsdList.json";
    //错题库移除本题
    public static String ERROR_CLEAR=head() + "wrongsubjectapp/removeWrongSubjectByParent.json";
    //错题库批量移除正确的题目
    public static String ERROR_CLEAR_BATCH=head() + "wrongsubjectapp/removeWrongSubjectBatchByParent.json";
    //相似题
    public static String SAME_SEQ=head() + "wrongsubjectapp/getSimilarSubjectByParent.json";
    //错题库--已移除错题库--已移除错题数量列表
    public static String HAS_REMOVE_ERROR=head() + "wrongsubjectapp/getCountForRemovedByMajor.json";
    //错题库--已移除错题-错题题目列表
    public static String HAS_REMOVE_ERROR_LIST=head() + "wrongsubjectapp/getWrongSubjectByParent.json";
    //错题库---重新加入错题库
    public static String READD_ERRORS=head() + "wrongsubjectapp/rejoinWrongSubjectByParent.json";
    //知识点分析-题目列表
    public static String ZSDFX_SEQ_LIST=head() + "wrongsubjectapp/getWrongSubjectByChapterId.json";
    //我的试卷
    public static String MY_TEST=head() + "papersapp/getMyPapersByStuId.json";
    //试卷详情
    public static String MY_TEST_DETAIL=head() + "papersapp/getAttachedByPaperId.json";
    //获取上传图片的接口地址
    public static String GET_IMG_REQUEST_URL=head() + "postfile/img";
    //我的-错题，作业，准确率
    public static String MY_DATA=head() + "analysis/getWrongWorkCurrentRate";
    //查询题目
    public static String SEARCH_SEQ=head() + "select/getimagerecognition";
    //查询结果的错题录入（指定题目）
    public static String ERROR_INPUT=head() + "wrongsubjectapp/submitWrongSubject.json";
    //查询结果的错题录入（直接录入，未指定题目）
    public static String ERROR_INPUT_1=head() + "select/saveunfiledwrongsubject";

    public static String IMG_SEARCH_SEQ=head() + "select/getimagerecognitionbyimg";
    //修改我的头像
    public static String MY_PICTURE = head() + "parent/updateAvater";
}
