package com.topwise.plugin;

import java.util.Map;

public abstract class BaseJson{
    public abstract Map<String,Object> toJson();
    public abstract void fromJson(Map<String, Object> json);
}
