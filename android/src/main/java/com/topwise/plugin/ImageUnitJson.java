package com.topwise.plugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Environment;

import com.topwise.cloudpos.aidl.printer.Align;
import com.topwise.cloudpos.aidl.printer.ImageUnit;
import com.topwise.cloudpos.aidl.printer.TextUnit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caixh
 * @description
 * @date 2023/4/13 16:44
 */
public class ImageUnitJson extends BaseJson{

    private String content = "";
    private int type = ImageType.QRCODE.value();
    private int left = 0;
    private int top = 0;
    private int width = 0;
    private int height = 0;
    private int align = Align.LEFT.getValue();

    public ImageUnitJson(){

    }

    @Override
    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("content",content);
        json.put("type",type);
        json.put("left",left);
        json.put("top",top);
        json.put("width",width);
        json.put("height",height);
        json.put("align",align);
        return json;
    }

    @Override
    public void fromJson(Map<String, Object> json) {
        if(json==null)
            return;
        if(json.containsKey("content")){
            setContent((String)json.get("content"));
        }
        if(json.containsKey("type")){
            setType((int)json.get("type"));
        }
        if(json.containsKey("left")){
            setLeft((int)json.get("left"));
        }
        if(json.containsKey("top")){
            setTop((int)json.get("top"));
        }
        if(json.containsKey("width")){
            setWidth((int)json.get("width"));
        }
        if(json.containsKey("height")){
            setHeight((int)json.get("height"));
        }
        if(json.containsKey("align")){
            setAlign((int)json.get("align"));
        }
    }

    public ImageUnitJson(String content, int type, int left, int top, int width, int height, int align) {
        this.content = content;
        this.type = type;
        this.left = left;
        this.align = align;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public ImageUnit toImageUnit(){
        Context context = TopUsdkManager.getInstance().getContext();
        if(context==null||content==null||content.equals("")){
            return null;
        }
        Align al = Align.LEFT;
        if(align==Align.CENTER.getValue()){
            al = Align.CENTER;
        } else if(align==Align.RIGHT.getValue()){
            al = Align.RIGHT;
        }
        Bitmap bitmap = null;
        ImageType imageType = ImageType.type(type);
        switch (imageType){
            case ASSET:
                try {
                    bitmap = BitmapFactory.decodeStream(context.getAssets().open(content));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SDCARD:
                boolean isSdCardExist = Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
                if (isSdCardExist) {
                    String sdpath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath();// 获取sdcard的根路径
                    String filepath = sdpath + File.separator + content;
                    File file = new File(filepath);
                    if (file.exists()) {
                        bitmap = BitmapFactory.decodeFile(filepath);
                    }
                }
                break;
            case QRCODE:
                bitmap = CodeUtil.createQRImage(content,width,height,null);
                break;
            case BARCODE:
                bitmap = CodeUtil.createBarcode(content,width,height);
                break;
            default:
                break;
        }
        if(bitmap==null){
            return null;
        }
        ImageUnit imageUnit = null;
        Rect margins = null;
        if(left>0&&top>0){
            margins = new Rect(left,top,left+width,top+height);
            imageUnit = new ImageUnit(margins,bitmap,width,height);
        }else{
            imageUnit = new ImageUnit(bitmap,width,height);
        }
        imageUnit.setAlign(al);
        return imageUnit;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

}
