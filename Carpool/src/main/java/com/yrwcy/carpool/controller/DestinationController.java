package com.yrwcy.carpool.controller;

import com.yrwcy.carpool.pojo.Destination;
import com.yrwcy.carpool.service.DestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DestinationController {

    @Autowired
    DestinationService destinationService;

    /**
     * 主页获得所有列表
     * @return 含拼车单数的目的地列表
     */
    @RequestMapping("/getList")
    @ResponseBody
    public List<Destination> getList(){
        return destinationService.getList();
    }


    /**
     * 通过种类筛选
     * @param category 目的地类别
     * @return 目的地列表
     */
    @RequestMapping("/byCategory")
    @ResponseBody
    public List<Destination> getListByCategory(@RequestParam("category")String category){
        return destinationService.getListByCategory(category);
    }

    /**
     * 查询目的地
     * @param mes 查询相关名字
     * @return 目的地列表
     */
    @RequestMapping("/query")
    @ResponseBody
    public List<Destination> queryDestination(@RequestParam("mes")String mes){
        return destinationService.queryDestination(mes);
    }


}
