package cn.itcast.myim.model;

import android.content.Context;
import android.content.Intent;
import android.media.midi.MidiOutputPort;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;

import java.util.List;

import cn.itcast.myim.model.bean.GroupInfo;
import cn.itcast.myim.model.bean.InvationInfo;
import cn.itcast.myim.model.bean.UserInfo;
import cn.itcast.myim.utils.Constant;
import cn.itcast.myim.utils.SpUtils;

//全局事件监听
public class EventListner {



    private Context mContext;
    private final LocalBroadcastManager mLBM;

    public EventListner(Context context) {
        mContext = context;
        //创建一个发送广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(mContext);

        //联系人变化监听
        EMClient.getInstance().contactManager().setContactListener(emContactLisner);

        //群信息变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupListener);
    }
    //注册一个联系人变化的监听
    private final EMContactListener emContactLisner = new EMContactListener() {

        //联系人增加后
        @Override
        public void onContactAdded(String hxid) {
            // 【数据库更新】
            Model.getInstance().getDbManager().getContactTableDao().saveContact(new UserInfo(hxid),true);
            //【发送联系人变化的广播】
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));

        }

        //联系人删除后
        @Override
        public void onContactDeleted(String hxid) {
            Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(hxid);
            Model.getInstance().getDbManager().getInviteTableDao().removeInvation(hxid);
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        //接收到联系人新邀请
        @Override
        public void onContactInvited(String hxid, String reason) {
            // 数据库更新
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setUser(new UserInfo(hxid));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvationInfo.InvitationStatus.NEW_INVITE);// 新邀请

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
            //Log.i("invite", "接收到新邀请");
            // 红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            // 发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        // 别人同意了你的好友邀请
        @Override
        public void onFriendRequestAccepted(String hxid) {
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setUser(new UserInfo(hxid));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);      //邀请被接受
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        // 别人拒绝了你的好友邀请
        @Override
        public void onFriendRequestDeclined(String hxid) {
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };

    //注册一个群信息变化的监听
    private final EMGroupChangeListener emGroupListener = new EMGroupChangeListener() {

        //收到 群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            //数据更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupName,groupId,inviter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_INVITE); //新的群邀请
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请通知
        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {

            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupName,groupId,applicant));
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION);    //新的群申请
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被接受
        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setGroup(new GroupInfo(groupName,groupId,accepter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);    //群申请被接受
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被拒绝
        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {

            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupName,groupId,decliner));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);    //群申请被拒绝
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被同意
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {

            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);    //群邀请被同意
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason) {

            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED);    //群邀请被拒绝
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群成员被删除
        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        //收到 群被解散
        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        //收到 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {

            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(inviteMessage);
            invationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_INVITE);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onMuteListAdded(String s, List<String> list, long l) {

        }

        @Override
        public void onMuteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onWhiteListAdded(String s, List<String> list) {

        }

        @Override
        public void onWhiteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAllMemberMuteStateChanged(String s, boolean b) {

        }

        @Override
        public void onAdminAdded(String s, String s1) {

        }

        @Override
        public void onAdminRemoved(String s, String s1) {

        }

        @Override
        public void onOwnerChanged(String s, String s1, String s2) {

        }

        @Override
        public void onMemberJoined(String s, String s1) {

        }

        @Override
        public void onMemberExited(String s, String s1) {

        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

        @Override
        public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

        }

        @Override
        public void onSharedFileDeleted(String s, String s1) {

        }
    };

}
