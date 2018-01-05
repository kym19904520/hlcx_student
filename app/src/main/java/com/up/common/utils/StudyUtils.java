package com.up.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.widget.MyGridView;
import com.up.common.widget.MyListView;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.ImgSelectorAdpter;
import com.up.study.adapter.RecyclerViewAdapter;
import com.up.study.base.BaseFragment;
import com.up.study.callback.CallBack;
import com.up.study.callback.ImgCallBack;
import com.up.study.callback.ReqImgUrlCallBack;
import com.up.study.listener.CameraResultCallback;
import com.up.study.listener.ImgSeletorGalleryFinalListener;
import com.up.study.model.ImgUrl;
import com.up.study.model.LcSeqModel;
import com.up.study.model.Login;
import com.up.study.model.Options;
import com.up.study.model.ReqImgUrl;
import com.up.study.model.SeqModel;
import com.up.study.weight.showimages.ImagePagerActivity;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by dell on 2017/6/19.
 */

public class StudyUtils {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    public final static int TYPE_KGT = 1;
    public final static int TYPE_ZGT = 2;

    /**
     * 获取科目类型 ，1：客观题：（单选，多选，判断）2： 主观题（简答题、填空题、计算题、应用题）
     *
     * @return
     */
    public static int getSubjectType(String subjectType) {
        if (subjectType.contains("单选") || subjectType.contains("多选") || subjectType.contains("判断")) {
            return 1;
        }
        return 2;

    }

    /**
     * 是否需要提交按钮
     *
     * @return
     */
    public static boolean getNeedSumitBtn(String subjectType) {
        if (subjectType.contains("单选") || subjectType.contains("判断")) {
            return false;
        }
        return true;
    }

    public static void onlyGetImgRequsetUrl(Context ctx, final ReqImgUrlCallBack callBack) {
        HttpParams params = new HttpParams();
        J.http().post(Urls.GET_IMG_REQUEST_URL, ctx, params, new HttpCallback<Respond<String>>(null) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {

                    String options = res.getData();
                    Type type = new TypeToken<ReqImgUrl>() {
                    }.getType();
                    ReqImgUrl reqImgUrl = new Gson().fromJson(options, type);
                    Logger.i(Logger.TAG, reqImgUrl.getDir());
                    callBack.suc(reqImgUrl);
                }
            }
        });
    }

    /**
     * 获取到上传地址，直接上传图片
     *
     * @param ctx
     * @param filePath
     * @param fileName
     * @param imgCallBack
     */
    public static void getImgRequsetUrl(final Context ctx, final String filePath, final String fileName, final ImgCallBack imgCallBack) {
        HttpParams params = new HttpParams();
        J.http().post(Urls.GET_IMG_REQUEST_URL, ctx, params, new HttpCallback<Respond<String>>(ctx) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {

                    String options = res.getData();
                    Type type = new TypeToken<ReqImgUrl>() {
                    }.getType();
                    ReqImgUrl reqImgUrl = new Gson().fromJson(options, type);

                    Logger.i(Logger.TAG, reqImgUrl.getDir());
                    uploadImg(ctx, reqImgUrl, filePath, fileName, imgCallBack);
                }
            }
        });
    }

    public static void uploadImg(Context ctx, final ReqImgUrl reqImgUrl, String filePath, String fileName, final ImgCallBack imgCallBack) {
        final String key = reqImgUrl.getDir() + "/"+sdf.format(new Date()) + new Random().nextInt(100)+ fileName;
        Logger.i(Logger.TAG, "key=" + key);
        OkGo.post("http://" + reqImgUrl.getReadhost())//
                .tag(ctx)//
                // .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .params("OSSAccessKeyId", reqImgUrl.getAccessid())
                .params("policy", reqImgUrl.getPolicy())
                .params("signature", reqImgUrl.getSignature())
                .params("key", key)
                .params("success_action_status", 200)
                .params("file", new File(filePath))   // 可以添加文件上传
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //上传成功
                        Logger.i(Logger.TAG, "图片上传到阿里云成功！图片地址：" + reqImgUrl.getReadhost() + "/" + key);
                        Logger.i(Logger.TAG, response.message());
//                        imgCallBack.suc("http://" + reqImgUrl.getReadhost() + "/" + key);
                        imgCallBack.suc("/" + key);
                        //未返回图片地址

                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                    }
                });
    }

    /**
     * 初始化图片view
     */
    public static void initImgView(View view, final List<String> imgList, final Context ctx,
                                   final ImgCallBack imgCallBack1,
                                   final ImgCallBack imgCallBack2,
                                   final ImgCallBack imgCallBack3) {
        final ImageView error_iv_1 = (ImageView) view.findViewById(R.id.error_iv_1);
        final ImageView error_iv_2 = (ImageView) view.findViewById(R.id.error_iv_2);
        final ImageView error_iv_3 = (ImageView) view.findViewById(R.id.error_iv_3);
        final ImageView error_iv_clear_1 = (ImageView) view.findViewById(R.id.error_iv_clear_1);
        final ImageView error_iv_clear_2 = (ImageView) view.findViewById(R.id.error_iv_clear_2);
        final ImageView error_iv_clear_3 = (ImageView) view.findViewById(R.id.error_iv_clear_3);

        error_iv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_1, new CameraResultCallback.CameraSucCallBack() {
                    @Override
                    public void success(String filePath) {
                        error_iv_clear_1.setVisibility(View.VISIBLE);
                        imgCallBack1.suc(filePath);
                        imgList.add(filePath);
                    }
                }));
            }
        });
        error_iv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_2, new CameraResultCallback.CameraSucCallBack() {
                    @Override
                    public void success(String filePath) {
                        error_iv_clear_2.setVisibility(View.VISIBLE);
                        imgCallBack2.suc(filePath);
                        imgList.add(filePath);
                    }
                }));
            }
        });
        error_iv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_3, new CameraResultCallback.CameraSucCallBack() {
                    @Override
                    public void success(String filePath) {
                        error_iv_clear_3.setVisibility(View.VISIBLE);
                        imgCallBack3.suc(filePath);
                        imgList.add(filePath);
                    }
                }));
            }
        });
        error_iv_clear_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error_iv_1.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
                error_iv_clear_1.setVisibility(View.GONE);
                imgList.remove(0);
            }
        });
        error_iv_clear_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error_iv_2.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
                error_iv_clear_2.setVisibility(View.GONE);
                imgList.remove(0);
            }
        });
        error_iv_clear_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error_iv_3.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
                error_iv_clear_3.setVisibility(View.GONE);
                imgList.remove(0);
            }
        });

    }

    /**
     * 初始化图片view 直接用Activity
     */
    public static void initImgActivity(final Activity view,
                                       final ImgCallBack imgCallBack1,
                                       final ImgCallBack imgCallBack2,
                                       final ImgCallBack imgCallBack3) {
        final ImageView error_iv_1 = (ImageView) view.findViewById(R.id.error_iv_1);
        final ImageView error_iv_2 = (ImageView) view.findViewById(R.id.error_iv_2);
        final ImageView error_iv_3 = (ImageView) view.findViewById(R.id.error_iv_3);
        final ImageView error_iv_clear_1 = (ImageView) view.findViewById(R.id.error_iv_clear_1);
        final ImageView error_iv_clear_2 = (ImageView) view.findViewById(R.id.error_iv_clear_2);
        final ImageView error_iv_clear_3 = (ImageView) view.findViewById(R.id.error_iv_clear_3);

        error_iv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_1, new CameraResultCallback.CameraSucCallBack() {
                    @Override
                    public void success(String filePath) {
                        error_iv_clear_1.setVisibility(View.VISIBLE);
                        imgCallBack1.suc(filePath);
                    }
                }));
            }
        });
        error_iv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_2, new CameraResultCallback.CameraSucCallBack() {
                    @Override
                    public void success(String filePath) {
                        error_iv_clear_2.setVisibility(View.VISIBLE);
                        imgCallBack2.suc(filePath);
                    }
                }));
            }
        });
        error_iv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_3, new CameraResultCallback.CameraSucCallBack() {
                    @Override
                    public void success(String filePath) {
                        error_iv_clear_3.setVisibility(View.VISIBLE);
                        imgCallBack3.suc(filePath);
                    }
                }));
            }
        });
        error_iv_clear_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_iv_1.setImageBitmap(ImageUtil.readBitMap(view, R.mipmap.sy_add_img));
                error_iv_clear_1.setVisibility(View.GONE);
            }
        });
        error_iv_clear_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_iv_2.setImageBitmap(ImageUtil.readBitMap(view, R.mipmap.sy_add_img));
                error_iv_clear_2.setVisibility(View.GONE);
            }
        });
        error_iv_clear_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_iv_3.setImageBitmap(ImageUtil.readBitMap(view, R.mipmap.sy_add_img));
                error_iv_clear_3.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 初始化题目页面
     */
    public static void initSeqView(View view, SeqModel seqModel, Activity ctx) {
        TextView tv_subject_type = (TextView) view.findViewById(R.id.tv_subject_type);
        tv_subject_type.setText(seqModel.getSubjectType());
        //TextView tv_subject_title = (TextView) view.findViewById(R.id.tv_subject_title);
        String knowName = seqModel.getKnowName();
        if (knowName != null) {
            //List<String> list2 = Arrays.asList(knowName.split(","));
            List<String> knowNameList = null;
            try {
                Type type1 = new TypeToken<List<String>>() {
                }.getType();
                knowNameList = new Gson().fromJson(knowName, type1);
            }
            catch (Exception e){
                knowNameList = Arrays.asList(knowName.split(","));
            }


            //找到这个Listview
            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recylist);
            //设置线性管理器
            LinearLayoutManager ms = new LinearLayoutManager(ctx);
            ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
            mRecyclerView.setLayoutManager(ms);
            RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(knowNameList);
            mRecyclerView.setAdapter(myAdapter);
        }

        //客观题初始化选项
        if (StudyUtils.getSubjectType(seqModel.getSubjectType()) == StudyUtils.TYPE_KGT) {

            //tv_subject_title.setText(seqModel.getTitle());
            WebView wv_title = (WebView) view.findViewById(R.id.wv_title);
            WidgetUtils.initWebView(wv_title, seqModel.getTitle());
            final String options = seqModel.getOptions();
            final Type type = new TypeToken<List<Options>>() {
            }.getType();
            try{
                final List<Options> optionsList = new Gson().fromJson(options, type);
                StudyUtils.clearOptionList(optionsList);
                if (optionsList!=null){
                    final MyListView lv = (MyListView) view.findViewById(R.id.lv);
                    final CommonAdapter<Options> adapter = new CommonAdapter<Options>(ctx, optionsList, R.layout.item_topic_kgt) {
                        @Override
                        public void convert(ViewHolder vh, Options item, int position) {
                            TextView tv_answer = vh.getView(R.id.tv_answer);
                            TextView tv_answer_text = vh.getView(R.id.tv_answer_text);
                            tv_answer.setText(item.getOpt());
                            tv_answer_text.setText(item.getText());
                        }
                    };
                    lv.setAdapter(adapter);
                }
            }catch (Exception e){

            }

        }
        //主观题初始化内容
        else {
            final List<String> imgList = new ArrayList<String>();
            WebView webView = (WebView) view.findViewById(R.id.web_view);
            WidgetUtils.initWebView(webView, seqModel.getTitle());

            MyListView lv = (MyListView) view.findViewById(R.id.lv);
            lv.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化题目页面,直接在activity中处理
     */
    public static void initSeqActivity(SeqModel seqModel, Activity view) {
        TextView tv_subject_type = (TextView) view.findViewById(R.id.tv_subject_type);
        if (TextUtils.isEmpty(seqModel.getSubjectType())){
            return;
        }
        tv_subject_type.setText(seqModel.getSubjectType());
        //TextView tv_subject_title = (TextView) view.findViewById(R.id.tv_subject_title);
        String knowName = seqModel.getKnowName();
        if (knowName != null) {
            //List<String> list2 = Arrays.asList(knowName.split(","));
            /*Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> knowNameList = new Gson().fromJson(knowName, type1);*/
            List<String> knowNameList = null;
            try {
                Type type1 = new TypeToken<List<String>>() {
                }.getType();
                knowNameList = new Gson().fromJson(knowName, type1);
            }
            catch (Exception e){
                knowNameList = Arrays.asList(knowName.split(","));
            }
            //找到这个Listview
            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recylist);
            //设置线性管理器
            LinearLayoutManager ms = new LinearLayoutManager(view);
            ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
            mRecyclerView.setLayoutManager(ms);
            RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(knowNameList);
            mRecyclerView.setAdapter(myAdapter);
        }

        //客观题初始化选项
        if (StudyUtils.getSubjectType(seqModel.getSubjectType()) == StudyUtils.TYPE_KGT) {

            //tv_subject_title.setText(seqModel.getTitle());
            WebView webView = (WebView) view.findViewById(R.id.wv_title);
            WidgetUtils.initWebView(webView, seqModel.getTitle());

            final String options = seqModel.getOptions();
            final Type type = new TypeToken<List<Options>>() {
            }.getType();
            final List<Options> optionsList = new Gson().fromJson(options, type);
            StudyUtils.clearOptionList(optionsList);
            if (optionsList!=null&&optionsList.size()>0){
                final MyListView lv = (MyListView) view.findViewById(R.id.lv);
                final CommonAdapter<Options> adapter = new CommonAdapter<Options>(view, optionsList, R.layout.item_topic_kgt) {
                    @Override
                    public void convert(ViewHolder vh, Options item, int position) {
                        TextView tv_answer = vh.getView(R.id.tv_answer);
                        TextView tv_answer_text = vh.getView(R.id.tv_answer_text);
                        tv_answer.setText(item.getOpt());
                        tv_answer_text.setText(item.getText());
                    }
                };
                lv.setAdapter(adapter);
            }

        }
        //主观题初始化内容
        else {
            final List<String> imgList = new ArrayList<String>();
            WebView webView = (WebView) view.findViewById(R.id.web_view);
            WidgetUtils.initWebView(webView, seqModel.getTitle());

            MyListView lv = (MyListView) view.findViewById(R.id.lv);
            lv.setVisibility(View.GONE);
        }
    }

    public static void initAnswerAndAnalysis(Activity ctx, SeqModel seqModel) {
        /*String answerjson = seqModel.getAnswer();
        Type type2 = new TypeToken<List<String>>() {
        }.getType();
        List<String> answerList = new Gson().fromJson(answerjson, type2);
*/
        //TextView tv_answer = (TextView) ctx.findViewById(R.id.tv_right_answer);
        //tv_answer.setText(FormatUtils.List2String(answerList));
        WebView wv_answer = (WebView)ctx.findViewById(R.id.wv_answer);
        WidgetUtils.initAnswerWebView(wv_answer,seqModel.getAnswer());

        WebView wv_analysis = (WebView) ctx.findViewById(R.id.wv_analysis);
        WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
    }

    public static void initErrorCause(Activity ctx, final EditText et_D, final CallBack<String> callBack) {

        final LinearLayout ll_A = (LinearLayout) ctx.findViewById(R.id.ll_A);
        final LinearLayout ll_B = (LinearLayout) ctx.findViewById(R.id.ll_B);
        final LinearLayout ll_D = (LinearLayout) ctx.findViewById(R.id.ll_D);
        final LinearLayout ll_C = (LinearLayout) ctx.findViewById(R.id.ll_C);
        final TextView tv_A = (TextView) ctx.findViewById(R.id.tv_A);
        final TextView tv_B = (TextView) ctx.findViewById(R.id.tv_B);
        final TextView tv_C = (TextView) ctx.findViewById(R.id.tv_C);
        final TextView tv_D = (TextView) ctx.findViewById(R.id.tv_D);

        et_D.setEnabled(false);

        ll_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.suc("A");
                tv_A.setBackgroundResource(R.drawable.round_blue_circle_background);
                tv_B.setBackgroundResource(R.drawable.round_gray_circle_background);
                tv_C.setBackgroundResource(R.drawable.round_gray_circle_background);
                tv_D.setBackgroundResource(R.drawable.round_gray_circle_background);
                et_D.setEnabled(false);
                et_D.setText(null);
            }
        });
        ll_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.suc("B");
                tv_B.setBackgroundResource(R.drawable.round_blue_circle_background);
                tv_A.setBackgroundResource(R.drawable.round_gray_circle_background);
                tv_C.setBackgroundResource(R.drawable.round_gray_circle_background);
                tv_D.setBackgroundResource(R.drawable.round_gray_circle_background);
                et_D.setEnabled(false);
                et_D.setText(null);
            }
        });
        ll_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.suc("C");
                tv_C.setBackgroundResource(R.drawable.round_blue_circle_background);
                tv_B.setBackgroundResource(R.drawable.round_gray_circle_background);
                tv_A.setBackgroundResource(R.drawable.round_gray_circle_background);
                tv_D.setBackgroundResource(R.drawable.round_gray_circle_background);
                et_D.setEnabled(false);
                et_D.setText(null);
            }
        });
        ll_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.suc("D");
                tv_D.setBackgroundResource(R.drawable.round_blue_circle_background);
                tv_B.setBackgroundResource(R.drawable.round_gray_circle_background);
                tv_C.setBackgroundResource(R.drawable.round_gray_circle_background);
                tv_A.setBackgroundResource(R.drawable.round_gray_circle_background);
                et_D.setEnabled(true);
                et_D.setFocusable(true);
                et_D.setFocusableInTouchMode(true);
            }
        });
    }

    /**
     * 初始化图片view
     */
    public static void initImgView(View view, final String[] imgs, final Context ctx,
                                   final ImgCallBack imgCallBack1,
                                   final ImgCallBack imgCallBack2,
                                   final ImgCallBack imgCallBack3) {
        final ImageView error_iv_1 = (ImageView) view.findViewById(R.id.error_iv_1);
        final ImageView error_iv_2 = (ImageView) view.findViewById(R.id.error_iv_2);
        final ImageView error_iv_3 = (ImageView) view.findViewById(R.id.error_iv_3);
        final ImageView error_iv_clear_1 = (ImageView) view.findViewById(R.id.error_iv_clear_1);
        final ImageView error_iv_clear_2 = (ImageView) view.findViewById(R.id.error_iv_clear_2);
        final ImageView error_iv_clear_3 = (ImageView) view.findViewById(R.id.error_iv_clear_3);

        error_iv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_1, new CameraResultCallback.CameraSucCallBack() {
                    @Override
                    public void success(String filePath) {
                        error_iv_clear_1.setVisibility(View.VISIBLE);
                        imgCallBack1.suc(filePath);
                    }
                }));
            }
        });
        error_iv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_2, new CameraResultCallback.CameraSucCallBack() {
                    @Override
                    public void success(String filePath) {
                        error_iv_clear_2.setVisibility(View.VISIBLE);
                        imgCallBack2.suc(filePath);
                    }
                }));
            }
        });
        error_iv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_3, new CameraResultCallback.CameraSucCallBack() {
                    @Override
                    public void success(String filePath) {
                        error_iv_clear_3.setVisibility(View.VISIBLE);
                        imgCallBack3.suc(filePath);
                    }
                }));
            }
        });
        error_iv_clear_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error_iv_1.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
                error_iv_clear_1.setVisibility(View.GONE);
                imgs[0] = "";
            }
        });
        error_iv_clear_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error_iv_2.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
                error_iv_clear_2.setVisibility(View.GONE);
                imgs[1] = "";
            }
        });
        error_iv_clear_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error_iv_3.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
                error_iv_clear_3.setVisibility(View.GONE);
                imgs[2] = "";
            }
        });

    }

    public static void uploadImgUrl(String[] localImgList, final Context ctx, final CallBack<List<ImgUrl>> callBack) {
        final List<String> strList = new ArrayList<>();
        strList.addAll(StringUtils.ArrayToList(localImgList));
        final List<ImgUrl> urls = new ArrayList<>();//上传到阿里云的图片地址
        onlyGetImgRequsetUrl(ctx, new ReqImgUrlCallBack() {
            @Override
            public void suc(ReqImgUrl reqImgUrl) {
                for (int i = 0; i < strList.size(); i++) {
                    final int index = i;
                    uploadImg(ctx, reqImgUrl,strList.get(i), "img.png", new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                            //返回的是阿里云的图片地址
                            ImgUrl imgUrl = new ImgUrl();
                            imgUrl.setUrl(imgPath);
                            urls.add(imgUrl);
                            if (index == strList.size() - 1) {//最后一个图片
                                callBack.suc(urls);
                            }
                        }
                    });
                }
            }
        });

    }

    public static void uploadImgUrl(List<String> localImgList, final Context ctx, final CallBack<List<ImgUrl>> callBack) {
        final List<String> strList = new ArrayList<>();
        strList.addAll(localImgList);
        final List<ImgUrl> urls = new ArrayList<>();//上传到阿里云的图片地址
        onlyGetImgRequsetUrl(ctx, new ReqImgUrlCallBack() {
            @Override
            public void suc(ReqImgUrl reqImgUrl) {
                for (int i = 0; i < strList.size(); i++) {
                    final int index = i;
                    uploadImg(ctx, reqImgUrl,strList.get(i), "img.png", new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                            //返回的是阿里云的图片地址
                            Logger.i(Logger.TAG,"上传到阿里云的第"+(index+1)+"张图片地址："+imgPath);
                            ImgUrl imgUrl = new ImgUrl();
                            imgUrl.setUrl(imgPath);
                            urls.add(imgUrl);
                            if (urls.size() == strList.size()) {//最后一个图片
                                callBack.suc(urls);
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     *
     * @param ctx
     * @param imageList 图片地址，包括add的添加图片
     * @param maxSize 图片选择最大数
     */
    public static  void initImgSelector(final Activity ctx,final List<String> imageList,final int maxSize){
        MyGridView mgv = (MyGridView)ctx.findViewById(R.id.mgv);
        imageList.add("add");
        final ImgSelectorAdpter imgAdapter = new ImgSelectorAdpter(ctx, imageList, R.layout.item_gv_image,imageList);
        mgv.setAdapter(imgAdapter);
        mgv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                if ("add".equals(imageList.get(i))) {//点击的是默认的添加图标，则去添加图片
                    FunctionConfig functionConfig  = StudyUtils.getConfigForGalleryMuti(maxSize - (imageList.size() - 1));
                    GalleryFinal.openGalleryMuti(1, functionConfig, new ImgSeletorGalleryFinalListener(imageList,imgAdapter,maxSize));
                } else {//查看图片
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.addAll(imageList);
                    if ("add".equals(temp.get(temp.size() - 1))) {
                        temp.remove(temp.size() - 1);
                    }
                    Intent intent = new Intent(ctx, ImagePagerActivity.class);
                    intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, temp);
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    ctx.startActivity(intent);
                }

            }
        });

    }

    public static  ImgSelectorAdpter initImgSelector2(final Activity ctx,final List<String> imageList,final int maxSize){
        MyGridView mgv = (MyGridView)ctx.findViewById(R.id.mgv);
        imageList.add("add");
        final ImgSelectorAdpter imgAdapter = new ImgSelectorAdpter(ctx, imageList, R.layout.item_gv_image,imageList);
        mgv.setAdapter(imgAdapter);
        mgv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                if (imageList.get(i).equals("add")) {//点击的是默认的添加图标，则去添加图片
                    FunctionConfig functionConfig  = StudyUtils.getConfigForGalleryMuti(maxSize - (imageList.size() - 1));
                    GalleryFinal.openGalleryMuti(1, functionConfig, new ImgSeletorGalleryFinalListener(imageList,imgAdapter,maxSize));
                } else {//查看图片
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.addAll(imageList);
                    if (temp.get(temp.size() - 1).equals("add")) {
                        temp.remove(temp.size() - 1);
                    }
                    Intent intent = new Intent(ctx, ImagePagerActivity.class);
                    intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, temp);
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    ctx.startActivity(intent);
                }

            }
        });
        return imgAdapter;
    }

    public static void getLoginInfo(Context ctx){
        String userId= SPUtil.getString(ctx, Constants.SP_USER_ID,"");
        String classId =  SPUtil.getString(ctx,Constants.SP_CLASS_ID,"");
        String studentId =  SPUtil.getString(ctx,Constants.SP_STUDENT_ID,"");
        String gradeId =  SPUtil.getString(ctx,Constants.SP_GRADE_ID,"");

        if (!TextUtils.isEmpty(userId)){
            TApplication.getInstant().setUserId(Integer.parseInt(userId));
        }
        if(!TextUtils.isEmpty(classId)){
            TApplication.getInstant().setClassId(Integer.parseInt(classId));
        }
        if(!TextUtils.isEmpty(studentId)){
            TApplication.getInstant().setStudentId(Integer.parseInt(studentId));
        }
        if (!TextUtils.isEmpty(gradeId)){
            TApplication.getInstant().setGradeId(Integer.parseInt(gradeId));
        }
        TApplication.getInstant().setPhone(SPUtil.getString(ctx, Constants.SP_USER_PHONE,""));
        TApplication.getInstant().setUserName(SPUtil.getString(ctx,Constants.SP_USER_NAME,""));
        TApplication.getInstant().setStudentNum(SPUtil.getString(ctx,Constants.SP_STUDENT_NUM,""));
        TApplication.getInstant().setStudentName(SPUtil.getString(ctx,Constants.SP_STUDENT_NAME,""));
        TApplication.getInstant().setClassName(SPUtil.getString(ctx,Constants.SP_CLASS_NAME,""));
    }
    public static void saveLoginInfo(Context ctx, Login loginData,String userPassword){

        if (loginData.getUser()!=null) {
            SPUtil.putString(ctx, Constants.SP_USER_PHONE, loginData.getUser().getAccount());
            SPUtil.putString(ctx,Constants.SP_USER_NAME,loginData.getUser().getName());
            SPUtil.putString(ctx,Constants.SP_USER_ID,loginData.getUser().getId()+"");

            TApplication.getInstant().setPhone(loginData.getUser().getAccount());
            TApplication.getInstant().setUserId(loginData.getUser().getId());
            TApplication.getInstant().setUserName(loginData.getUser().getName());
        }

        if (loginData.getClasss()!=null){
            SPUtil.putString(ctx,Constants.SP_CLASS_ID,loginData.getClasss().getId()+"");
            SPUtil.putString(ctx,Constants.SP_CLASS_NAME,loginData.getClasss().getName()+"");
            SPUtil.putString(ctx,Constants.SP_GRADE_ID,loginData.getClasss().getGrade()+"");
            TApplication.getInstant().setClassId(loginData.getClasss().getId());
            TApplication.getInstant().setClassName(loginData.getClasss().getName());
            TApplication.getInstant().setGradeId(loginData.getClasss().getGrade());
        }

        if (loginData.getStudent()!=null) {
            SPUtil.putString(ctx, Constants.SP_STUDENT_ID, loginData.getStudent().getId() + "");
            SPUtil.putString(ctx, Constants.SP_STUDENT_NAME, loginData.getStudent().getName() + "");
            SPUtil.putString(ctx, Constants.SP_STUDENT_NUM, loginData.getStudent().getCode() + "");
            TApplication.getInstant().setStudentId(loginData.getStudent().getId());
            TApplication.getInstant().setStudentName(loginData.getStudent().getName());
            TApplication.getInstant().setStudentNum(loginData.getStudent().getCode());
        }
        SPUtil.putString(ctx, Constants.SP_USER_PSW, userPassword);
        SPUtil.putString(ctx, Constants.SP_IMG_URL, loginData.getIMGROOT());
    }


    public static void clearOptionList(List<Options> optionsList) {
        if(optionsList==null){
            return;
        }
        for (int i = 0;i<optionsList.size();i++){
            if(TextUtils.isEmpty(optionsList.get(i).getText())||optionsList.get(i).getText().contains("null")){
                optionsList.remove(i);
                i--;
            }
        }

    }

    public static FunctionConfig getConfigForGalleryMuti(int maxSize) {
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setMutiSelectMaxSize(maxSize)
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
//                    .setForceCrop(true)
                .setForceCropEdit(true)
                .setEnablePreview(true)
                .build();
        return functionConfig;
    }

    /**
     * 处理掉添加图片的图片
     * @param list
     * @return
     */
    public static List<String> getUploadImgs(ArrayList<String> list) {
        ArrayList<String> imageList = new ArrayList<>();
        imageList.addAll(list);
        if (imageList.get(imageList.size() - 1).equals("add")) {
            imageList.remove(imageList.size() - 1);
        }
        return imageList;
    }

    public static ArrayList<String> imgUrl2String(List<ImgUrl> dataList) {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0;i<dataList.size();i++){
            temp.add(dataList.get(i).getUrl());
        }
        return temp;
    }

    /**
     * 显示该题的错误原因和错误图片
     * @param fragment
     * @param seqModel
     */
    public static void showSeqErrorCauseAndImg(final BaseFragment fragment, LcSeqModel seqModel) {
        try {
            LinearLayout ll_error = fragment.bind(R.id.ll_error);
            ll_error.setVisibility(View.VISIBLE);
            TextView tv_error_cause =fragment.bind(R.id.tv_error_cause);
            MyGridView mgv_error_img =fragment.bind(R.id.mgv_error_img);

            String [] imgJsons = seqModel.getErrAttached().split(",;;");
            String [] errorNames = seqModel.getErrName().split(",;;");
            String [] errorTypes = seqModel.getErr_type().split(",;;");
            String [] errorDescribe = seqModel.getErr_describe().split(",;;");
            Logger.i("返回的图片",imgJsons.length + "");
            if(imgJsons.length==errorNames.length&&imgJsons.length==errorTypes.length/*&&imgJsons.length==errorDescribe.length*/){
                Logger.i(Logger.TAG,"----值正常");
            }
            else{
                fragment.showLog("错误描述数量不统一");
                return;
            }

            List<ImgUrl> allImgUrl = new ArrayList<>();
            String errorCause = "";

            for (int i = 0;i<imgJsons.length;i++){
                //处理错题图片
                Type type = new TypeToken<List<ImgUrl>>() {}.getType();
                List<ImgUrl> imageList = new Gson().fromJson(imgJsons[i], type);
                if (!imageList.isEmpty()){
                    allImgUrl.addAll(imageList);
                }
                //处理错误原因
                if ("D".equals(errorTypes[i])){
                    errorCause +=errorTypes[i]+":"+errorDescribe[i]+"\n";
                }
                else{
                    errorCause +=errorTypes[i]+":"+errorNames[i]+"\n";
                }
            }
            tv_error_cause.setText(errorCause);
            final ArrayList<ImgUrl> showImgs = new ArrayList<>();
            if (allImgUrl.size()>9){
                showImgs.addAll(allImgUrl.subList(0,9));
            }
            else{
                showImgs.addAll(allImgUrl);
            }

            CommonAdapter<ImgUrl> imgAdapter = new CommonAdapter<ImgUrl>(fragment.getActivity(), showImgs, R.layout.item_gv_image) {
                @Override
                public void convert(ViewHolder vh, ImgUrl item, int position) {
                    final int index = position;
                    ImageView ivDel = vh.getView(R.id.iv_del);
                    ivDel.setVisibility(View.GONE);
                    vh.setImageByUrl(R.id.iv, item.getUrl());
                }
            };
            mgv_error_img.setAdapter(imgAdapter);
            mgv_error_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<String> temp = StudyUtils.imgUrl2String(showImgs);
                    Intent intent = new Intent(fragment.getActivity(), ImagePagerActivity.class);
                    intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, temp);
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    fragment.getActivity().startActivity(intent);
                }
            });

        }catch (Exception e){
            Logger.e(Logger.TAG,"错误原因，错误图片数据异常");
            e.printStackTrace();
        }
    }

    /**
     *重做本题
     */
    public static void reform(Context ctx, List<String> imgList, TextView tv_submit, LinearLayout ll_bottom,
                              LinearLayout ll_answer, ImageView error_iv_1, ImageView error_iv_2,
                              ImageView error_iv_3, ImageView error_iv_clear_1, ImageView error_iv_clear_2,
                              ImageView error_iv_clear_3) {
        imgList.clear();
        tv_submit.setVisibility(View.VISIBLE);
        ll_bottom.setVisibility(View.GONE);
        ll_answer.setVisibility(View.GONE);
        error_iv_1.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
        error_iv_2.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
        error_iv_3.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
        error_iv_clear_1.setVisibility(View.GONE);
        error_iv_clear_2.setVisibility(View.GONE);
        error_iv_clear_3.setVisibility(View.GONE);
    }
}
