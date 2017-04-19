package com.example.taukir.selfdevelopmentproject.Model;


import java.io.Serializable;

public class MyDataModel implements Serializable {

    private String title;
    private String poster_path;
    private String overview;
    private boolean isSelected;

    public MyDataModel() {
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }


    public String getEmail() {
        return overview;
    }

    public void setEmail(String email) {
        this.overview = email;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

//    public static class Phone {
//        private String mobile;
//
//
//        public String getMobile() {
//            return mobile;
//        }
//
//        public void setMobile(String mobile) {
//            this.mobile = mobile;
//        }
//    }
}
