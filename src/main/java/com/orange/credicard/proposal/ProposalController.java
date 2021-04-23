package com.orange.credicard.proposal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/proposals")
public class ProposalController {

    private final ProposalRepository proposalRepository;

    public ProposalController(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid ProposalCreateForm form, UriComponentsBuilder uriBuilder) {

        Optional<Proposal> possibleProposal = proposalRepository.findByDocument(form.getDocument());
        if(possibleProposal.isPresent()) {
            return ResponseEntity.status(422).build();
        }

        Proposal proposal = form.toModel();
        Proposal savedProposal = proposalRepository.save(proposal);

        URI uri = uriBuilder.path("/proposals/{id}").buildAndExpand(savedProposal.getId()).toUri();
        return ResponseEntity.created(uri).body(new ProposalCreateDTO(savedProposal));
    }
}
