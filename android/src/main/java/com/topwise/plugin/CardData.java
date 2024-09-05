package com.topwise.plugin;

/**
 * @author caixh
 * @description
 * @date 2023/3/13 10:56
 */
public class CardData {
    /**
     *
     */
    private EReturnType eReturnType;
    /**
     *
     */
    private ECardType eCardType;
    /**
     *
     */
    private String track1;
    /**
     *
     */
    private String track2;
    /**
     *
     */
    private String track3;

    public CardData(EReturnType eReturnType) {
        this.eReturnType = eReturnType;
    }

    public CardData(EReturnType eReturnType, ECardType eCardType) {
        this.eReturnType = eReturnType;
        this.eCardType = eCardType;
    }

    public CardData(EReturnType eReturnType, ECardType eCardType, String track1, String track2, String track3) {
        this.eReturnType = eReturnType;
        this.eCardType = eCardType;
        this.track1 = track1;
        this.track2 = track2;
        this.track3 = track3;
    }

    /**
     *
     */
    public enum EReturnType {
        OK(0,"SUCCESS"),
        TIMEOUT(-1,"TIMEOUT"),
        CANCEL(-2,"CANCEL"),
        OPEN_MAG_ERR(-3,"OPEN_MAG_ERR"),
        OPEN_IC_ERR(-4,"OPEN_IC_ERR"),
        OPEN_RF_ERR(-5,"OPEN_RF_ERR"),
        OPEN_MAG_RESET_ERR(-6,"OPEN_MAG_RESET_ERR"),
        OPEN_IC_RESET_ERR(-7,"OPEN_IC_RESET_ERR"),
        OPEN_RF_RESET_ERR(-8,"OPEN_RF_RESET_ERR"),
        RF_MULTI_CARD(-9,"CLS_MULTI_CARD"),
        OTHER_ERR(-10,"OTHER_ERR");

        private int code;
        private String msg;

        EReturnType(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "EReturnType{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }
    public enum ECardType {
        IC, RF, MAG;
    }

    public EReturnType geteReturnType() {
        return eReturnType;
    }

    public void seteReturnType(EReturnType eReturnType) {
        this.eReturnType = eReturnType;
    }

    public ECardType geteCardType() {
        return eCardType;
    }

    public void seteCardType(ECardType eCardType) {
        this.eCardType = eCardType;
    }

    public String getTrack1() {
        return track1;
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getTrack3() {
        return track3;
    }

    public void setTrack3(String track3) {
        this.track3 = track3;
    }

    @Override
    public String toString() {
        return "CardData{" +
                "eReturnType=" + eReturnType.toString() +
                ", eCardType=" + eCardType +
                ", track1='" + track1 + '\'' +
                ", track2='" + track2 + '\'' +
                ", track3='" + track3 + '\'' +
                '}';
    }
}
