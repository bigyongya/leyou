package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuBo;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spu")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("page")
    public ResponseEntity<PageResult<SpuBo>> querySpu(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")int page,
            @RequestParam(value = "rows",defaultValue = "5")int rows
    ){
        PageResult<SpuBo> spus =  this.goodsService.querySpus(key,saleable,page,rows);
        if(spus.getItems()!=null && spus.getItems().size()!=0){
            return ResponseEntity.ok(spus);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        this.goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("detail/{id}")
    public ResponseEntity<SpuDetail> findSpuDetailById(@PathVariable("id")Long id){
        SpuDetail spu = this.goodsService.findSpuDetailById(id);
        if(spu!=null){
            return ResponseEntity.ok(spu);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> findSkuBySpuId(@RequestParam("id")Long spuId){
        List<Sku> skus = this.goodsService.findSkuBySpuId(spuId);
        if(skus!=null && skus.size()!=0){
            return ResponseEntity.ok(skus);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){
        this.goodsService.updateGoods(spuBo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //spu下架
    @PutMapping("soldOut")
    public ResponseEntity<Void> updateGoodsSoldOutById(@RequestParam("id") Long id,@RequestParam("saleable")Boolean saleable){
        this.goodsService.updateGoodsSoldOutById(id,saleable);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //删除spu
    @DeleteMapping("deleteGoods")
    public ResponseEntity<Void> deleteGoods(@RequestParam("id")Long id){
        this.goodsService.deleteGoods(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> findSpuById(@PathVariable("id")Long id){
        Spu spu = this.goodsService.findSpuById(id);
        if (spu != null) {
            return ResponseEntity.ok(spu);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
