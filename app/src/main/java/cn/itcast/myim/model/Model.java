package cn.itcast.myim.model;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.itcast.myim.model.bean.UserInfo;
import cn.itcast.myim.model.dao.UserAccountDao;
import cn.itcast.myim.model.db.DBManager;

//数据模型层全局类
//所有数据的更新都直接调用这个类
public class Model {
    //创建对象
    private static Model model = new Model();

    private Context mContext;
    private ExecutorService executors = Executors.newCachedThreadPool();
    private UserAccountDao userAccountDao;
    private DBManager dbManager;

    //私有化构造
    private Model(){

    }
    //获取单例对象
    public static Model getInstance(){
        return model;
    }

    //初始化方法
    public void init(Context context){
        mContext = context;

        //创建用户账号数据库的操作类对象
        userAccountDao = new UserAccountDao(context);

        //开启全局监听
        EventListner eventListner = new EventListner(mContext);
    }

    // 获取全局线程池对象
    public ExecutorService getGlobalThreadPool(){
        return executors;
    }


    //获取用户账号数据库的操作类对象
    public UserAccountDao getUserAccountDao(){
        return userAccountDao;
    }

    //登录成功的处理
    public void loginSuccess(UserInfo account){
        if (account == null){
            return;
        }
        if (dbManager != null){
            dbManager.close();
        }
        dbManager = new DBManager(mContext, account.getName());
    }

    public DBManager getDbManager(){
        return dbManager;
    }

}
