package org.example.service.author;

import lombok.RequiredArgsConstructor;
import org.example.database.AuthorRepository;
import org.example.exception.NotFoundException;
import org.example.mapper.AuthorMapper;
import org.example.model.Author;
import org.example.model.AuthorEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorApiService implements AuthorApiInterface {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    public Author getAuthorById(Integer authorId) {
        AuthorEntity author = existAuthorEntity(authorId);
        return authorMapper.toDto(author);
    }

    @Override
    public List<Author> getPopularAuthors(Integer limit, Integer offset) {
        List<AuthorEntity> authors = authorRepository.findAll();

        //Comparator<AuthorEntity> comparator;
        return authors.stream()
                .map(authorMapper::toDto)
                .skip(offset)
                .limit(limit)
                .toList();
    }

    private AuthorEntity existAuthorEntity(Integer authorId) {
        return authorRepository.findById(authorId).orElseThrow(() -> new NotFoundException("Author not found"));
    }
}