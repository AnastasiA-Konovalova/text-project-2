package org.example.mapper;

import org.example.model.PublisherSeries;
import org.example.model.PublisherSeriesEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class PublisherSeriesMapper {

    public PublisherSeries toDto(PublisherSeriesEntity entity) {
        if (entity == null) return null;
        PublisherSeries dto = new PublisherSeries();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPublisherId(entity.getPublisherId());
        dto.setCreatedAt(entity.getCreatedAt().atOffset(ZoneOffset.UTC));
        dto.setCreatedAt(entity.getUpdatedAt().atOffset(ZoneOffset.UTC));

        return dto;
    }
}
