package com.topwise.plugin;

import com.topwise.cloudpos.aidl.printer.Align;
import com.topwise.cloudpos.aidl.printer.TextUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author caixh
 * @description
 * @date 2023/4/13 16:14
 */
public class TextUnitJson extends BaseJson{

    private String text = "";
    private int fontSize = 24;
    private boolean isBold = false;
    private int align = Align.LEFT.getValue();
    private boolean isUnderline = false;
    private boolean isWordWrap = true;
    private int lineSpacing = 0;
    private int letterSpacing = 0;
    private double scaleX = 1.0;
    private double scaleY = 1.0;

    public TextUnitJson(){

    }

    @Override
    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("text",text);
        json.put("fontSize",fontSize);
        json.put("isBold",isBold);
        json.put("align",align);
        json.put("isUnderline",isUnderline);
        json.put("isWordWrap",isWordWrap);
        json.put("lineSpacing",lineSpacing);
        json.put("letterSpacing",letterSpacing);
        json.put("scaleX",scaleX);
        json.put("scaleY",scaleY);
        return json;
    }

    @Override
    public void fromJson(Map<String, Object> json) {
        if(json==null)
            return;
        if(json.containsKey("text")){
            setText((String)json.get("text"));
        }
        if(json.containsKey("fontSize")){
            setFontSize((int)json.get("fontSize"));
        }
        if(json.containsKey("isBold")){
            setBold((boolean)json.get("isBold"));
        }
        if(json.containsKey("align")){
            setAlign((int)json.get("align"));
        }
        if(json.containsKey("isUnderline")){
            setUnderline((boolean)json.get("isUnderline"));
        }
        if(json.containsKey("isWordWrap")){
            setWordWrap((boolean)json.get("isWordWrap"));
        }
        if(json.containsKey("lineSpacing")){
            setLineSpacing((int)json.get("lineSpacing"));
        }
        if(json.containsKey("letterSpacing")){
            setLetterSpacing((int)json.get("letterSpacing"));
        }
        if(json.containsKey("scaleX")){
            setScaleX((double)json.get("scaleX"));
        }
        if(json.containsKey("scaleY")){
            setScaleY((double)json.get("scaleY"));
        }
    }

    public TextUnitJson(String text, int fontSize, int align, boolean isBold, boolean isUnderline,
                        boolean isWordWrap, int lineSpacing, int letterSpacing,
                        double scaleX, double scaleY) {
        this.text = text;
        this.fontSize = fontSize;
        this.align = align;
        this.isBold = isBold;
        this.isUnderline = isUnderline;
        this.isWordWrap = isWordWrap;
        this.lineSpacing = lineSpacing;
        this.letterSpacing = letterSpacing;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    TextUnit toTextUnit(){
        Align al = Align.LEFT;
        if(align==Align.CENTER.getValue()){
            al = Align.CENTER;
        } else if(align==Align.RIGHT.getValue()){
            al = Align.RIGHT;
        }
        TextUnit textUnit = new TextUnit(text,fontSize,al);
        textUnit.setBold(isBold);
        textUnit.setLetterSpacing(letterSpacing);
        textUnit.setLineSpacing(lineSpacing);
        textUnit.setUnderline(isUnderline);
        textUnit.setWordWrap(isWordWrap);
        textUnit.setScaleX((float)scaleX);
        textUnit.setScaleY((float) scaleY);
        return textUnit;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public void setUnderline(boolean underline) {
        isUnderline = underline;
    }

    public boolean isWordWrap() {
        return isWordWrap;
    }

    public void setWordWrap(boolean wordWrap) {
        isWordWrap = wordWrap;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public int getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(int letterSpacing) {
        this.letterSpacing = letterSpacing;
    }

    public double getScaleX() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

}
