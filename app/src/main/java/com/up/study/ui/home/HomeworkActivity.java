package com.up.study.ui.home;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;
import com.up.common.J;
import com.up.common.base.BaseBean;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.ImageUtil;
import com.up.common.utils.Logger;
import com.up.common.utils.SPUtil;
import com.up.common.widget.LoadingDialog;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragment;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.TaskModel;

import java.io.File;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 家庭作业
 */
public class HomeworkActivity extends BaseFragmentActivity {

    private Button btn_up,btn_down;
    private TextView tv_title,tv_num,tv_time,tv_subject,tv_teacher;
    private ImageView iv;
    private TaskModel taskModel;
    @Override
    protected int getContentViewId() {
        return R.layout.act_home_work;
    }

    @Override
    protected void initView() {
        btn_up = bind(R.id.btn_up);
        btn_down = bind(R.id.btn_down);
        tv_title = bind(R.id.tv_title);
        tv_num = bind(R.id.tv_num);
        tv_time = bind(R.id.tv_time);
        tv_subject = bind(R.id.tv_subject);
        tv_teacher = bind(R.id.tv_teacher);
        iv = bind(R.id.iv);
    }

    @Override
    protected void initEvent() {
        btn_up.setOnClickListener(this);
        btn_down.setOnClickListener(this);
        registerForContextMenu(btn_down);//btn是要点击的控件
    }

    @Override
    protected void initData() {
        getTeacherInfo();
        taskModel =(TaskModel)getIntent().getSerializableExtra("bean1");

        SPUtil.saveTaskMesId(this,taskModel.getId()+"",1);

        tv_title.setText(taskModel.getTitle());
        tv_num.setText(taskModel.getSubjectSum()+"题");
        tv_time.setText(taskModel.getDeadline());
        tv_subject.setText(taskModel.getMajorName());
    }

    @Override
    public void onClick(View v) {
        if(v==btn_down){//导出
//            importImg();
            v.showContextMenu();//单击直接显示Context菜单
        }
        else if(v==btn_up){//开始做题
            gotoActivity(DoHomeworkActivity.class,null);
        }
    }

    /**
     * 获取老师部分信息
     */
    private void getTeacherInfo() {
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        J.http().post(Urls.GET_TEACHER_INFO, ctx, params, new HttpCallback<Respond<Teacher>>(ctx) {
            @Override
            public void success(Respond<Teacher> res, Call call, Response response, boolean isCache) {
               Teacher teacher = res.getData();
                if (teacher==null){
                    tv_teacher.setText("任课老师：");
                    J.image().loadCircle(HomeworkActivity.this, null, iv);
                }
                else {
                    tv_teacher.setText("任课老师：" + teacher.name);
                    J.image().loadCircle(HomeworkActivity.this, teacher.getHead(), iv);
                }
            }

        });
    }

    //导出图片
    private void importImg() {
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("classsId", TApplication.getInstant().getClassId());
//        params.put("title",taskModel.getTitle());
        OkGo.get(Urls.HOMEWORK_EXPORT_PIC)//
                .tag(this)//
                .params(params)
                .execute(new FileCallback(taskModel.getTitle()+"_"+new Date().getTime()+"_qsx.png") {  //文件下载时，可以指定下载的文件目录和文件名
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // file 即为文件数据，文件保存在指定目录
                        showLog(file.getName());
//                        showToast("下载完成，图片已经保存在sdcard/download文件下");
                        Toast.makeText(HomeworkActivity.this, "下载完成，图片已经保存在sdcard/download文件下", Toast.LENGTH_LONG).show();
                        hideLoading();
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        initLoadingDialog();
                        showLoading("下载中...");
                    }
                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调下载进度(该回调在主线程,可以直接更新ui)
                    }
                });
    }


    /**
     * 导出word
     */
    private void loadDoc() {
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        J.http().post(Urls.HOMEWORK_EXPORT_DOC, ctx, params, new HttpCallback<Respond<FileUrl>>(ctx) {
            @Override
            public void success(Respond<FileUrl> res, Call call, Response response, boolean isCache) {
                String fileUrl = res.getData().getFileUrl();
                importDoc(fileUrl);
            }
        });
    }

    //导出word
    private void importDoc(String fileUrl) {
        HttpParams params = new HttpParams();
        OkGo.get(fileUrl)//
                .tag(this)//
                .params(params)
                .execute(new FileCallback(taskModel.getTitle()+"_"+new Date().getTime()+"_qsx.doc") {  //文件下载时，可以指定下载的文件目录和文件名
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // file 即为文件数据，文件保存在指定目录
                        showLog(file.getName());
//                        showToast("下载完成，图片已经保存在sdcard/download文件下");
                        Toast.makeText(HomeworkActivity.this, "下载完成，word已经保存在sdcard/download文件下", Toast.LENGTH_LONG).show();
                        hideLoading();
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        initLoadingDialog();
                        showLoading("下载中...");
                    }
                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调下载进度(该回调在主线程,可以直接更新ui)
                    }
                });
    }

    private  class  FileUrl extends BaseBean{
        private String fileUrl;

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }
    public class Teacher extends BaseBean {
        private  String head;
        private  String name;

        public String getHead() {
            return head;
        }

        public void setHead(String head) {
            this.head = head;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.load,menu);
        //menu.setHeaderTitle("选择导出方式");
        /*menu.add(0, 3, 0, "修改");
        menu.add(0, 4, 1, "删除");*/
        //给menu设置布局文件，当触发时显示在界面上
    }

   @Override
   public boolean onContextItemSelected(MenuItem item) {
       //布局文件里面对应的id，当点击时，根据id区别那个被点击
       switch (item.getItemId()){
           case R.id.text1:
               showLog("=========importImg=================");
               importImg();
               break;
           case R.id.text2:
               showLog("=========loadDoc=================");
               loadDoc();
               break;
       }
       return super.onOptionsItemSelected(item);
   }

}
