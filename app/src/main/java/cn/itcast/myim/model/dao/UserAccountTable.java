package cn.itcast.myim.model.dao;

//用户账号基本信息的建表语句
public class UserAccountTable {

    public static final String TAB_NAME = "tab_account";
    public static final String COL_NAME = "name";
    public static final String COL_HXID = "hxid";
    public static final String COL_NICK = "nick_name";
    public static final String COL_PHOTO = "photo";

    //建表语句：注意空格
    public static final String CREATRE_TAB = "create table " + TAB_NAME  + " ("
            + COL_HXID + " text primary key,"
            + COL_NAME + " text,"
            + COL_NICK + " text,"
            + COL_PHOTO + " text);";

}
