package com.yrwcy.carpool.service;

import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yrwcy.carpool.mapper.UserMapper;
import com.yrwcy.carpool.pojo.User;
import com.yrwcy.carpool.util.OssManagerUtil;
import com.yrwcy.carpool.util.Sample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    //更新头像
    public void updateHeadPortrait(MultipartFile file){
        String url = OssManagerUtil.getUrl(file);

        QueryWrapper<User> wrapper=new QueryWrapper<>();

        wrapper.eq("student_id",StpUtil.getLoginId());

        User user = userMapper.selectOne(wrapper);

        String oldUrl=user.getHeadPortrait();

        if(!oldUrl.equals("https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/65085d3427024923aec6bc91064bcc13.png")){
            OssManagerUtil.deleteImage(oldUrl);
        }
        user.setHeadPortrait(url);

        userMapper.update(user,wrapper);
    }

    //改昵称
    public void updateName(String name){
        UpdateWrapper<User> wrapper=new UpdateWrapper<>();

        wrapper.eq("student_id",StpUtil.getLoginId().toString())
                .set("name",name);

        userMapper.update(null,wrapper);
    }

    //判断已有拼车单数量
    public Map<String,Object> getJudge(){

        Map<String,Object> map=new HashMap<>();

        QueryWrapper<User> wrapper=new QueryWrapper<>();

        wrapper.eq("student_id",StpUtil.getLoginId().toString());

        User user = userMapper.selectOne(wrapper);

        if(user.getCurrentFormCount()==2){
            map.put("isOk",false);

            map.put("message","您已参与两个拼车单，无法继续添加");
        }else {
            map.put("isOk",true);
        }

        return map;
    }

    //通过手机验证码登录时获得学号
    public String getSnoByPho(String pho)throws Exception{

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("phone",SaBase64Util.encode(pho));

        User user = userMapper.selectOne(queryWrapper);

        if (user == null){
            throw new Exception("查找不到学号");
        }

        return user.getStudentId();
    }

    //绑定手机号
    public void setPhoBySno(String sno, String pho){

        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();

        userUpdateWrapper.eq("student_id",sno)
                .set("phone",SaBase64Util.encode(pho));

        userMapper.update(null, userUpdateWrapper);
    }

    //验证学号密码
    public boolean isSnoAndPassword(String sno,String password){

        String encode = SaBase64Util.encode(password);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("student_id",sno);

        User user = userMapper.selectOne(queryWrapper);

        if(user==null){
            return false;
        }else {
            return encode.equals(user.getPassword());
        }
    }

    //发送验证码
    public void sendMessage(String phone){

        Jedis jedis = new Jedis("119.91.225.64",6379);

        jedis.auth("020704Yrw");

        StringBuilder nums = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            Random random = new Random();

            int i1 = random.nextInt(10);

            nums.append(i1);
        }

        try {
            jedis.setex(phone,600, nums.toString());

            Sample.sendMessage(phone, nums.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
