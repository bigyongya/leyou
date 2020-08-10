package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.clients.GoodsClient;
import com.leyou.page.clients.SpecClient;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    public Map<String,Object> loadData(Long spuId) {

        Map<String,Object> data = new HashMap<>();

        //根据spuId查询spu信息
        Spu spu = this.goodsClient.findSpuById(spuId);

        //根据spuId查询spuDetail信息
        SpuDetail spuDetail = this.goodsClient.findSpuDetailById(spuId);

        //根据spuId查询skus信息
        List<Sku> skus = this.goodsClient.findSkuBySpuId(spuId);

        data.put("spu",spu);

        data.put("spuDetail",spuDetail);

        data.put("skus",skus);

        Map<Long,String> specMap = new HashMap<>();

        List<SpecParam> specParams = this.specClient.querySpecParamsByGid(null, spu.getCid3(), null, false);

        specParams.forEach(specParam -> {
            specMap.put(specParam.getId(),specParam.getName());
        });

        data.put("specMap",specMap);

        List<SpecGroup> specGroups = this.specClient.querySpecGroups(spu.getCid3());

        data.put("specGroups",specGroups);

        return data;

    }

}
