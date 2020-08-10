package com.leyou.item.service;

import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.mapper.SpecificationMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationService {
    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> querySpecGroups(Long id) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(id);
        List<SpecGroup> specGroups = this.specificationMapper.select(specGroup);
        specGroups.forEach(spec->{
            spec.setSpecParams(querySpecParamsByGid(spec.getId(),id,null,null));
        });
        return  specGroups;
    }

    public List<SpecParam> querySpecParamsByGid(Long gid,Long cid,Boolean searching,Boolean generic) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        specParam.setGeneric(generic);
        return this.specParamMapper.select(specParam);
    }

    public void insetGroup(SpecGroup specGroup) {
        this.specificationMapper.insertSelective(specGroup);
    }

    public void updateGroup(SpecGroup specGroup) {
        this.specificationMapper.updateByPrimaryKeySelective(specGroup);
    }

    public void deleteGroup(Long id) {
        this.specificationMapper.deleteByPrimaryKey(id);
    }

    public void insertSpecParam(SpecParam specParam) {
        this.specParamMapper.insertSelective(specParam);
    }

    public void updateSpecParam(SpecParam specParam) {
        this.specParamMapper.updateByPrimaryKeySelective(specParam);
    }

    public void deleteSpecParam(Long id) {
        this.specParamMapper.deleteByPrimaryKey(id);
    }

}
