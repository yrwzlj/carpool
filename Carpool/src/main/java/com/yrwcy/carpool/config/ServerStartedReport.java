package com.yrwcy.carpool.config;

import cn.dev33.satoken.secure.SaBase64Util;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yrwcy.carpool.mapper.CarpoolFormMapper;
import com.yrwcy.carpool.mapper.DestinationMapper;
import com.yrwcy.carpool.mapper.GroupMapper;
import com.yrwcy.carpool.pojo.CarpoolForm;
import com.yrwcy.carpool.pojo.Destination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Order(2)
@Component
class ServerStartedReport implements CommandLineRunner {

    @Autowired
    CarpoolFormMapper carpoolFormMapper;

    @Autowired
    DestinationMapper destinationMapper;

    @Autowired
    GroupMapper groupMapper;


    //项目启动时将拼单在ing的设置一个定时任务
    @Override
    public void run(String... args) throws Exception {

        QueryWrapper<CarpoolForm> wrapper=new QueryWrapper<>();

        wrapper.eq("status","ing");

        List<CarpoolForm> carpoolForms = carpoolFormMapper.selectList(wrapper);

        for (CarpoolForm carpoolForm : carpoolForms) {

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    QueryWrapper<CarpoolForm> formQueryWrapper=new QueryWrapper<>();

                    formQueryWrapper.eq("id",carpoolForm.getId());

                    CarpoolForm form=carpoolFormMapper.selectOne(formQueryWrapper);

                    if(form.getStatus().equals("ing")){

                        if(form.getNeedCount()>form.getPeopleNumber()){
                            QueryWrapper<Destination> destinationQueryWrapper=new QueryWrapper<>();

                            destinationQueryWrapper.eq("place",form.getDestination());

                            Destination one = destinationMapper.selectOne(destinationQueryWrapper);

                            one.setCount(one.getCount()-1);

                            destinationMapper.update(one,destinationQueryWrapper);
                        }

                        if(form.getPeopleNumber()>=2){
                            form.setStatus("success");

                            Jedis jedis = new Jedis("119.91.225.64",6379);

                            jedis.auth("020704Yrw");

                            List<String> snoList=groupMapper.getSnoList(form.getId());

                            for (String s : snoList) {
                                jedis.lpush(form.getId() +"hangHead",s);
                            }
                        }else{
                            form.setStatus("fail");
                        }

                        carpoolFormMapper.update(form,formQueryWrapper);
                    }
                }
            };

            Timer timer = new Timer();

            timer.schedule(task, carpoolForm.getDeadline());

        }


    }
}
