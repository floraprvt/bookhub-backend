package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Author;
import fr.eni.bookhubbackend.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auteur non trouvé"));
    }

    public Author create(Author author) {
        return authorRepository.save(author);
    }

    public Author update(Long id, Author author) {
        if (!authorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auteur non trouvé");
        }
        author.setId(id);
        return authorRepository.save(author);
    }

    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auteur non trouvé");
        }
        authorRepository.deleteById(id);
    }
}