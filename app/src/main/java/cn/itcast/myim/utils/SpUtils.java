package cn.itcast.myim.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cn.itcast.myim.IMApplication;

//SharedPreference工具类
//保存数据，获取数据
public class SpUtils {

    public static final String IS_NEW_INVITE = "is_new_invite";     //新邀请
    private static SpUtils instance = new SpUtils();
    private static SharedPreferences mSp;

    private SpUtils(){ }

    //单例模式
    public static SpUtils getInstance(){

        if (mSp == null){
            mSp = IMApplication.getGlobalApplication().getSharedPreferences("MyIM", Context.MODE_PRIVATE);
        }
        return instance;
    }

    // 保存
    public void save(String key, Object value){
        if (value instanceof String){
            mSp.edit().putString(key, (String) value).commit();
        }else if (value instanceof Integer){
            mSp.edit().putInt(key, (Integer) value).commit();
        }else if (value instanceof Boolean){
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        }
    }

    // 获取
    public String getString(String key, String defValue){
        return mSp.getString(key, defValue);
    }
    public int getInt(String key, int defValue){
        return mSp.getInt(key, defValue);
    }
    public Boolean getBoolean(String key, boolean defValue){
        return mSp.getBoolean(key, defValue);
    }
}
