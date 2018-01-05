package com.up.study;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.up.common.conf.Constants;
import com.up.common.utils.Logger;
import com.up.common.utils.SPUtil;

import org.junit.Test;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        //assertEquals(4, 2 + 2);

        String str = "[\"<img  src=\"doc-dir/pic20170823164611524828409.jpg\"/>\"][\"<img  src=\"doc-dir/pic20170823164611524828409.jpg\"/>\"]";
        if(!str.toString().contains("http")&&str.toString().contains("doc-dir")){
           str = str.replace("src=\"","src=\""+"http://loclhost/");
        }

        System.out.println(str);
        if (true){
            return;
        }
        Date date=new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,-1);//把日期往后增加一天.整数往后推,负数往前移动
        date=calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        System.out.println(dateString);

        String value = "2";
        String idsStr = "";
        String [] ids = idsStr.split("\\@");
        for (int i = 0;i<ids.length;i++){
            if (ids[i].equals(value)){
                return;
            }
        }

        if(idsStr.length()==0){
            idsStr=value;
        }
        else{
            idsStr+="@"+value;
        }
        System.out.println("ids="+idsStr);
        for (int i = 0;i<ids.length;i++){
            System.out.println("id="+ids[i]);
        }

       String answer="[\"1\"]";
        answer = answer.replace("0","A");
        answer = answer.replace("1","B");
        System.out.println("answer="+answer);

       System.out.println("===========");
        List<T> imgList = new ArrayList<>();
        T t = new T();
        t.setUrl("12312");
        imgList.add(t);
       /* imgList.add("img1");
        imgList.add("img2");
        imgList.add("img3");*/
        String s = new Gson().toJson(imgList);
        System.out.println("==========s:"+s);

        String aaa1 = "[\"1.172÷43=42.128÷32=43.392÷67=5……574.507÷79=6……33<img  src=\"http://hlcximg.oss-cn-hangzhou.aliyuncs.com/doc-dir/pic20170724150227482083481.jpg\"/><img  src=\"http://hlcximg.oss-cn-hangzhou.aliyuncs.com/doc-dir/pic20170724150227528856165.jpg\"/><img  src=\"http://hlcximg.oss-cn-hangzhou.aliyuncs.com/doc-dir/pic20170724150227591988715.jpg\"/><img  src=\"http://hlcximg.oss-cn-hangzhou.aliyuncs.com/doc-dir/pic20170724150227622120636.jpg\"/>\"]";
        String aaa2 = "[\"60\"]";

        JsonReader jsonReader = new JsonReader(new StringReader(aaa2));//其中jsonContext为String类型的Json数据
        jsonReader.setLenient(true);
        List<String> list = new Gson().fromJson(jsonReader, List.class);

       // Type type = new TypeToken<List<String>>() {}.getType();
        //List<String> list = new Gson().fromJson(aaa1, type);
        System.out.println("list.size="+list.size());
        String bb = aaa1.substring(2,aaa1.length()-2);
        System.out.println("bb="+bb);
    }
    class T{
        String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}