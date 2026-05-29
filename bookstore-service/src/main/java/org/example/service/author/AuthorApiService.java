package org.example.service.author;

import lombok.RequiredArgsConstructor;
import org.example.database.AuthorRepository;
import org.example.mapper.AuthorMapper;
import org.example.model.Author;
import org.example.model.AuthorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorApiService implements AuthorApiInterface {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    public Author getAuthorById(Integer authorId) {
        AuthorEntity author = authorRepository.findById(authorId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found"
        ));
        return authorMapper.toDto(author);
    }

    @Override
    public List<Author> getPopularAuthors(Integer limit, Integer offset) {
        List<AuthorEntity> authors = authorRepository.findAll();

        Comparator<AuthorEntity> comparator;

        return authors.stream()
                .map(authorMapper::toDto)
                .skip(offset)
                .limit(limit)
                .toList();
    }
}
