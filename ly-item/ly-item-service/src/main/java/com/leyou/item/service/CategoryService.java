package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        return this.categoryMapper.select(category);
    }

    public List<Category> queryBrandById(Long id) {
        return this.categoryMapper.queryBrandById(id);
    }

    public void insertCategory(Category category) {
        this.categoryMapper.insertSelective(category);
    }

    public void deleteCategoryById(Long id) {
        Category category = new Category();
        category.setId(id);
        this.categoryMapper.deleteByPrimaryKey(category);
    }

    public List<String> queryNameByIds(List<Long> asList) {
        ArrayList<String> names = new ArrayList<>();
        for (Long cid : asList) {
            names.add(this.categoryMapper.selectByPrimaryKey(cid).getName());
        }
        return names;
    }
}
