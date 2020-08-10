package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Id;
import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        // 开始分页
        PageHelper.startPage(page, rows);
        // 过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().andLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
        }
        if (StringUtils.isNotBlank(sortBy)) {
            // 排序
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        // 查询
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);
        // 返回结果
        return new PageResult<>(pageInfo.getTotal(), pageInfo);
    }

    public Brand queryBrandById(Long id) {
        Brand brand = new Brand();
        brand.setId(id);
        return this.brandMapper.selectByPrimaryKey(brand);
    }

    @Transactional
    public void deleteBrandById(Long id) {
        this.brandMapper.deleteByPrimaryKey(id);
        this.brandMapper.deleteBrandByBid(id);
    }

    @Transactional
    public void insertBrand(Brand brand, List<Long> cids) {

        this.brandMapper.insertSelective(brand);
        for (Long cid : cids) {
            this.brandMapper.insertBrandAndCategory(brand.getId(),cid);
        }

    }

    public void updateBrand(Brand brand, List<Long> cids) {
        this.brandMapper.updateByPrimaryKeySelective(brand);

        //删掉tb_category_brand表的关联
        this.brandMapper.deleteBrandAndCategory(brand.getId());

        //新增
        for (Long cid : cids) {
            this.brandMapper.insertBrandAndCategory(brand.getId(),cid);
        }
    }

    public List<Brand> queryBrandByCid(Long id) {
        return this.brandMapper.queryBrandByCid(id);
    }

    public Brand queryBrandByBid(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}
