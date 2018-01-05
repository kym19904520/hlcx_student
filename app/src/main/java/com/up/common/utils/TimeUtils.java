package com.up.common.utils;

import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by dell on 2017/4/10.
 * button上的60秒倒计时
 */

public class TimeUtils {
    private int time = 120;

    private Timer timer;

    private TextView btnSure;

    private String btnText;

    //
    public TimeUtils(TextView btnSure, String btnText) {
        super();
        this.btnSure = btnSure;
        this.btnText = btnText;
    }


    public void RunTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                time--;
                Message msg = handler.obtainMessage();
                msg.what = 1;
                handler.sendMessage(msg);

            }
        };


        timer.schedule(task, 100, 1000);
    }


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:

                    if (time > 0) {
                        btnSure.setEnabled(false);
                        btnSure.setText(time + "秒后重新发送");
                        btnSure.setTextSize(14);
                    } else {
                        timer.cancel();
                        btnSure.setText(btnText);
                        btnSure.setEnabled(true);
                        btnSure.setTextSize(14);
                    }

                    break;


                default:
                    break;
            }

        }

        ;
    };


}
