package com.yrwcy.carpool.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yrwcy.carpool.mapper.GroupMapper;
import com.yrwcy.carpool.pojo.Group;
import com.yrwcy.carpool.util.DeleteDelay;
import com.yrwcy.carpool.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GroupService {

    @Autowired
    GroupMapper groupMapper;

    @Autowired
    RedisUtils redisUtils;

    public List<String> getGroupMember(String form_id){
        String s = redisUtils.get("group"+form_id);

        List<String> list = new ArrayList<>();

        if (s == null){
            LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();

            queryWrapper.eq(Group::getFormId,form_id);

            List<Group> groups = groupMapper.selectList(queryWrapper);

            String memberIds = "";
            for (Group group : groups){
                memberIds = memberIds + group.getStudentId() + " ";
                list.add(group.getStudentId());
            }
            System.out.println("db" + memberIds);

            redisUtils.set("group"+form_id,memberIds,1000*60*10,TimeUnit.MILLISECONDS);
        }else {
            String[] strings = s.split(" ");

            for (String str : strings){
                list.add(str);
            }
            System.out.println("cache"+list);
        }

        return list;
    }

    @DeleteDelay(name = "group")
    public boolean insertMember(String form_id,String stu_id){

        Group group = new Group(form_id,stu_id);

        groupMapper.insert(group);

        return true;
    }

}
