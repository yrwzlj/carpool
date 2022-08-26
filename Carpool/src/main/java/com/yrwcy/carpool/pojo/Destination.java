package com.yrwcy.carpool.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Destination {

    //目的地种类
    private String category;

    //详细目的地
    @TableId(type = IdType.ASSIGN_ID)
    private String place;

    //该目的地需拼车数量
    private int count;
}
