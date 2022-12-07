package edu.njunet.utils.JsonReader.JavaBean;

public class LoginDataBean {
    private String User_name;
    private String Password;
    private String Last_login;

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String name) {
        User_name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getLast_login() {
        return Last_login;
    }

    public void setLast_login(String timeStamp) {
        Last_login = timeStamp;
    }
}
