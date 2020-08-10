package com.leyou.serach.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {

}
