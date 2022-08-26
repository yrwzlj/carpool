package com.yrwcy.carpool.util;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yrwcy.carpool.mapper.CarpoolFormMapper;
import com.yrwcy.carpool.mapper.EvaluateMapper;
import com.yrwcy.carpool.mapper.GroupMapper;
import com.yrwcy.carpool.mapper.UserMapper;
import com.yrwcy.carpool.pojo.CarpoolForm;
import com.yrwcy.carpool.pojo.Evaluate;
import com.yrwcy.carpool.pojo.Group;
import com.yrwcy.carpool.pojo.User;
import com.yrwcy.carpool.service.CarpoolFormService;
import com.yrwcy.carpool.service.EvaluateService;
import com.yrwcy.carpool.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.*;

@Component
public class EvaluateTimer {

    @Autowired
    CarpoolFormMapper carpoolFormMapper;

    @Autowired
    EvaluateMapper evaluateMapper;

    @Autowired
    EvaluateService evaluateService;

    @Autowired
    GroupService groupService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CarpoolFormService carpoolFormService;

    @Autowired
    GroupMapper groupMapper;

    @Autowired
    RedisTemplate redisTemplate;

    //3小时轮询
    @Scheduled(fixedRate = 1000*60*60*3,initialDelay = 0)
    public void evaluateTimer() {
        int i = 0;

        QueryWrapper<CarpoolForm> queryWrapper = new QueryWrapper<>();

        queryWrapper.select("DISTINCT id")
                .eq("status","success");

        List<CarpoolForm> carpoolForms = carpoolFormMapper.selectList(queryWrapper);

        Jedis jedis = new Jedis("119.91.225.64",6379);

        jedis.auth("020704Yrw");

        //判断是否存在用户超3天未评价 自动好评
        for (CarpoolForm carpoolForm : carpoolForms){
            String formId = carpoolForm.getId();

            Long ttl = jedis.ttl(formId);
            if (ttl == -2){
                long evaluateMemberNum = evaluateService.evaluateMemberNum(formId);

                long groupMemberNum = carpoolFormService.getPeoPleNum(formId);

                if (evaluateMemberNum < groupMemberNum){
                    QueryWrapper<Evaluate> evaluateQueryWrapper = new QueryWrapper<>();

                    evaluateQueryWrapper.eq("form_id",formId)
                            .select("DISTINCT publisher");

                    List<Evaluate> evaluates = evaluateMapper.selectList(evaluateQueryWrapper);

                    //存放已评价人的id
                    Set<String> set = new HashSet<>();

                    for (Evaluate evaluate : evaluates){
                        set.add(evaluate.getPublisher());
                    }

//                    QueryWrapper<Group> groupQueryWrapper = new QueryWrapper<>();
//
//                    groupQueryWrapper.eq("form_id",formId);
//
//                    List<Group> groups = groupMapper.selectList(groupQueryWrapper);
//
//                    //获取订单中所有人id
//                    Set<String> groupSet = new HashSet<>();
//
//                    for (Group group : groups){
//                        groupSet.add(group.getStudentId());
//                    }

                    //同一拼单人的id list
                    List<String> groups = groupService.getGroupMember(formId);

                    System.out.println("now begin");

                    for (String str : groups){
                        boolean flag=false;
                        if (!set.contains(str)){

                            for (String target : groups) {
                                //补全未评价人的评价
                                if (!target.equals(str)) {
                                    Evaluate evaluate = new Evaluate(formId, str, target, 5);

                                    flag=true;

                                    evaluateMapper.insert(evaluate);
                                }
                            }
                        }

                        QueryWrapper<Group> groupQueryWrapper2=new QueryWrapper<>();

                        groupQueryWrapper2.eq("form_id",formId).eq("student_id",str);

                        groupMapper.delete(groupQueryWrapper2);

                        if(flag){
                            QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();

                            userQueryWrapper.eq("student_id",str);

                            User user = userMapper.selectOne(userQueryWrapper);

                            user.setCurrentFormCount(user.getCurrentFormCount()-1);

                            userMapper.update(user,userQueryWrapper);
                        }
                    }

                    carpoolFormService.updateEvaluate(formId);

                    UpdateWrapper<CarpoolForm> updateWrapper = new UpdateWrapper<>();

                    updateWrapper.eq("id",formId)
                            .set("status","finish");

                    carpoolFormMapper.update(null,updateWrapper);

                }
            }
        }

        QueryWrapper<CarpoolForm> wrapper=new QueryWrapper<>();

        wrapper.eq("status","finish");

        List<CarpoolForm> forms = carpoolFormMapper.selectList(wrapper);

        for (CarpoolForm form : forms) {
            Date nowTime=new Date();

            long nowTimeLong=nowTime.getTime();

            long ageDateLong=form.getDeadline().getTime();

            long cha=nowTimeLong-ageDateLong;

            if(cha>(1000*60*60*72)){
                QueryWrapper<CarpoolForm> formQueryWrapper=new QueryWrapper<>();

                formQueryWrapper.eq("id",form.getId());

                carpoolFormMapper.delete(formQueryWrapper);
            }
        }
    }
}
