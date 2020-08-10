package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.GoodsMapper;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    Logger logger = LoggerFactory.getLogger(GoodsService.class);


    public PageResult<SpuBo> querySpus(String key, Boolean saleable, int page, int rows) {
        // 开始分页
        PageHelper.startPage(page, rows);
        // 过滤
        Example example = new Example(Spu.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().andLike("title", "%" + key + "%");
        }
        if(saleable!=null){
            example.createCriteria().orEqualTo("saleable",saleable);
        }
        Page<Spu> pageInfo = (Page<Spu>)this.goodsMapper.selectByExample(example);

        /*List<Spu> result = pageInfo.getResult();
        for (Spu spu : result) {
            List<String> names = this.categoryService.queryNameByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
            spu.setCname(StringUtils.join(names,"/"));

            spu.setBname(this.brandService.queryBrandById(spu.getBrandId()).getName());
        }*/

        List<SpuBo> list = pageInfo.getResult().stream().map(spu -> {
            // 把spu变为 spuBo
            SpuBo spuBo = new SpuBo();
            // 属性拷贝
            BeanUtils.copyProperties(spu, spuBo);

            // 2、查询spu的商品分类名称,要查三级分类
            List<String> names = this.categoryService.queryNameByIds(
                    Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            // 将分类名称拼接后存入
            spuBo.setCname(StringUtils.join(names, "/"));

            // 3、查询spu的品牌名称
            Brand brand = this.brandService.queryBrandById(spu.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;
        }).collect(Collectors.toList());
        return  new PageResult<>(pageInfo.getTotal(), list);
    }

    @Transactional
    public void saveGoods(SpuBo spuBo) {

        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuBo.setValid(true);
        spuBo.setSaleable(true);

        //保存spu
        this.goodsMapper.insertSelective(spuBo);

        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        //保存spuDetail
        this.spuDetailMapper.insertSelective(spuDetail);

        List<Sku> skus = spuBo.getSkus();
        skus.forEach(sku -> {
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());

            //保存sku
            this.skuMapper.insertSelective(sku);

            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            //保存stock库存
            this.stockMapper.insertSelective(stock);
        });

        sendMessage(spuBo.getId(),"insert");
    }

    public SpuDetail findSpuDetailById(Long id) {
        return this.spuDetailMapper.selectByPrimaryKey(id);
    }

    public List<Sku> findSkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(sku);
        skus.forEach(sku1 -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(sku1.getId());
            sku1.setStock(stock.getStock());
        });

        return skus;
    }

    @Transactional
    public void updateGoods(SpuBo spuBo) {

        //修改最后修改时间
        spuBo.setLastUpdateTime(new Date());
        //修改spu
        this.goodsMapper.updateByPrimaryKeySelective(spuBo);

        //修改spuDetail
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        List<Sku> newSkus = spuBo.getSkus();

        List<Sku> oldSkus = this.findSkuBySpuId(spuBo.getId());

        for (Sku newSku : newSkus) {
            if(newSku.getId()==null){
                newSku.setSpuId(spuBo.getId());
                newSku.setCreateTime(new Date());
                newSku.setLastUpdateTime(newSku.getCreateTime());
                //保存新增的sku
                this.skuMapper.insertSelective(newSku);

                Stock stock = new Stock();
                stock.setSkuId(newSku.getId());
                stock.setStock(newSku.getStock());
                //保存库存stock
                this.stockMapper.insertSelective(stock);
            }else{

                newSku.setLastUpdateTime(new Date());
                newSku.setSpuId(spuBo.getId());
                //修改sku
                this.skuMapper.updateByPrimaryKeySelective(newSku);

                Stock stock = new Stock();
                stock.setSkuId(newSku.getId());
                stock.setStock(newSku.getStock());
                //修改库存
                this.stockMapper.updateByPrimaryKeySelective(stock);

                oldSkus.removeIf(oldSku -> newSku.getId().equals(oldSku.getId()));


            }
        }


            oldSkus.forEach(oldSku ->{
                oldSku.setEnable(false);
                oldSku.setLastUpdateTime(new Date());
                this.skuMapper.updateByPrimaryKeySelective(oldSku);
            });

        sendMessage(spuBo.getId(),"update");
    }

    @Transactional
    public void updateGoodsSoldOutById(Long id,Boolean saleable) {
        Spu spu = this.goodsMapper.selectByPrimaryKey(id);
        spu.setSaleable(!saleable);
        spu.setLastUpdateTime(new Date());
        //将spu下架
        this.goodsMapper.updateByPrimaryKeySelective(spu);

        List<Sku> skus = this.findSkuBySpuId(id);
        skus.forEach(sku -> {
            sku.setEnable(!saleable);
            sku.setLastUpdateTime(new Date());
            //将sku下架
            this.skuMapper.updateByPrimaryKeySelective(sku);
        });
    }

    @Transactional
    public void deleteGoods(Long id) {
        List<Sku> skus = this.findSkuBySpuId(id);
        if(skus!=null && skus.size()!=0){
            skus.forEach(sku -> {
                //删除sku
                this.skuMapper.deleteByPrimaryKey(sku);

                Stock stock = new Stock();
                stock.setSkuId(sku.getId());
                //删除stock
                this.stockMapper.deleteByPrimaryKey(stock);
            });
        }

        //删除spuDetail
        this.spuDetailMapper.deleteByPrimaryKey(id);
        //删除spu
        this.goodsMapper.deleteByPrimaryKey(id);

    }

    public Spu findSpuById(Long id) {
        return this.goodsMapper.selectByPrimaryKey(id);
    }

    private void sendMessage(Long id, String type){
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            logger.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }
    }
}
