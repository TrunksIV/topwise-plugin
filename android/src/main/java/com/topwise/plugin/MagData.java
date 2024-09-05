package com.topwise.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author caixh
 * @description
 * @date 2023/2/1 9:29
 */
public class MagData extends BaseJson{
    private String track1 = ""; // 一磁道数据
    private String track2 = ""; // 二磁道数据
    private String track3 = ""; // 三磁道数据
    private String cardno = ""; // 卡号
    private String formatTrack = ""; // 二三磁格式化数据
    private String expiryDate = ""; // 卡片有效期
    private String serviceCode = ""; // 服务码

    public MagData(){

    }

    @Override
    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("track1",track1);
        json.put("track2",track2);
        json.put("track3",track3);
        json.put("cardno",cardno);
        json.put("formatTrack",formatTrack);
        json.put("expiryDate",expiryDate);
        json.put("serviceCode",serviceCode);
        return json;
    }

    @Override
    public void fromJson(Map<String, Object> json) {
        if(json==null)
            return;
        if(json.containsKey("track1")){
            setTrack1((String)json.get("track1"));
        }
        if(json.containsKey("track2")){
            setTrack2((String)json.get("track2"));
        }
        if(json.containsKey("track3")){
            setTrack3((String)json.get("track3"));
        }
        if(json.containsKey("cardno")){
            setCardno((String)json.get("cardno"));
        }
        if(json.containsKey("formatTrack")){
            setFormatTrack((String)json.get("formatTrack"));
        }
        if(json.containsKey("expiryDate")){
            setExpiryDate((String)json.get("expiryDate"));
        }
        if(json.containsKey("serviceCode")){
            setServiceCode((String)json.get("serviceCode"));
        }
    }

    public MagData(String cardno,
                     String track1, String track2,
                     String track3, String formatTrack, String expiryDate,
                     String serviceCode) {
        this.cardno = cardno;
        this.track1 = track1;
        this.track2 = track2;
        this.track3 = track3;
        this.expiryDate = expiryDate;
        this.formatTrack = formatTrack;
        this.serviceCode = serviceCode;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
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

    public void setTrack3(String thirdTrackData) {
        this.track3 = track3;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getFormatTrack() {
        return formatTrack;
    }

    public void setFormatTrack(String formatTrack) {
        this.formatTrack = formatTrack;
    }

}
