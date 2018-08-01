package com.zbm.lovehealth;


public class ThemeMessageEvent {
    private boolean isNight;
    public ThemeMessageEvent(boolean isNight){
        this.isNight=isNight;
    }

    public boolean isNight() {
        return isNight;
    }

}
