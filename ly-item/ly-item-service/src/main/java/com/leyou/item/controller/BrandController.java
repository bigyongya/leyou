package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;


    @RequestMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                           @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                                           @RequestParam(value = "sortBy", required = false) String sortBy,
                                                           @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
                                                           @RequestParam(value = "key", required = false) String key){
        PageResult<Brand> pageResult = this.brandService.queryBrandPage(page,rows,sortBy,desc,key);

        if(pageResult.getItems()!=null && pageResult.getItems().size()!=0){
            return ResponseEntity.ok(pageResult);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteBrandById(@PathVariable("id")Long id) {
        this.brandService.deleteBrandById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> insertBrand(Brand brand, @RequestParam("cids")List<Long> cids){
        this.brandService.insertBrand(brand,cids);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand,@RequestParam("cids")List<Long> cids){
        this.brandService.updateBrand(brand,cids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("cid/{id}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("id")Long id){
        List<Brand> brands = this.brandService.queryBrandByCid(id);

        if(brands!=null && brands.size()!=0){
            return ResponseEntity.ok(brands);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("bid/{bid}")
    public ResponseEntity<Brand> queryBrandByBid(@PathVariable("bid")Long id){
        Brand brand = this.brandService.queryBrandByBid(id);

        if(brand!=null){
            return ResponseEntity.ok(brand);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
