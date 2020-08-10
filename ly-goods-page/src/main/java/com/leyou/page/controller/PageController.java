package com.leyou.page.controller;

import com.leyou.page.service.FileService;
import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    @Autowired
    private FileService fileService;

    @GetMapping("/item/{spuId}.html")
    public String page(@PathVariable("spuId")Long spuId, Model model){

        model.addAllAttributes(pageService.loadData(spuId));

        // 判断是否需要生成新的页面
        if(!this.fileService.exists(spuId)){
            //this.fileService.syncCreateHtml(spuId);
        }
        return "item";

    }
}
