package com.yrwcy.carpool.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    //学号
    @TableId(type = IdType.ASSIGN_ID)
    private String studentId;

    //昵称
    private String name;

    //电话
    private String phone;

    //密码
    private String password;

    //信誉分
    private int creditPoints;

    //头像
    private String headPortrait;

    //相关拼单数
    private int CurrentFormCount;

    //历史拼单数
    private int historyFormCount;
}
