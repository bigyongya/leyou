package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

public interface SpecApi {
    @GetMapping("spec/params")
     List<SpecParam> querySpecParamsByGid(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching,
            @RequestParam(value = "generic",required = false)Boolean generic
    );

    @GetMapping("spec/groups/{id}")
    @ResponseBody
    List<SpecGroup> querySpecGroups(@PathVariable("id")Long id);
}
