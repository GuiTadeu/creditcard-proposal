package com.orange.credicard.biometry;

import com.orange.credicard.card.Card;
import com.orange.credicard.card.CardRepository;
import com.orange.credicard.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/biometrics")
public class BiometryController {

    private final CardRepository cardRepository;
    private final BiometryRepository biometryRepository;

    public BiometryController(CardRepository cardRepository, BiometryRepository biometryRepository) {
        this.cardRepository = cardRepository;
        this.biometryRepository = biometryRepository;
    }

    @PostMapping("/card/{id}")
    public ResponseEntity<?> create(@Valid @RequestBody BiometryCreateForm form, @PathVariable("id") Long cardId, UriComponentsBuilder uriBuilder) {
        Card card = cardRepository.findById(cardId).orElseThrow(NotFoundException::new);
        Biometry savedBiometry = biometryRepository.save(new Biometry(form.getValue(), card));
        URI uri = uriBuilder.path("/biometrics/{bioId}/card/{cardId}").buildAndExpand(savedBiometry.getId(), card.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }
}
