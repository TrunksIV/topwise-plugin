package com.topwise.plugin.emv;

import com.topwise.plugin.BaseJson;
import com.topwise.plugin.PluginUtils;

import java.util.HashMap;
import java.util.Map;

public class EmvTransData extends BaseJson {
    private int cardType = CardType.NONE;
    private byte transType = EmvDefinition.EMV_TRANS_TYPE_GOODS;
    private boolean isForceOnline = false;
    private long amount = 0;
    private long otherAmount = 0;

    public EmvTransData(){

    }

    @Override
    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("amount",amount);
        json.put("transType",transType);
        json.put("isForceOnline",isForceOnline);
        json.put("otherAmount",otherAmount);
        json.put("cardType",cardType);
        return json;
    }

    @Override
    public void fromJson(Map<String, Object> json) {
        if(json==null)
            return;
        if(json.containsKey("amount")){
            setAmount(PluginUtils.getLong(json.get("amount")));
        }
        if(json.containsKey("otherAmount")){
            setOtherAmount(PluginUtils.getLong(json.get("otherAmount")));
        }
        if(json.containsKey("transType")){
            setTransType(PluginUtils.getByte(json.get("transType")));
        }
        if(json.containsKey("cardType")){
            setCardType((int)json.get("cardType"));
        }
        if(json.containsKey("isForceOnline")){
            setForceOnline((boolean)json.get("isForceOnline"));
        }
    }

    public EmvTransData(int cardType, byte transType, long amount){
        setCardType(cardType);
        setTransType(transType);
        setAmount(amount);
    }

    public EmvTransData(int cardType, byte transType, long amount, boolean isForceOnline){
        setCardType(cardType);
        setTransType(transType);
        setAmount(amount);
        setForceOnline(isForceOnline);
    }

    public EmvTransData(int cardType, byte transType, long amount, boolean isForceOnline, long otherAmount){
        setCardType(cardType);
        setTransType(transType);
        setAmount(amount);
        setForceOnline(isForceOnline);
        setOtherAmount(otherAmount);
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public byte getTransType() {
        return transType;
    }

    public void setTransType(byte transType) {
        this.transType = transType;
    }

    public boolean isForceOnline() {
        return isForceOnline;
    }

    public void setForceOnline(boolean forceOnline) {
        isForceOnline = forceOnline;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        if(amount>=0)
            this.amount = amount;
    }

    public long getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(long otherAmount) {
        if(otherAmount>=0)
            this.otherAmount = otherAmount;
    }

}
