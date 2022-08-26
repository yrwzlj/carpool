package com.yrwcy.carpool.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yrwcy.carpool.mapper.DestinationMapper;
import com.yrwcy.carpool.mapper.UserMapper;
import com.yrwcy.carpool.pojo.Destination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Service
public class DestinationService {
    @Autowired
    DestinationMapper destinationMapper;

    @Autowired
    UserMapper userMapper;

    public List<Destination> getList(){
        return destinationMapper.selectList(null);
    }

    public List<Destination> getListByCategory(String category){

        QueryWrapper<Destination> wrapper=new QueryWrapper<>();

        wrapper.eq("category",category);

        return destinationMapper.selectList(wrapper);
    }

    public List<Destination> queryDestination(@RequestParam("mes")String mes){
        QueryWrapper<Destination> wrapper=new QueryWrapper<>();

        wrapper.like("place",mes);

        return destinationMapper.selectList(wrapper);
    }

}
