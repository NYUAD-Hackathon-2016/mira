package com.itech.miraclient;

import java.util.Date;

/**
 * Created by oSunshine on 16/04/2016.
 */
public class Medecine {


    public Medecine() {
    }

    public Medecine(String name, Date pillTime, int dose, boolean tokken) {
        this.name = name;
        this.pillTime = pillTime;
        this.dose = dose;
        this.tokken = tokken;
    }


    String name ;
    Date pillTime ;
    int dose ;
    boolean tokken ;


    public boolean isTokken() {
        return tokken;
    }

    public void setTokken(boolean tokken) {
        this.tokken = tokken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getPillTime() {
        return pillTime;
    }

    public void setPillTime(Date pillTime) {
        this.pillTime = pillTime;
    }

    public int getDose() {
        return dose;
    }

    public void setDose(int dose) {
        this.dose = dose;
    }

}
