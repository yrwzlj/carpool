package com.yrwcy.carpool.controller;

import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.yrwcy.carpool.mapper.UserMapper;
import com.yrwcy.carpool.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;


@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    /**
     * 换头像
     * @param file 头像
     */
    @PostMapping("/updateImage")
    @ResponseBody
    public void updateHead(@RequestParam("file") MultipartFile file) {
        userService.updateHeadPortrait(file);
    }

    /**
     * 改昵称
     * @param name 新昵称
     */
    @RequestMapping("/updateName")
    @ResponseBody
    public void updateName(@RequestParam("name") String name){
        userService.updateName(name);
    }

    /**
     * 获得用户信息
     * @return 用户信息
     */
    @RequestMapping("/userDetails")
    @ResponseBody
    public Map<String,Object> userDetails(){
        Map<String, Object> map = userMapper.userDetails(StpUtil.getLoginId().toString());

        map.replace("phone", SaBase64Util.decode(map.get("phone").toString()));

        return map;
    }

    /**
     * 判断是否能够继续添加拼车单
     * @return 是否有加入
     */
    @RequestMapping("/toJudge")
    @ResponseBody
    public Map<String,Object> getJudge(){
        return userService.getJudge();
    }

    /**
     * 绑定手机号码
     * @param phone 手机号码
     * @param message 验证码
     */
    @RequestMapping("/upByPho")
    @ResponseBody
    public SaResult signupByPho(String phone, String message){

        Jedis jedis = new Jedis("119.91.225.64",6379);

        jedis.auth("020704Yrw");

        String s = jedis.get(phone);

        if (s.equals(message)){
            userService.setPhoBySno(StpUtil.getLoginId().toString(), phone);

            StpUtil.login(StpUtil.getLoginId().toString());

            return SaResult.data(StpUtil.getTokenInfo());
        }else {
            return  SaResult.error("验证码错误");
        }
    }

    /**
     * 获取短语验证码并发送
     * @param phone 手机号码
     */
    @RequestMapping("/sendMessage")
    @ResponseBody
    public void sendMessage(String phone){
        userService.sendMessage(phone);
    }


}
