package com.leyou.serach.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.serach.pojo.Goods;
import com.leyou.serach.pojo.SearchRequest;

import com.leyou.serach.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> page(@RequestBody SearchRequest searchRequest){
        PageResult<Goods> goodsPageResult = this.searchService.page(searchRequest);

        if(goodsPageResult!=null && goodsPageResult.getItems()!=null && goodsPageResult.getItems().size()!=0){
            return ResponseEntity.ok(goodsPageResult);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
