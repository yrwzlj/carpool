package com.yrwcy.carpool.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "`group`")
public class Group {

    //拼车单编号
    private String formId;

    //学号
    private String studentId;
}
