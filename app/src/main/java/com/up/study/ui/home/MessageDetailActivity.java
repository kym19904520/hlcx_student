package com.up.study.ui.home;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.up.common.J;
import com.up.common.utils.Logger;
import com.up.common.utils.SPUtil;
import com.up.common.utils.StudyUtils;
import com.up.study.R;
import com.up.study.adapter.MesGridViewAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.ImgUrl;
import com.up.study.model.MesModel;
import com.up.study.weight.MyGridView;
import com.up.study.weight.showimages.ImagePagerActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息详情
 */
public class MessageDetailActivity extends BaseFragmentActivity {
    ImageView ivImage;
    //TextView time;
    TextView content;
    TextView tv_major;
    TextView tv_name;
    ImageView iv_head;
    MyGridView gvImages;

    MesModel mesModel;

    List<ImgUrl> imgs;
    @Override
    protected int getContentViewId() {
        return R.layout.act_message_detail;
    }

    @Override
    protected void initView() {

        ivImage=(ImageView)this.findViewById(R.id.iv_my_home_img);
        iv_head=(ImageView)this.findViewById(R.id.iv_head);
        //vh.time=(TextView)convertView.findViewById(R.id.tv_time);
        content=(TextView)this.findViewById(R.id.tv_content);
        tv_major=(TextView)this.findViewById(R.id.tv_major);
        tv_name=(TextView)this.findViewById(R.id.tv_name);
        gvImages=(MyGridView)this.findViewById(R.id.gv_my_home_imgs);
    }

    @Override
    protected void initEvent() {
        ivImage.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mesModel = (MesModel)getIntent().getSerializableExtra("bean1");
        SPUtil.saveTaskMesId(this,mesModel.getId()+"",2);
        try {
            String imgUrls =  mesModel.getAttached();
            Type type=new TypeToken<List<ImgUrl>>(){}.getType();
            imgs = new Gson().fromJson(imgUrls,type);
        }catch (Exception e){
            Logger.e(Logger.TAG,"图片格式异常");
        }
        //List<Bitmap> imgs = dynamicLists.get(position).getAttached();
        if(imgs!=null&&imgs.size()>1){
            ivImage.setVisibility(View.GONE);
            gvImages.setVisibility(View.VISIBLE);
            gvImages.setAdapter(new MesGridViewAdapter(imgs, this));
        }
        else if(imgs!=null&&imgs.size()==1){
            ivImage.setVisibility(View.VISIBLE);
            gvImages.setVisibility(View.GONE);
            //vh.ivImage.setImageBitmap(imgs.get(0));
            J.image().load(this,imgs.get(0).getUrl(),ivImage);
        }
        else{
            gvImages.setVisibility(View.GONE);
            ivImage.setVisibility(View.GONE);
        }
        //vh.time.setText(dynamicLists.get(position).getTime().toString());
        content.setText(mesModel.getContent().toString());
        tv_major.setText(mesModel.getMajor());
        tv_name.setText(mesModel.getName());
        J.image().loadCircle(this,mesModel.getHead(),iv_head);

        gvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> temp = new ArrayList<>();
                for(int i = 0;i<imgs.size();i++){
                    temp.add(imgs.get(i).getUrl());
                }
                Intent intent = new Intent(MessageDetailActivity.this, ImagePagerActivity.class);
                intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, temp);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v==ivImage){
            ArrayList<String> temp = new ArrayList<>();
            for(int i = 0;i<imgs.size();i++){
                temp.add(imgs.get(i).getUrl());
            }
            Intent intent = new Intent(this, ImagePagerActivity.class);
            intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, temp);
            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
            startActivity(intent);
        }
    }

}
