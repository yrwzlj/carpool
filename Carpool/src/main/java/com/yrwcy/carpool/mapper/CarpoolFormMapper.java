package com.yrwcy.carpool.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yrwcy.carpool.pojo.CarpoolForm;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CarpoolFormMapper extends BaseMapper<CarpoolForm> {

    List<Map<Object,Object>> selectByDestination(String destination);

    Map<Object,Object> selectById(String id);

    List<Map<Object,Object>> hangMessage(String id);
}
