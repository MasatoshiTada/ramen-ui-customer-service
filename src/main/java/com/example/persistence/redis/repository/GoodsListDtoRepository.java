package com.example.persistence.redis.repository;

import com.example.persistence.redis.dto.GoodsListDto;
import org.springframework.data.repository.CrudRepository;

public interface GoodsListDtoRepository extends CrudRepository<GoodsListDto, String> {
}
