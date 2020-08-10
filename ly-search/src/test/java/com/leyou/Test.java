package com.leyou;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.SpuBo;

import com.leyou.serach.client.GoodsClient;
import com.leyou.serach.pojo.Goods;
import com.leyou.serach.repository.GoodsRepository;
import com.leyou.serach.service.GoodsService;
import com.netflix.discovery.converters.Auto;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class Test {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @org.junit.Test
    public void test(){
        // 创建索引
        this.elasticsearchTemplate.createIndex(Goods.class);
        // 配置映射
        this.elasticsearchTemplate.putMapping(Goods.class);
    }

    @org.junit.Test
    public void test2(){
        int i = 1;
        while (true) {
            PageResult<SpuBo> spuBos = goodsClient.querySpu(null, true, i, 50);
            if(spuBos==null){
                break;
            }
            List<Goods> goods = new ArrayList<>();
            spuBos.getItems().forEach(spuBo -> {
                Goods good = goodsService.buildGoods(spuBo);
                goods.add(good);
            });
            this.goodsRepository.saveAll(goods);
            i++;
        }
    }
}
