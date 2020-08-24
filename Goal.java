package com.example.justsavings.MAIN_ACTIVITY;

public class Goal {
    public String name;
    public int id;
    public String required_money;
    public int url;
    public String amount_collected;
    public String currency_sign;


    public Goal(int id , String name, String required_money, int url,String amount_collected,String currency_sign) {
        this.name = name;
        this.required_money = required_money;
        this.url = url;
        this.currency_sign=currency_sign;
        this.amount_collected=amount_collected;
        this.id = id;
    }

}