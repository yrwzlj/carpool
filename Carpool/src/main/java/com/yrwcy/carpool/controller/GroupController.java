package com.yrwcy.carpool.controller;

import com.yrwcy.carpool.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class GroupController {

    @Autowired
    GroupService groupService;
}
