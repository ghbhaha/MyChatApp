package com.suda.mychatapp.db.pojo;

import java.util.Date;

/**
 * Created by Suda on 2015/7/21.
 */
public class User {

    private String objId;
    private String userName;
    private String nikeName;
    private String sign;
    private String iconUrl;
    private String tel;
    private String email;
    private String sex;
    private Date birthday;


    public User(String objId, String userName, String nikeName, String sign, String iconUrl, String tel, String email, String sex, Date birthday) {
        this.objId = objId;
        this.userName = userName;
        this.nikeName = nikeName;
        this.sign = sign;
        this.iconUrl = iconUrl;
        this.tel = tel;
        this.email = email;
        this.sex = sex;
        this.birthday = birthday;
    }

    public String getObjId() {
        return objId;
    }

    public String getUserName() {
        return userName;
    }

    public String getNikeName() {
        return nikeName;
    }

    public String getSign() {
        return sign;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getTel() {
        return tel;
    }

    public String getEmail() {
        return email;
    }

    public String getSex() {
        return sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
