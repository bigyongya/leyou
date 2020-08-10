package com.leyou.serach.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.*;
import com.leyou.serach.client.CategoryClient;
import com.leyou.serach.client.GoodsClient;
import com.leyou.serach.client.SpecClient;
import com.leyou.serach.pojo.Goods;
import com.leyou.serach.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.java2d.pipe.SpanClipRenderer;

import java.util.*;

@Service
public class GoodsService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private GoodsRepository goodsRepository;


    public Goods buildGoods(SpuBo spu){

        Goods goods = new Goods();

        BeanUtils.copyProperties(spu,goods);

        //查询cid1、2、3对应的中文名称
        List<String> names = categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //all
        goods.setAll(spu.getTitle()+" "+ StringUtils.join(names," "));

        //price
        //查询price
        List<Sku> skus = this.goodsClient.findSkuBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();

        List<Map<String,Object>> skusList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("image",StringUtils.isBlank(sku.getImages())?"":sku.getImages());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());

            skusList.add(map);
        });
        goods.setPrice(prices);

        //skus

        goods.setSkus(JsonUtils.serialize(skusList));

        //specs
        HashMap<String, Object> specs = new HashMap<>();

        List<SpecParam> specParams = this.specClient.querySpecParamsByGid(null, spu.getCid3(), true, null);

        SpuDetail spuDetail = this.goodsClient.findSpuDetailById(spu.getId());

        //解析通过规格参数
        Map<Long, Object> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, Object.class);

        //解析特有规格参数
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(),
                new TypeReference<Map<Long, List<String>>>() {
                });


        for (SpecParam param : specParams) {
            Long paramId = param.getId();
            String name = param.getName();
            //通用参数
            Object value = null;
            if (param.getGeneric()) {
                //通用参数
                value = genericSpec.get(paramId);

                if (param.getNumeric()) {
                    //数值类型需要加分段
                    value = this.chooseSegment(value.toString(), param);
                }
            } else {//特有参数
                value = specialSpec.get(paramId);

            }
            if (null == value) {
                value = "其他";
            }
            specs.put(name,value);
        }
        goods.setSpecs(specs);


        return goods;
    }
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public void createIndex(Long id){
        Spu spu = this.goodsClient.findSpuById(id);
        SpuBo spuBo = new SpuBo();
        BeanUtils.copyProperties(spu,spuBo);
        Goods goods = this.buildGoods(spuBo);
        this.goodsRepository.save(goods);
    }

    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }
}
