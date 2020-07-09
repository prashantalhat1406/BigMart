package com.asm.bigmart;

public class User {
    String ID;
    Long Mobile;
    String Name;
    Integer Password;
    String Email;
    String Address1;
    String Address2;
    Integer securityQ;
    String Answer;
    String Access;

    public User() {
    }

    public User(String ID, Long mobile, String name, Integer password, String email, String address1, String address2) {
        this.ID = ID;
        Mobile = mobile;
        Name = name;
        Password = password;
        Email = email;
        Address1 = address1;
        Address2 = address2;
    }

    public User(Long mobile, String name, Integer password, String address1) {
        Mobile = mobile;
        Name = name;
        Password = password;
        Address1 = address1;
    }

    public String getAccess() {
        return Access;
    }

    public void setAccess(String access) {
        Access = access;
    }

    public Integer getSecurityQ() {
        return securityQ;
    }

    public void setSecurityQ(Integer securityQ) {
        this.securityQ = securityQ;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        this.Answer = answer;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Long getMobile() {
        return Mobile;
    }

    public void setMobile(Long mobile) {
        Mobile = mobile;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getPassword() {
        return Password;
    }

    public void setPassword(Integer password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String address2) {
        Address2 = address2;
    }
}
