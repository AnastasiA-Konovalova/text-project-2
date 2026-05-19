package org.example.controller.publisher;

import org.example.api.PublisherApi;
import org.example.model.Publisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublisherApiImpl implements PublisherApi {

    @Override
    public ResponseEntity<Publisher> getPublisherById(Integer publisherId) {
        Publisher publisher = new Publisher();
        return ResponseEntity.ok(publisher);
    }
}
