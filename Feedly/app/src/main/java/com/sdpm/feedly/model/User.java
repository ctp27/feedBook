package com.sdpm.feedly.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clinton on 12/2/17.
 */

public class User implements Serializable{
    private String full_name;
    private String email_id;
    private String password;
    private List<Object> preferences;


    public User(String full_name, String email_id, String password,List<Object> preferences) {
        this.full_name = full_name;
        this.email_id = email_id;
        this.password = password;
        this.preferences = preferences;
    }

    public User(){}

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Object> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<Object> preferences) {
        this.preferences = preferences;
    }
}
