package com.up.study.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.up.study.TApplication;
import com.xyzlf.share.library.interfaces.ShareConstant;

/**
 * TODO:
 * Created by 王剑洪
 * on 2016/11/24 0024.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private String url = "https://api.weixin.qq.com/sns/oauth2/access_token?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, TApplication.getInstant().getWxAppId(), false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Intent intent = new Intent();
        intent.setAction(ShareConstant.ACTION_WEIXIN_CALLBACK);
        intent.putExtra(ShareConstant.EXTRA_WEIXIN_RESULT, baseResp.errCode);
        sendBroadcast(intent);
        finish();
//        String result = "";
//        switch (baseResp.errCode) {
//            case BaseResp.ErrCode.ERR_OK:
//                result = "发送成功";
//                /*获取token*/
//                SendAuth.Resp sendResp = (SendAuth.Resp) baseResp;
//                String code = sendResp.code;
//                Log.d("Tag", "code\t" + code);
//                finish();
//                getToken(code,Wx.APP_ID,Wx.APP_SECRET);
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                result = "发送取消";
//                break;
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                result = "发送被拒绝";
//                break;
//            default:
//                result = "发送返回";
//                finish();
//                break;
//        }
    }

//    private void getToken(String code, String appId, String secret) {
//        String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appId+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";
//        OkGo.get(url).execute(new StringCallback() {
//            @Override
//            public void onSuccess(String s, Call call, Response response) {
//                Type type=new TypeToken<WxLogin>(){}.getType();
//                WxLogin wxLogin=new Gson().fromJson(s,type);
//                final String openId=wxLogin.getOpenid();
//                OkGo.get("https://api.weixin.qq.com/sns/userinfo?access_token="+wxLogin.getAccess_token()+"&openid="+wxLogin.getOpenid()).execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(String s, Call call, Response response) {
//                        Type type=new TypeToken<WxUser>(){}.getType();
//                        WxUser user=new Gson().fromJson(s,type);
//                        HttpParams params=new HttpParams();
//                        params.put("openId",openId);
//                        params.put("form",2);
//                        params.put("nickname",user.getNickname());
//                        params.put("face",user.getHeadimgurl());
//                        J.http().post(HttpUrl.URL_LOGIN_WX, HttpUrl.ID_LOGIN_WX, params, this, new ResCallback() {
//                            @Override
//                            public void onStart(int id) {
//
//                            }
//
//                            @Override
//                            public void OK(int id, boolean isCache, String t) {
//                                Type type=new TypeToken<BaseRes<Member>>(){}.getType();
//                                BaseRes<Member> res=new Gson().fromJson(t,type);
//                                Member member=res.getData();
//                                if (TextUtils.isEmpty(member.getPhone())){//手机号为空
//                                    Map<String,Object> map=new HashMap<String, Object>();
//                                    map.put("member",member);
//                                    Intent intent = new Intent();
//                                    intent.setClass(WXEntryActivity.this, BindPhoneActivity.class);
//                                    SerializableMap serializableMap = new SerializableMap();
//                                    serializableMap.setMap(map);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putSerializable("Map", serializableMap);
//                                    intent.putExtras(bundle);
//                                    startActivity(intent);
//                                    finish();
//                                }else {
//                                    Intent intent = new Intent();  //Intent就是我们要发送的内容
//                                    intent.setAction(LoginActivity.BROAD_ACTION);   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
//                                    sendBroadcast(intent);   //发送广播
//                                    member.save();
//                                    Intent intent2 = new Intent();  //Intent就是我们要发送的内容
//                                    intent2.putExtra(MainActivity.KEY_INDEX, 3);
//                                    intent2.setAction(MainActivity.BROAD_ACTION);   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
//                                    sendBroadcast(intent2);   //发送广播
//                                    finish();
//                                }
//                            }
//
//                            @Override
//                            public void onFinish(int id) {
//
//                            }
//                        });
//                    }
//                });
//
//
//            }
//        });
//    }
}
