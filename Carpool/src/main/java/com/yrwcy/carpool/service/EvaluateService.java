package com.yrwcy.carpool.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yrwcy.carpool.mapper.EvaluateMapper;
import com.yrwcy.carpool.pojo.Evaluate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluateService {

    @Autowired
    EvaluateMapper evaluateMapper;

    /**
     *获取同一拼单参与评价的人数
     * @param from_id 拼单id
     */
    public long evaluateMemberNum(String from_id){
        QueryWrapper<Evaluate> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("form_id",from_id)
                .select("DISTINCT publisher");

        return evaluateMapper.selectCount(queryWrapper);
    }

    public int getStarsNum(String form_id){

        QueryWrapper<Evaluate> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("form_id",form_id);

        List<Evaluate> evaluates = evaluateMapper.selectList(queryWrapper);

        int stars = 0;

        for (Evaluate evaluate:evaluates){
            stars += evaluate.getStarcount();
        }

        return stars;
    }


}
