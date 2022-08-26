package com.yrwcy.carpool.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
public class CarpoolForm {

    //拼车单号
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    //发起人的学号
    private String studentId;

    //出发地
    private String origin;

    //目的地
    private String destination;

    //目的地类别
    private String category;

    //是否有信用限制
    private Boolean isLimit;

    //信用下限
    private int creditFloor;

    //当前拼车人数
    private int peopleNumber;

    //需要拼车人数
    private int needCount;

    //备注
    private String remark;

    //截止日期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    //拼单状态
    private String status;

}
