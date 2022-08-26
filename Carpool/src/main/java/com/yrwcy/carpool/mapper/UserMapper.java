package com.yrwcy.carpool.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yrwcy.carpool.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserMapper extends BaseMapper<User> {

    List<String> selHeadPortrait(String id);

    String getOneHeadPortrait(String id);

    List<Map<String,Object>> userDetailsList(String formId);

    Map<String,Object> userDetails(String sno);

    List<User> selectByGroup(String form_id);
}
