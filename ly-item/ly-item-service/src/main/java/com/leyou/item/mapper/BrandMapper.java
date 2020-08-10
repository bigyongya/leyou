package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    @Select("delete from tb_category_brand where brand_id = #{bid}")
    void deleteBrandByBid(@Param("bid") Long bid);

    @Select("insert into tb_category_brand (category_id, brand_id) values(#{cid},#{bid})")
    void insertBrandAndCategory(@Param("bid") Long id, @Param("cid") Long cid);


    @Select("delete from tb_category_brand where brand_id = #{bid}")
    void deleteBrandAndCategory(@Param("bid") Long id);

    @Select("select b.* from tb_brand b left join tb_category_brand cb on b.id=cb.brand_id where cb.category_id = #{cid}")
    List<Brand> queryBrandByCid(@Param("cid") Long cid);
}
