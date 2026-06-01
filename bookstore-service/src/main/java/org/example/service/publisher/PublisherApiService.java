package org.example.service.publisher;

import lombok.RequiredArgsConstructor;
import org.example.database.PublisherRepository;
import org.example.exeception.ClassNotFoundException;
import org.example.mapper.PublisherMapper;
import org.example.model.Publisher;
import org.example.model.PublisherEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublisherApiService implements PublisherApiInterface {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    @Override
    public Publisher getPublisherById(Integer publisherId) {
        PublisherEntity publisher = existPublisherEntity(publisherId);

        return publisherMapper.toDto(publisher);
    }

    private PublisherEntity existPublisherEntity(Integer publisherId) {
        return publisherRepository.findById(publisherId).orElseThrow(() -> new ClassNotFoundException("Publisher not found"));
    }
}