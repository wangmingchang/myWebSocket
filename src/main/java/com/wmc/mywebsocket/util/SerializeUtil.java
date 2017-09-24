package com.wmc.mywebsocket.util;
import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;

public class SerializeUtil implements RedisSerializer<Object>{

    static final byte[] EMPTY_ARRAY = new byte[0];
    private final Charset charset;
    
    public SerializeUtil() {
        // TODO Auto-generated constructor stub
        this(Charset.forName("UTF8"));
    }
    
    @SuppressWarnings("deprecation")
	public SerializeUtil(Charset charset) {
        // TODO Auto-generated constructor stub
        Assert.notNull(charset);
        this.charset = charset;
    }

    public byte[] serialize(Object object){  //序列化方法
        // TODO Auto-generated method stub
        try {
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
            String jsonString = jsonObject.toString();
            return (jsonString == null ? EMPTY_ARRAY : jsonString.getBytes(charset));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
        
    }

    public Object deserialize(byte[] bytes) throws SerializationException { //反序列化
        // TODO Auto-generated method stub
        String objectStr = null;
        Object object = null;
        if (bytes == null) {
            return object;
        }
        try {
            objectStr = new String(bytes,charset); //byte数组转换为String
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(objectStr); //String转化为JSONObject
            object = jsonObject;  //返回的是JSONObject类型  取数据时候需要再次转换一下
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return object;
    }
        
}