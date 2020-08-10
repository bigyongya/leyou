package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuBo;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {

    @GetMapping("spu/sku/list")
    List<Sku> findSkuBySpuId(@RequestParam("id")Long spuId);

    @GetMapping("spu/detail/{id}")
    SpuDetail findSpuDetailById(@PathVariable("id")Long id);

    @GetMapping("spu/page")
    PageResult<SpuBo> querySpu(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")int page,
            @RequestParam(value = "rows",defaultValue = "5")int rows
    );

    @GetMapping("spu/spu/{id}")
     Spu findSpuById(@PathVariable("id")Long id);
}
