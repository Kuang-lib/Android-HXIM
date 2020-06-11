package cn.itcast.myim.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.myim.model.bean.GroupInfo;
import cn.itcast.myim.model.bean.InvationInfo;
import cn.itcast.myim.model.bean.UserInfo;
import cn.itcast.myim.model.db.DBHelper;

public class InviteTableDao {

    private DBHelper mHelper;
    private SQLiteDatabase db;

    public InviteTableDao(DBHelper helper){
        mHelper = helper;

    }

    // 添加邀请
    public void addInvitation(InvationInfo invationInfo) {
        //获取数据库连接
        db = mHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_REASON,invationInfo.getReason());
        values.put(InviteTable.COL_STATUS,invationInfo.getStatus().ordinal());  //ordinal枚举的序号

        UserInfo user = invationInfo.getUser();
        if (user != null){  //联系人
            values.put(InviteTable.COL_USER_HXID,invationInfo.getUser().getHxid());
            values.put(InviteTable.COL_USER_NAME,invationInfo.getUser().getName());
        }else { //群组
            values.put(InviteTable.COL_GROUP_HXID,invationInfo.getGroup().getGroupId());
            values.put(InviteTable.COL_GROUP_NAME,invationInfo.getGroup().getGroupName());
            values.put(InviteTable.COL_USER_HXID,invationInfo.getGroup().getInvatePerson());
        }

        db.replace(InviteTable.TAB_NAME,null,values);
    }

    //获取所有邀请信息
    public List<InvationInfo> getInvations(){
        //获取数据库连接
        db = mHelper.getReadableDatabase();
        List<InvationInfo> invationInfos = new ArrayList<InvationInfo>();

        String sql = "select * from " + InviteTable.TAB_NAME;
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()){
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(cursor.getString(cursor.getColumnIndex(InviteTable.COL_REASON)));
            invationInfo.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(InviteTable.COL_STATUS))));

            //通过查找是否有群组id来判断是联系人还是群组的邀请信息
            String groupId = cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID));
            if (groupId == null){   //联系人
                UserInfo userInfo = new UserInfo();

                userInfo.setHxid(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                userInfo.setNickName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                invationInfo.setUser(userInfo);
            }else{  //群组
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupId(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_NAME)));
                groupInfo.setInvatePerson(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
                invationInfo.setGroup(groupInfo);

            }
            invationInfos.add(invationInfo);
        }
        cursor.close();
        return  invationInfos;
    }

    // 将int类型状态转换为邀请的状态
    private InvationInfo.InvitationStatus int2InviteStatus(int intStatus) {

        if (intStatus == InvationInfo.InvitationStatus.NEW_INVITE.ordinal()) {
            return InvationInfo.InvitationStatus.NEW_INVITE;
        }

        if (intStatus == InvationInfo.InvitationStatus.INVITE_ACCEPT.ordinal()) {
            return InvationInfo.InvitationStatus.INVITE_ACCEPT;
        }

        if (intStatus == InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }

        if (intStatus == InvationInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InvationInfo.InvitationStatus.NEW_GROUP_INVITE;
        }

        if (intStatus == InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }

        return null;
    }

    //删除邀请
    public void removeInvation(String hxid){
        //获取数据库连接
        db = mHelper.getReadableDatabase();
        if (hxid == null){
            return;
        }

        db.delete(InviteTable.TAB_NAME,InviteTable.COL_USER_HXID+"=?",new String[]{hxid});
    }

    //更新邀请状态
    public void updateInvitationStatus(InvationInfo.InvitationStatus invitationStatus,String hxid){
        //获取数据库连接
        db = mHelper.getReadableDatabase();
        if (hxid == null){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_STATUS, invitationStatus.ordinal());
        db.update(InviteTable.TAB_NAME, values, InviteTable.COL_USER_HXID+"=?", new String[]{hxid});
    }
}
