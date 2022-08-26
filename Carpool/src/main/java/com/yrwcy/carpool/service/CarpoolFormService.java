package com.yrwcy.carpool.service;

import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yrwcy.carpool.mapper.*;
import com.yrwcy.carpool.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CarpoolFormService {

    @Autowired
    CarpoolFormMapper carpoolFormMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    GroupMapper groupMapper;

    @Autowired
    DestinationMapper destinationMapper;

    @Autowired
    EvaluateService evaluateService;

    @Autowired
    EvaluateMapper evaluateMapper;

    @Autowired
    GroupService groupService;

    public List<Map<Object,Object>> getListByDestination(String destination){
        List<Map<Object, Object>> maps = carpoolFormMapper.selectByDestination(destination);

        for (Map<Object, Object> map : maps) {
            LocalDateTime deadline = (LocalDateTime) map.get("deadline");

            String format = deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            map.replace("deadline",format);

            List<String> headPortraits=userMapper.selHeadPortrait(map.get("id").toString());

            map.put("headPortraits",headPortraits);
        }

        return maps;
    }

    public int getPeoPleNum(String form_id){
        QueryWrapper<CarpoolForm> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("id",form_id);

        CarpoolForm carpoolForm = carpoolFormMapper.selectOne(queryWrapper);

        if (carpoolForm == null) return 0;

        return carpoolForm.getPeopleNumber();
    }

    public Map<Object,Object> formDetails(@RequestParam("id") String id) {
        Map<Object, Object> map = carpoolFormMapper.selectById(id);

        LocalDateTime deadline = (LocalDateTime) map.get("deadline");

        String format = deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        map.replace("deadline",format);

        List<String> headPortraits = userMapper.selHeadPortrait(id);

        map.put("headPortraits",headPortraits);

        map.replace("phone",SaBase64Util.decode(map.get("phone").toString()));

        if(!StpUtil.isLogin()){
            if(map.get("status").toString().equals("ing")&&!map.get("people_number").toString().equals(map.get("need_count").toString())){
                map.put("message","前往登录加入拼车单");
            }else{
                map.put("message","null");
            }

            return map;
        }else{

            QueryWrapper<Group> wrapper=new QueryWrapper<>();

            wrapper.eq("form_id",id)
                    .eq("student_id",StpUtil.getLoginId().toString());

            Group group = groupMapper.selectOne(wrapper);

            if(group!=null){
                if(map.get("student_id").toString().equals(StpUtil.getLoginId().toString())){
                    map.put("message","取消拼车");
                }else {
                    map.put("message","退出拼车");
                }
            }else {
                if(!map.get("people_number").toString().equals(map.get("need_count").toString()))
                    map.put("message","加入拼车");
                else
                    map.put("message","null");
            }
        }
        return map;
    }

    public void addForm(CarpoolForm carpoolForm){

        UUID uuid = UUID.randomUUID();

        String id = uuid.toString();

        carpoolForm.setId(id);

        if(!carpoolForm.getIsLimit()){
            carpoolForm.setCreditFloor(0);
        }

        carpoolForm.setPeopleNumber(1);

        carpoolForm.setStatus("ing");

        carpoolForm.setStudentId(StpUtil.getLoginId().toString());

        carpoolFormMapper.insert(carpoolForm);

        groupMapper.insert(new Group(id, carpoolForm.getStudentId()));

        QueryWrapper<Destination> wrapper=new QueryWrapper<>();

        wrapper.eq("place", carpoolForm.getDestination());

        Destination destination = destinationMapper.selectOne(wrapper);

        destination.setCount(destination.getCount()+1);

        destinationMapper.updateById(destination);

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();

        queryWrapper.eq("student_id",StpUtil.getLoginId().toString());

        User user = userMapper.selectOne(queryWrapper);

        user.setCurrentFormCount(user.getCurrentFormCount()+1);

        userMapper.update(user,queryWrapper);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                QueryWrapper<CarpoolForm> formQueryWrapper=new QueryWrapper<>();

                formQueryWrapper.eq("id",id);

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

    public List<Map<Object,Object>> hangMessage(){
        List<Map<Object, Object>> maps = carpoolFormMapper.hangMessage(StpUtil.getLoginId().toString());

        for (Map<Object, Object> map : maps) {
            List<String> headPortraits=new ArrayList<>();

            if(map.get("status").toString().equals("ing")||map.get("status").toString().equals("fail")){
                headPortraits=userMapper.selHeadPortrait(map.get("id").toString());
            }
            else{
                Jedis jedis = new Jedis("119.91.225.64",6379);

                jedis.auth("020704Yrw");

                List<String> snoList=jedis.lrange(map.get("id").toString()+"hangHead",0,-1);

                for (String s : snoList) {
                    headPortraits.add(userMapper.getOneHeadPortrait(s));
                }
            }

            map.put("headPortraits",headPortraits);

            LocalDateTime deadline = (LocalDateTime) map.get("deadline");

            String format = deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            map.replace("deadline",format);
        }

        return maps;
    }

    public Map<String,Object> joinForm(String formId){
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();

        userQueryWrapper.eq("student_id",StpUtil.getLoginId().toString());

        User user = userMapper.selectOne(userQueryWrapper);

        QueryWrapper<CarpoolForm> wrapper=new QueryWrapper<>();

        wrapper.eq("id",formId);

        CarpoolForm carpoolForm = carpoolFormMapper.selectOne(wrapper);

        Map<String,Object> map=new HashMap<>();

        if(user.getCreditPoints()<carpoolForm.getCreditFloor()){
            map.put("isOk",false);

            map.put("message","您的信用过低，无法加入该拼车");
        }
        else if(user.getCurrentFormCount()==2){
            map.put("isOk",false);

            map.put("message","您参与的拼车至多两个");
        }else {
            map.put("isOk",true);

            //  groupMapper.insert(new Group(formId,StpUtil.getLoginId().toString()));

            groupService.insertMember(formId,StpUtil.getLoginId().toString());

            carpoolForm.setPeopleNumber(carpoolForm.getPeopleNumber()+1);

            carpoolFormMapper.update(carpoolForm,wrapper);

            user.setCurrentFormCount(user.getCurrentFormCount()+1);

            userMapper.update(user,userQueryWrapper);

        }

        if (carpoolForm.getPeopleNumber()==carpoolForm.getNeedCount()){
            QueryWrapper<Destination> destinationQueryWrapper=new QueryWrapper<>();

            destinationQueryWrapper.eq("place",carpoolForm.getDestination());

            Destination destination = destinationMapper.selectOne(destinationQueryWrapper);

            destination.setCount(destination.getCount()-1);

            destinationMapper.update(destination,destinationQueryWrapper);
        }

        return map;
    }

    public void secedeForm(String formId){
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();

        userQueryWrapper.eq("student_id",StpUtil.getLoginId().toString());

        User user = userMapper.selectOne(userQueryWrapper);

        user.setCurrentFormCount(user.getCurrentFormCount()-1);

        userMapper.update(user,userQueryWrapper);

        QueryWrapper<CarpoolForm> wrapper=new QueryWrapper<>();

        wrapper.eq("id",formId);

        CarpoolForm carpoolForm = carpoolFormMapper.selectOne(wrapper);

        if(carpoolForm.getPeopleNumber()==carpoolForm.getNeedCount()){

            QueryWrapper<Destination> destinationQueryWrapper=new QueryWrapper<>();

            destinationQueryWrapper.eq("place",carpoolForm.getDestination());

            Destination destination = destinationMapper.selectOne(destinationQueryWrapper);

            destination.setCount(destination.getCount()+1);

            destinationMapper.update(destination,destinationQueryWrapper);
        }

        carpoolForm.setPeopleNumber(carpoolForm.getPeopleNumber()-1);

        carpoolFormMapper.update(carpoolForm,wrapper);

        QueryWrapper<Group> groupQueryWrapper=new QueryWrapper<>();

        groupQueryWrapper.eq("form_id",formId)
                        .eq("student_id",StpUtil.getLoginId().toString());

        groupMapper.delete(groupQueryWrapper);
    }

    /**
     * 取消订单
     */
    public void  cancelForm(String formId){
        String loginId = (String)StpUtil.getLoginId();

        QueryWrapper<CarpoolForm> wrapper = new QueryWrapper<>();

        wrapper.eq("id",formId);

        CarpoolForm carpoolForm = carpoolFormMapper.selectOne(wrapper);

        if(carpoolForm.getPeopleNumber()!=1){
            Jedis jedis = new Jedis("119.91.225.64",6379);

            jedis.auth("020704Yrw");

            List<String> snoList=groupMapper.getSnoList(carpoolForm.getId());

            for (String s : snoList) {
                jedis.lpush(carpoolForm.getId() +"hangHead",s);
            }
        }

        carpoolForm.setStatus("cancel");

        carpoolFormMapper.update(carpoolForm,wrapper);

        List<User> users = userMapper.selectByGroup(formId);

        confirmForm(formId);

        QueryWrapper<Destination> destinationQueryWrapper=new QueryWrapper<>();

        destinationQueryWrapper.eq("place",carpoolForm.getDestination());

        Destination destination = destinationMapper.selectOne(destinationQueryWrapper);

        destination.setCount(destination.getCount()-1);

        destinationMapper.update(destination,destinationQueryWrapper);

        //当 当前订单仅有发起人1人时 不扣 诚信分
        if (users.size() == 1) return ;

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("student_id",loginId);

        User user = userMapper.selectOne(queryWrapper);

        int newCreditPoints = (int)((user.getCreditPoints() * 0.05 * user.getHistoryFormCount() / (user.getHistoryFormCount()+1)) /0.05);

        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();

        userUpdateWrapper.eq("student_id",loginId)
                .set("history_form_count",user.getHistoryFormCount()+1)
                .set("credit_points",newCreditPoints);

        userMapper.update(null,userUpdateWrapper);
    }

    /**
     * 订单评价
     * @param targets 评价对象
     * @param starsCount 不同用户给的星星数量
     */
    public void evaluateForm(String formId,String[] targets,int[] starsCount){

        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();

        userQueryWrapper.eq("student_id",StpUtil.getLoginId());

        User user = userMapper.selectOne(userQueryWrapper);

        user.setCurrentFormCount(user.getCurrentFormCount()-1);

        userMapper.update(user,userQueryWrapper);

        QueryWrapper<Group> wrapper=new QueryWrapper<>();

        wrapper.eq("form_id",formId).eq("student_id",StpUtil.getLoginId());

        groupMapper.delete(wrapper);

        long groupMemberNum = getPeoPleNum(formId);

        Jedis jedis = new Jedis("119.91.225.64",6379);

        jedis.auth("020704Yrw");

        //已评价人数
        long  evaerNum= 0;

        if (jedis.exists(formId)){
            evaerNum = Long.valueOf(jedis.get(formId));
        }

        for (int i = 0; i < targets.length; i++) {
            Evaluate evaluate = new Evaluate(formId, StpUtil.getLoginId().toString(),targets[i],starsCount[i]);

            evaluateMapper.insert(evaluate);
        }

        /*
          3种情况
          1.该订单未有评价
          2.评价人数少于groupMemberNum-1
          3.完成评价，更新
         */
        if (evaerNum == 0){
            jedis.setex(formId,259200,"1");

            evaerNum = 1;
        }else if (evaerNum < groupMemberNum-1){

            jedis.incr(formId);

        }else {
            jedis.incr(formId);

            UpdateWrapper<CarpoolForm> updateWrapper = new UpdateWrapper<>();

            updateWrapper.eq("id",formId)
                    .set("status","finish");

            carpoolFormMapper.update(null,updateWrapper);

           updateEvaluate(formId);
        }
    }

    /**
     * 更新个人信誉
     * @param formId 拼单id
     */
    public void updateEvaluate(String formId){
        QueryWrapper<Evaluate> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("form_id",formId);

        List<Evaluate> evaluates = evaluateMapper.selectList(queryWrapper);

        //合并同一单人评价
        Map<String,Integer> targetStars = new HashMap<>();

        for (Evaluate evaluate:evaluates){

            String target = evaluate.getTarget();

            if (targetStars.containsKey(target)){
                targetStars.replace(target,targetStars.get(target)+evaluate.getStarcount());
            }else {
                targetStars.put(target,evaluate.getStarcount());
            }

        }

        for (String target : targetStars.keySet()){
            Integer stars = targetStars.get(target);

            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();

            userQueryWrapper.eq("student_id",target);

            User user = userMapper.selectOne(userQueryWrapper);

            int newCreditPoints = (int)(((user.getCreditPoints() * 0.05 * user.getHistoryFormCount() + stars/(targetStars.keySet().size()-1)) / (user.getHistoryFormCount()+1)) /0.05);

            UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();

            userUpdateWrapper.eq("student_id",target)
                    .set("history_form_count",user.getHistoryFormCount()+1)
                    .set("credit_points",newCreditPoints);

            userMapper.update(null,userUpdateWrapper);
        }
    }
    public Map<String,Object> successForm(String id){
        QueryWrapper<CarpoolForm> wrapper=new QueryWrapper<>();

        wrapper.eq("id",id);

        CarpoolForm carpoolForm = carpoolFormMapper.selectOne(wrapper);

        Map<String,Object> map=new HashMap<>();

        Jedis jedis = new Jedis("119.91.225.64",6379);

        jedis.auth("020704Yrw");

        List<Map<String, Object>> list=new ArrayList<>();

        map.put("isEval",false);

        if(!jedis.exists(id+"snoList")){
            list=userMapper.userDetailsList(id);

            for (Map<String, Object> objectMap : list) {
                jedis.lpush(id+"snoList",objectMap.get("student_id").toString());
            }
        }else{
            List<String> snoList=jedis.lrange(id+"snoList",0,-1);

            boolean flag= StpUtil.isLogin();

            for (String s : snoList) {
                list.add(userMapper.userDetails(s));

                if(flag){
                    if(s.equals(StpUtil.getLoginId().toString())){
                        map.replace("isEval",true);
                    }
                }
            }
        }

        for (Map<String, Object> objectMap : list) {
            objectMap.replace("phone", SaBase64Util.decode(objectMap.get("phone").toString()));

            if(objectMap.get("student_id").toString().equals(carpoolForm.getStudentId())){

                objectMap.put("isMas",true);
            }else{
                objectMap.put("isMas",false);
            }
        }

        map.put("userList",list);

        map.put("carpoolForm",carpoolForm);

        return map;
    }
    public Map<String,Object> evaFormDe(String id){
        Map<String, Object> map = successForm(id);

        List<Map<Object, Object>> list= (List<Map<Object, Object>>) map.get("userList");

        System.out.println(list);

        list.removeIf(objectMap -> objectMap.get("student_id").toString().equals(StpUtil.getLoginId().toString()));

        map.replace("list",list);

        return map;
    }

    public void confirmForm( String id){

        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();

        userQueryWrapper.eq("student_id",StpUtil.getLoginId());

        User user = userMapper.selectOne(userQueryWrapper);

        user.setCurrentFormCount(user.getCurrentFormCount()-1);

        userMapper.update(user,userQueryWrapper);

        QueryWrapper<Group> wrapper=new QueryWrapper<>();

        wrapper.eq("form_id",id).eq("student_id",StpUtil.getLoginId());

        groupMapper.delete(wrapper);

        QueryWrapper<CarpoolForm> queryWrapper=new QueryWrapper<>();

        queryWrapper.eq("id",id);

        Jedis jedis = new Jedis("119.91.225.64",6379);

        jedis.auth("020704Yrw");

        if(!jedis.exists(id+"confirm")){
            CarpoolForm carpoolForm = carpoolFormMapper.selectOne(queryWrapper);

            jedis.set(id+"confirm","0");

            jedis.set(id+"people",String.valueOf(carpoolForm.getPeopleNumber()));
        }
        jedis.incr(id+"confirm");

        if(jedis.get(id+"confirm").equals(jedis.get(id+"people"))){

            jedis.del(id+"confirm");

            jedis.del(id+"people");

            CarpoolForm carpoolForm = carpoolFormMapper.selectOne(queryWrapper);

            carpoolForm.setStatus("finish");

            carpoolFormMapper.update(carpoolForm,queryWrapper);

            if(jedis.exists(id +"hangHead")){
                jedis.del(id +"hangHead");
            }
        }
    }
}
