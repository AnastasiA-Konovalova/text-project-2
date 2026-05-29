package org.example.service.author;

import org.example.model.Author;

import java.util.List;

public interface AuthorApiInterface {

    Author getAuthorById(Integer authorId);

    List<Author> getPopularAuthors(Integer limit, Integer offset);
}
