package org.example.mapper;

import jakarta.persistence.Column;
import org.example.model.BasketDetail;
import org.example.model.BasketDetailEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BasketDetailMapper {

    public BasketDetail toDto(BasketDetailEntity basketDetail) {
        BasketDetail dto = new BasketDetail();
        dto.setId(basketDetail.getId());
        dto.setBookId(basketDetail.getBook().getId());
        dto.setPrice(basketDetail.getBook().getPrice());
        dto.setQuantity(basketDetail.getQuantity());
        dto.setTitle(basketDetail.getBook().getTitle());
        dto.setTotalPrice(
                basketDetail.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(basketDetail.getQuantity()))
        );

        return dto;
    }


}
