package com.topwise.plugin;

/**
 * @author caixh
 * @description
 * @date 2023/3/14 21:04
 */
public final class PluginUtils {

    public static byte getByte(Object value) {
        if(value instanceof Integer){
            return (byte)((int)value&0xFF);
        }else if(value instanceof Long){
            return (byte)((long)value&0xFF);
        }else if(value instanceof Byte){
            return (byte)value;
        }else{
            return 0;
        }
    }

    public static long getLong(Object value) {
        if(value instanceof Integer){
            return Long.valueOf((int)value);
        }else if(value instanceof Long){
            return (long)value;
        }else{
            return 0;
        }
    }
}
