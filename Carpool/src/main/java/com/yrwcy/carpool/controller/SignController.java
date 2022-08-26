package com.yrwcy.carpool.controller;

import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yrwcy.carpool.mapper.UserMapper;
import com.yrwcy.carpool.pojo.User;
import com.yrwcy.carpool.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;


/**
 * 手机号码 格式 未验证
 */
@Controller
@RequestMapping("/sign")
public class SignController {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    /**
     * 学号密码登录
     * @param sno 学号
     * @param password 密码
     */
    @RequestMapping("/BySno")
    @ResponseBody
    public SaResult signInBySno(String sno, String password){

        if (!userService.isSnoAndPassword(sno,password))
            return SaResult.error("学号或密码错误");

        StpUtil.login(sno,true);

        SaTokenInfo info = StpUtil.getTokenInfo();

        QueryWrapper<User> wrapper=new QueryWrapper<>();

        wrapper.eq("student_id",sno);

        User user = userMapper.selectOne(wrapper);

        info.setTag(SaBase64Util.decode(user.getPhone()));

        return SaResult.data(info);
    }

    /**
     *  手机号码登录
     * @param phone 手机号
     * @param message 验证码
     */
    @RequestMapping("/ByPho")
    @ResponseBody
    public SaResult signInByPho(String phone, String message){
        Jedis jedis = new Jedis("119.91.225.64",6379);

        jedis.auth("020704Yrw");

        String s = jedis.get(phone);

        String snoByPho ;

        try {
            snoByPho = userService.getSnoByPho(phone);

        } catch (Exception e) {
            e.printStackTrace();

            return SaResult.error("手机号未与学号绑定");
        }

        if (s.equals(message)){
            StpUtil.login(snoByPho);

            return SaResult.data(StpUtil.getTokenInfo());
        }else {
            return  SaResult.error("验证码错误");
        }
    }
}
