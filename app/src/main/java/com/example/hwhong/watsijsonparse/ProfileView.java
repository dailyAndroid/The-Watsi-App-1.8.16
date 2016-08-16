package com.example.hwhong.watsijsonparse;

/**
 * Created by hwhong on 8/1/16.
 */
public class ProfileView {

    private String name;
    private String partner_name;
    private String header;
    private int amount_raised;
    private int target_amount;

    private String photo_url;

    private String launch_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartner_name() {
        return partner_name;
    }

    public void setPartner_name(String partner_name) {
        this.partner_name = partner_name;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getAmount_raised() {
        return amount_raised;
    }

    public void setAmount_raised(int amount_needed) {
        this.amount_raised = amount_needed;
    }

    public int getTarget_amount() {
        return target_amount;
    }

    public void setTarget_amount(int target_amount) {
        this.target_amount = target_amount;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getLaunch_url() {
        return launch_url;
    }

    public void setLaunch_url(String launch_url) {
        this.launch_url = launch_url;
    }
}
