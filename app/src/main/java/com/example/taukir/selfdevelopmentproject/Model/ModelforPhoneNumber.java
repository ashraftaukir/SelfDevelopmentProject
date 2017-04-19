package com.example.taukir.selfdevelopmentproject.Model;

import java.io.Serializable;


public class ModelforPhoneNumber implements Serializable {


    private String mobile;
    private String home;
    private String office;

    public ModelforPhoneNumber() {
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

}
