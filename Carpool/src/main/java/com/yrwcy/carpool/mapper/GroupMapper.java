package com.yrwcy.carpool.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yrwcy.carpool.pojo.Group;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMapper extends BaseMapper<Group> {
    List<String> getSnoList(String id);
}
