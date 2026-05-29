package org.example.controller.publisher;

import lombok.RequiredArgsConstructor;
import org.example.api.PublisherApi;
import org.example.model.Publisher;
import org.example.service.publisher.PublisherApiInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublisherApiImpl implements PublisherApi {

    private final PublisherApiInterface publisherInterface;

    @Override
    public ResponseEntity<Publisher> getPublisherById(Integer publisherId) {
        return ResponseEntity.ok(publisherInterface.getPublisherById(publisherId));
    }
}
