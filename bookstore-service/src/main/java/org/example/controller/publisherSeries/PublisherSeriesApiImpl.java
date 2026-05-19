package org.example.controller.publisherSeries;

import org.example.api.PublisherSeriesApi;
import org.example.model.PublisherSeries;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublisherSeriesApiImpl implements PublisherSeriesApi {

    @Override
    public ResponseEntity<PublisherSeries> getPublisherSeriesById(Integer publisherSeriesId) {
        PublisherSeries publisherSeries = new PublisherSeries();
        return ResponseEntity.ok(publisherSeries);
    }
}
