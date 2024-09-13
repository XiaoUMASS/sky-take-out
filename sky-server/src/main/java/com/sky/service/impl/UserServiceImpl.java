package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //微信接口服务请求地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    //用于获取配置文件中的APPID和APPSECRET（配置属性类）
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务获得当前微信用户的openId
        String openid = getOpenid(userLoginDTO.getCode());
        //判断openId是否为空，如果为空表示登陆失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断是否为新用户（openId是否已经存在于数据库中）
        User user = userMapper.getByOpenId(openid);
        //如果是新用户，自动完成注册
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回用户对象
        return user;
    }

    private String getOpenid(String code) {
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("appid", weChatProperties.getAppid());
        paraMap.put("secret",weChatProperties.getSecret());
        paraMap.put("js_code", code);
        paraMap.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, paraMap);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
