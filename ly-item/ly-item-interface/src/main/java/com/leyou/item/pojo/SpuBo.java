package com.leyou.item.pojo;

import javax.persistence.Transient;
import java.util.List;

public class SpuBo extends Spu {

    @Transient
    String cname;// 商品分类名称

    @Transient
    String bname;// 品牌名称

    @Transient
    List<Sku> skus;

    @Transient
    SpuDetail spuDetail;



    public String getCname() {
        return cname;
    }


    public void setCname(String cname) {
        this.cname = cname;
    }


    public String getBname() {
        return bname;
    }


    public void setBname(String bname) {
        this.bname = bname;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public SpuDetail getSpuDetail() {
        return spuDetail;
    }

    public void setSpuDetail(SpuDetail spuDetail) {
        this.spuDetail = spuDetail;
    }
}