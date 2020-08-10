package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.tree.VoidDescriptor;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("list")
    @ResponseBody
    public ResponseEntity<List<Category>> queryCategoryByPid(@RequestParam("pid")Long pid){
        List<Category> categories = this.categoryService.queryCategoryByPid(pid);

        if(categories!=null && 0!=categories.size()){
            return ResponseEntity.ok(categories);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("bid/{id}")
    public ResponseEntity<List<Category>> queryBrandById(@PathVariable("id")Long id){
        List<Category> categories = this.categoryService.queryBrandById(id);

        if(categories!=null && 0!=categories.size()) {
            return ResponseEntity.ok(categories);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Void> insetCategory(Category category){
        this.categoryService.insertCategory(category);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable("id")Long id){
        this.categoryService.deleteCategoryById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("names")
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids")List<Long> ids){
        List<String> names = this.categoryService.queryNameByIds(ids);
        if(names!=null && names.size()!=0){
            return ResponseEntity.ok(names);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
