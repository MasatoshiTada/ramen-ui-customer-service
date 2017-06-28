package com.example.persistence.redis.dto;

import com.example.service.dto.Goods;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash(value = "goods_list")
public class GoodsListDto {

    public static final String ID = "GoodsListDtoID";

    @Id
    private String id = ID;

    private List<Goods> goodsList;

    public GoodsListDto() {}

    public GoodsListDto(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }
}
