package cn.itcast.myim;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;

import cn.itcast.myim.model.Model;

public class IMApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化EaseUI
        EMOptions options=new EMOptions();
        options.setAcceptInvitationAlways(false);       // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAutoAcceptGroupInvitation(false);    //设置需要同意后才能接受群邀请(不管用了？)
        //options.isAutoAcceptGroupInvitation(false);
        EaseUI.getInstance().init(this,options);

        //初始化数据模型层类
        Model.getInstance().init(this);

        //初始化全局上下文
        mContext = this;
    }

    //获取全局上下文对象
    public static Context getGlobalApplication(){
        return mContext;
    }
}