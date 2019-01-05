package com.twtech.fleetviewapp;

import android.app.Application;

/**
 * Created by twtech on 3/2/18.
 */

public class GlobalVariable extends Application {

    private String StringGPRMC;
    private String PrevGPRMC;
    private String PrevAcDcGPRMC;
    private String winners;
    private String myScore;


    public String getOSGprmcString() {
        return OSGprmcString;
    }

    public void setOSGprmcString(String osGprmcString) {
        OSGprmcString = osGprmcString;
    }

    private String OSGprmcString;

    public String getPrevAcDcGPRMC() {
        return PrevAcDcGPRMC;
    }

    public void setPrevAcDcGPRMC(String prevAcDcGPRMC) {
        PrevAcDcGPRMC = prevAcDcGPRMC;
    }

    public String getPrevGPRMC() {
        return PrevGPRMC;
    }

    public void setPrevGPRMC(String prevGPRMC) {
        PrevGPRMC = prevGPRMC;
    }
    public String getStringGPRMC() {
        return StringGPRMC;
    }

    public void setStringGPRMC(String stringGPRMC) {
        StringGPRMC = stringGPRMC;
    }

    public String getWinners() {
        return winners;
    }

    public void setWinners(String winners) {
        this.winners = winners;
    }
}
