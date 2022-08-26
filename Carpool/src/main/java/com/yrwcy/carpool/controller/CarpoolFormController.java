package com.yrwcy.carpool.controller;

import com.yrwcy.carpool.pojo.CarpoolForm;
import com.yrwcy.carpool.service.CarpoolFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class CarpoolFormController {

    @Autowired
    CarpoolFormService carpoolFormService;


    /**
     * 根据目的地查询出所有的拼单粗略内容
     * @param destination 目的地
     * @return 目的地列表
     */
    @RequestMapping("/destList")
    @ResponseBody
    public List<Map<Object,Object>> getListByDestination(@RequestParam String destination){
        return carpoolFormService.getListByDestination(destination);
    }

    /**
     * 拼单详情
     * @param id 拼车单编号id
     * @return 拼车单信息
     */
    @RequestMapping("/formDetails")
    @ResponseBody
    public Map<Object,Object> formDetails(@RequestParam("id") String id){
        return carpoolFormService.formDetails(id);
    }

    /**
     * 增加拼车单
     * @param carpoolForm 拼车单大部分信息
     */
    @RequestMapping("/addForm")
    @ResponseBody
    public void addForm(CarpoolForm carpoolForm){
        carpoolFormService.addForm(carpoolForm);
    }

    /**
     * 悬挂着的拼单信息
     * @return 拼单信息列表
     */
    @RequestMapping("/hangMessage")
    @ResponseBody
    public List<Map<Object,Object>> hangMessage(){
        return carpoolFormService.hangMessage();
    }

    /**
     * 加入拼单
     * @param formId 要加入的拼车单编号id
     * @return 成功与否和失败原因
     */
    @RequestMapping("/joinForm")
    @ResponseBody
    public Map<String,Object> joinForm(String formId){
        return carpoolFormService.joinForm(formId);
    }

    /**
     * 退出拼单
     * @param formId 要加入的拼车单编号id
     */
    @RequestMapping("/secede")
    @ResponseBody
    public void secedeForm(String formId){
        carpoolFormService.secedeForm(formId);
    }

    /**
     * 取消拼单
     */
    @RequestMapping("/cancelForm")
    @ResponseBody
    public void cancelForm(String formId){
        carpoolFormService.cancelForm(formId);
    }

    /**
     * 评价拼单
     */
    @RequestMapping("/evaluateForm")
    @ResponseBody
    public void evaluateForm(String formId,String[] targets,int[] starsCount){
        carpoolFormService.evaluateForm(formId,targets,starsCount);
    }
    /**
     *成功拼车的拼单详情
     */
    @RequestMapping("/successForm")
    @ResponseBody
    public Map<String,Object> successForm(String id){
        return carpoolFormService.successForm(id);
    }

    /**
     * 评价页面详情
     */
    @RequestMapping("/evaFormDe")
    @ResponseBody
    public Map<String,Object> evaFormDe(String id){
        return carpoolFormService.evaFormDe(id);
    }
    /**
     * 确认拼单
     */
    @RequestMapping("/confirm")
    @ResponseBody
    public void confirmForm(@RequestParam("id") String id){
        carpoolFormService.confirmForm(id);
    }
}
