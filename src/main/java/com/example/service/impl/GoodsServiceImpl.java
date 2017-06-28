package com.example.service.impl;

import com.example.persistence.redis.dto.GoodsListDto;
import com.example.persistence.redis.repository.GoodsListDtoRepository;
import com.example.service.GoodsService;
import com.example.service.dto.Goods;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestTemplate restTemplate;
    private final String goodsServiceUrl;
    private final GoodsListDtoRepository goodsListDtoRepository;

    public GoodsServiceImpl(@LoadBalanced RestTemplate restTemplate,
                            @Value("${goods.url}") String goodsServiceUrl,
                            GoodsListDtoRepository goodsListDtoRepository) {
        this.restTemplate = restTemplate;
        this.goodsServiceUrl = goodsServiceUrl;
        this.goodsListDtoRepository = goodsListDtoRepository;
    }

    @HystrixCommand(fallbackMethod = "getDefaultGoods")
    @Override
    public List<Goods> findAll() {
        // goods-serviceから商品データを取得
        RequestEntity<Void> requestEntity = RequestEntity.get(URI.create(goodsServiceUrl)).build();
        ResponseEntity<List<Goods>> responseEntity =
                restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<Goods>>() {});
        List<Goods> goodsList = responseEntity.getBody();
        // Redisにキャッシュする
        GoodsListDto goodsListDto = new GoodsListDto(goodsList);
        goodsListDtoRepository.save(goodsListDto);
        // 戻り値を返す
        return goodsList;
    }

    /**
     * findAll()のフォールバックメソッド。
     * goods-serviceから前回取得した値を、Redisキャッシュから取得する。
     */
    public List<Goods> getDefaultGoods(Throwable throwable) {
        logger.error("商品取得処理でフォールバックしました", throwable);
        GoodsListDto goodsListDto = goodsListDtoRepository.findOne(GoodsListDto.ID);
        List<Goods> goodsList = goodsListDto.getGoodsList();
        if (goodsList != null) {
            return goodsList;
        } else {
            return Collections.emptyList();
        }
    }
}
