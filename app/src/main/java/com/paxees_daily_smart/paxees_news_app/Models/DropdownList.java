package com.paxees_daily_smart.paxees_news_app.Models;

public class DropdownList {

    public int id = 0;
    public String name = "";
    public String code = "";

    public DropdownList() {
    }

    public DropdownList(int _id, String _name, String _code) {
        id = _id;
        name = _name;
        code = _code;
    }

    public String toString() {
        return (name); //return( name + " (" + code + ")" );
    }
}
