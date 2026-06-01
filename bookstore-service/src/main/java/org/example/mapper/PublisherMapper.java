package org.example.mapper;

import org.example.model.Publisher;
import org.example.model.PublisherEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class PublisherMapper {

    public Publisher toDto(PublisherEntity publisher) {
        Publisher dto = new Publisher();

        dto.setId(publisher.getId());
        dto.setName(publisher.getName());
        dto.setCountry(publisher.getCountry());
        dto.setDescription(publisher.getDescription());
        dto.setCreatedAt(publisher.getCreatedAt().atOffset(ZoneOffset.UTC));
        dto.setCreatedAt(publisher.getUpdatedAt().atOffset(ZoneOffset.UTC));

        return dto;
    }
}