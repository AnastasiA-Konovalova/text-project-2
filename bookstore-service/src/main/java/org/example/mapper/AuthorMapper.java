package org.example.mapper;

import org.example.model.Author;
import org.example.model.AuthorEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author toDto(AuthorEntity author) {
        Author dto = new Author();

        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setSurname(author.getSurname());
        dto.middleName(author.getMiddleName());
        dto.setPseudonym(author.getPseudonym());

        return dto;
    }
}