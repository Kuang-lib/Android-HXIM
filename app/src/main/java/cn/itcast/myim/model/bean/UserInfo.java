package cn.itcast.myim.model.bean;

//用户账号信息的bean类
public class UserInfo {
    private String name;    //用户名称
    private String hxid;    //环信id
    private String nick_mame;    //昵称
    private String photo;   //头像

    public UserInfo() {
    }

    public UserInfo(String name, String hxid, String nickName) {
        this.name = name;
        this.hxid = hxid;
        this.nick_mame = nickName;
    }
    public UserInfo(String name) {
        this.name = name;
        this.hxid = name;
        this.nick_mame = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHxid() {
        return hxid;
    }

    public void setHxid(String hxid) {
        this.hxid = hxid;
    }

    public String getNickName() {
        return nick_mame;
    }

    public void setNickName(String nickName) {
        this.nick_mame = nickName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", hxid='" + hxid + '\'' +
                ", nickName='" + nick_mame + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
