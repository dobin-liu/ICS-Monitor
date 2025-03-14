package com.bi.common.model;

import java.sql.Timestamp;
import java.util.Set;

import com.bi.base.model.BaseTableObject;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TblSysApiLogs extends BaseTableObject {

    private static final String TABLE_NAME = "tblSysApiLogs";
    
    @JsonProperty(value = "uuid")
    private String uuid; // 資料流水號(UUID碼)
    
    @JsonProperty(value = "custName")
    private String cust_name; // 客戶姓名
    
    @JsonProperty(value = "custBirthday2")
    private Timestamp cust_birthday; // 客戶生日
    
    @JsonProperty(value = "custSex")
    private String cust_sex; // 性別
    
    @JsonProperty(value = "custJobPa")
    private String cust_jobpa; // 職業等級
    
    @JsonProperty(value = "companyName")
    private String companyname; // 保險公司
    
    @JsonProperty(value = "mark")
    private String mark; // 單張/多張相片
    
    @JsonProperty(value = "userName")
    private String user_name; // 使用者姓名
    
    @JsonProperty(value = "userId")
    private String user_id; // 使用者ID
    
    @JsonProperty(value = "source")
    private String source; // 新光通路
    
    @JsonProperty(value = "creDt2")
    private Timestamp cre_dt; // 建立日期

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public Timestamp getCust_birthday() {
        return cust_birthday;
    }

    public void setCust_birthday(Timestamp cust_birthday) {
        this.cust_birthday = cust_birthday;
    }

    public String getCust_sex() {
        return cust_sex;
    }

    public void setCust_sex(String cust_sex) {
        this.cust_sex = cust_sex;
    }

    public String getCust_jobpa() {
        return cust_jobpa;
    }

    public void setCust_jobpa(String cust_jobpa) {
        this.cust_jobpa = cust_jobpa;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Timestamp getCre_dt() {
        return cre_dt;
    }

    public void setCre_dt(Timestamp cre_dt) {
        this.cre_dt = cre_dt;
    }

    @Override
    public Set<String> getKeyNames() {
        return null;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

}
