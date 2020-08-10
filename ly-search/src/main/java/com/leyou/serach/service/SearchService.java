package com.leyou.serach.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.serach.client.BrandClient;
import com.leyou.serach.client.CategoryClient;
import com.leyou.serach.client.GoodsClient;
import com.leyou.serach.client.SpecClient;
import com.leyou.serach.pojo.Goods;
import com.leyou.serach.pojo.SearchRequest;
import com.leyou.serach.pojo.SearchResult;
import com.leyou.serach.repository.GoodsRepository;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private BrandClient brandClient;
    
    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private GoodsClient goodsClient;

    public PageResult<Goods> page(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if(StringUtils.isBlank(key)){
            // 如果用户没搜索条件，我们可以给默认的，或者返回null
            return null;
        }

        // 1、创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 2、查询
        // 2.1、对结果进行筛选
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"}, null));

        QueryBuilder query = buildBasicQueryWithFilter(searchRequest);
        // 2.2、基本查询
        queryBuilder.withQuery(query);

        // 2.3、分页
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1, searchRequest.getSize()));

        //聚合
        String categoryAggName = "category";

        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));

        String brandAggName = "brand";

        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 3、返回结果
        AggregatedPage<Goods> result = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        List<Long> cids = new ArrayList<>();
        LongTerms categoryTerms = (LongTerms) result.getAggregation(categoryAggName);

        List<LongTerms.Bucket> buckets = categoryTerms.getBuckets();

        for (LongTerms.Bucket bucket : buckets) {
            cids.add(bucket.getKeyAsNumber().longValue());
        }


        List<Long> bids = new ArrayList<>();
        LongTerms brandTerms = (LongTerms) result.getAggregation(brandAggName);
        List<LongTerms.Bucket> brandTermsBuckets = brandTerms.getBuckets();

        for (LongTerms.Bucket brandTermsBucket : brandTermsBuckets) {
            bids.add(brandTermsBucket.getKeyAsNumber().longValue());
        }


        List<Category> categories = new ArrayList<>();
        List<Brand> brands = new ArrayList<>();

        bids.forEach(bid->{
            brands.add(this.brandClient.queryBrandByBid(bid));
        });

        List<String> names = this.categoryClient.queryNameByIds(cids);
        for (int i = 0; i < names.size(); i++) {
            Category c = new Category();
            c.setId(cids.get(i));
            c.setName(names.get(i));
            categories.add(c);
        }

        List<Map<String, Object>> specs = null;

        if(categories!=null && 1==categories.size()){
            specs = getSpecs(categories.get(0).getId(),query);
        }
        return new SearchResult(result.getTotalElements(), new Long(result.getTotalPages()), result.getContent(),categories,brands,specs);
    }

    private List<Map<String, Object>> getSpecs(Long cid,QueryBuilder query) {
        List<Map<String, Object>>  specs = new ArrayList<>();

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder.withQuery(query);

        //所有的可搜索的规格参数
        List<SpecParam> specParams = this.specClient.querySpecParamsByGid(null,cid,true,null);

        specParams.forEach(specParam->{
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));
        });

        //执行查询
        AggregatedPage<Goods> search = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        specParams.forEach(specParam -> {
            String aggName = specParam.getName();

            StringTerms aggregation = (StringTerms) search.getAggregation(aggName);

            Map<String,Object> spec = new HashMap<>();

            spec.put("k",aggName);

            List<String> values = new ArrayList<>();

            List<StringTerms.Bucket> buckets = aggregation.getBuckets();

            for (StringTerms.Bucket bucket : buckets) {
                values.add(bucket.getKeyAsString());
            }

            spec.put("options",values);

            specs.add(spec);
        });
        return specs;
    }

    // 构建基本查询条件
    private QueryBuilder buildBasicQueryWithFilter(SearchRequest request) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 基本查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        // 过滤条件构建器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        // 整理过滤条件
        Map<String, String> filter = request.getFilter();
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 商品分类和品牌可以直接聚合不需要拼接
            if (key != "cid3" && key != "brandId") {
                key = "specs." + key + ".keyword";
            }
            // 字符串类型，进行term查询
            filterQueryBuilder.must(QueryBuilders.termQuery(key, value));
        }
        // 添加过滤条件
        queryBuilder.filter(filterQueryBuilder);
        return queryBuilder;
    }

}
