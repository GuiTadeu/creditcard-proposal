package com.orange.credicard.proposal;

import com.orange.credicard.card.CardRepository;
import com.orange.credicard.exception.NotFoundException;
import com.orange.credicard.service.accounts.AccountsClient;
import com.orange.credicard.service.analysis.AnalysisResponse;
import com.orange.credicard.service.analysis.AnalysisStatusCode;
import com.orange.credicard.service.analysis.AnalysisClient;
import com.orange.credicard.service.analysis.AnalysisRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.BindException;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/proposals")
public class ProposalController {

    private final AnalysisClient analysisClient;
    private final AccountsClient accountsClient;
    private final CardRepository cardRepository;
    private final ProposalRepository proposalRepository;
    private final AddressRepository addressRepository;

    public ProposalController(AnalysisClient analysisClient, AccountsClient accountsClient, CardRepository cardRepository,
                              ProposalRepository proposalRepository, AddressRepository addressRepository) {
        this.analysisClient = analysisClient;
        this.accountsClient = accountsClient;
        this.cardRepository = cardRepository;
        this.proposalRepository = proposalRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid ProposalCreateForm form, UriComponentsBuilder uriBuilder) {

        Optional<Proposal> possibleProposal = proposalRepository.findByDocument(form.getDocument());
        if (possibleProposal.isPresent()) {
            return ResponseEntity.status(422).build();
        }

        addressRepository.save(form.getAddress());

        Proposal proposal = form.toModel();
        Proposal savedProposal = proposalRepository.save(proposal);

        URI uri = uriBuilder.path("/proposals/{id}").buildAndExpand(savedProposal.getId()).toUri();
        return ResponseEntity.created(uri).body(new ProposalCreateDTO(savedProposal));
    }

    @PostMapping("/analysis")
    public ResponseEntity<?> analysis(@RequestBody @Valid AnalysisRequest form) throws Exception {
        Proposal proposal = proposalRepository.findById(form.getIdProposta()).orElseThrow(BindException::new);
        AnalysisResponse serviceResponse = analysisClient.analysis(form);
        return getConvertedStatusServiceResponse(proposal, serviceResponse);
    }

    private ResponseEntity<?> getConvertedStatusServiceResponse(Proposal proposal, AnalysisResponse serviceResponse) {
        var analysisStatusCode = AnalysisStatusCode.valueOf(serviceResponse.getResultadoSolicitacao());

        proposal.setStatus(analysisStatusCode.getConvertedStatus());
        proposalRepository.save(proposal);

        return ResponseEntity.status(analysisStatusCode.getHttpStatusCode()).build();
    }

    @GetMapping("/status/{proposalId}")
    public ResponseEntity<ProposalSituationDTO> status(@PathVariable Long proposalId) throws Exception {
        Proposal proposal = proposalRepository.findById(proposalId).orElseThrow(NotFoundException::new);
        return ResponseEntity.ok(new ProposalSituationDTO(proposal));
    }
}