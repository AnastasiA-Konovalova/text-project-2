package org.example.mapper;

import lombok.RequiredArgsConstructor;
import org.example.model.Basket;
import org.example.model.BasketDetailEntity;
import org.example.model.BasketEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class BasketMapper {

    private final BasketDetailMapper basketDetailMapper;

    public Basket toDto(BasketEntity basket) {
        Basket dto = new Basket();

        dto.setId(basket.getId());
        dto.setBasketDetails(basket.getBasketDetails()
                .stream()
                .map(basketDetailMapper::toDto)
                .toList());
        int quantityBooks = basket.getBasketDetails()
                .stream()
                .mapToInt(BasketDetailEntity::getQuantity)
                .sum();
        dto.setQuantityBooks(quantityBooks);
        dto.setTotalPrice(basket.getTotalPrice());
        dto.setCreatedAt(basket.getCreatedAt().atOffset(ZoneOffset.UTC));
        dto.setUpdatedAt(basket.getUpdatedAt().atOffset(ZoneOffset.UTC));

        return dto;
    }

}
