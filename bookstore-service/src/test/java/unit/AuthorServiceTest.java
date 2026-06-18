package unit;

import org.example.database.AuthorRepository;
import org.example.mapper.AuthorMapper;
import org.example.model.Author;
import org.example.model.AuthorEntity;
import org.example.service.author.AuthorApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @InjectMocks
    private AuthorApiService authorService;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Test
    void getAuthorById_shouldReturnAuthorDto() {
        Integer authorId = 1;

        AuthorEntity entity = new AuthorEntity();
        entity.setId(authorId);

        Author dto = new Author();
        dto.setId(authorId);

        when(authorRepository.findById(authorId))
                .thenReturn(Optional.of(entity));
        when(authorMapper.toDto(entity))
                .thenReturn(dto);

        Author result = authorService.getAuthorById(authorId);

        assertNotNull(result);
        assertEquals(authorId, result.getId());

        verify(authorRepository).findById(authorId);
        verify(authorMapper).toDto(entity);
    }

    @Test
    void getPopularAuthors_shouldReturnList() {
        AuthorEntity entityOne = new AuthorEntity();
        AuthorEntity entityTwo = new AuthorEntity();

        Author dtoOne = new Author();
        Author dtoTwo = new Author();

        List<AuthorEntity> entities = List.of(entityOne, entityTwo);

        when(authorRepository.findAll())
                .thenReturn(entities);
        when(authorMapper.toDto(entityOne)).thenReturn(dtoOne);
        when(authorMapper.toDto(entityTwo)).thenReturn(dtoTwo);

        List<Author> result = authorService.getPopularAuthors(10, 0);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(authorRepository).findAll();
        verify(authorMapper).toDto(entityOne);
        verify(authorMapper).toDto(entityTwo);
    }
}