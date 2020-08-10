package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("spec")//spec/groups/3
public class SpecController {


    @Autowired
    private SpecificationService specificationService;

    @GetMapping("groups/{id}")
    @ResponseBody
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("id")Long id){
        List<SpecGroup> groupList = this.specificationService.querySpecGroups(id);

        if(groupList!=null  && groupList.size()!=0){
            return ResponseEntity.ok(groupList);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("params")
    @ResponseBody
    public ResponseEntity<List<SpecParam>> querySpecParamsByGid(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching,
            @RequestParam(value = "generic",required = false)Boolean generic
    ){
        List<SpecParam> specParams = this.specificationService.querySpecParamsByGid(gid,cid,searching,generic);
        if(specParams !=null && specParams.size()!=0){
            return ResponseEntity.ok(specParams);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("group")
    @ResponseBody
    public ResponseEntity<Void> insertGroup(@RequestBody SpecGroup specGroup){
        this.specificationService.insetGroup(specGroup);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("group")
    @ResponseBody
    public ResponseEntity<Void> updateGroup(@RequestBody SpecGroup specGroup){
        this.specificationService.updateGroup(specGroup);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("group/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteGroup(@PathVariable("id")Long id){
        this.specificationService.deleteGroup(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("param")
    @ResponseBody
    public ResponseEntity<Void> insretSpecParam(@RequestBody SpecParam specParam){
        this.specificationService.insertSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("param")
    @ResponseBody
    public ResponseEntity<Void> updateSpecParam(@RequestBody SpecParam specParam){
        this.specificationService.updateSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("param/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("id")Long id){
        this.specificationService.deleteSpecParam(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
