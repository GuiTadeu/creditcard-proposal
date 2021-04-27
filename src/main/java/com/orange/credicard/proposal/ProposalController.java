package com.orange.credicard.proposal;

import com.orange.credicard.service.solicitation.AnalysisServiceResponse;
import com.orange.credicard.service.solicitation.AnalysisStatusCode;
import com.orange.credicard.service.solicitation.ProposalAnalysisClient;
import com.orange.credicard.service.solicitation.ProposalAnalysisForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.BindException;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/proposals")
public class ProposalController {

    private final ProposalRepository proposalRepository;
    private final ProposalAnalysisClient analysisClient;

    public ProposalController(ProposalRepository proposalRepository, ProposalAnalysisClient analysisClient) {
        this.proposalRepository = proposalRepository;
        this.analysisClient = analysisClient;
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

    @PostMapping("/analysis")
    public ResponseEntity<?> analysis(@RequestBody @Valid ProposalAnalysisForm form) throws Exception {
        Proposal proposal = proposalRepository.findById(form.getIdProposta()).orElseThrow(BindException::new);
        AnalysisServiceResponse serviceResponse = analysisClient.analysis(form);
        return getConvertedStatusServiceResponse(proposal, serviceResponse);
    }

    private ResponseEntity<?> getConvertedStatusServiceResponse(Proposal proposal, AnalysisServiceResponse serviceResponse) {
        var analysisStatusCode = AnalysisStatusCode.valueOf(serviceResponse.getResultadoSolicitacao());
        proposal.setStatus(analysisStatusCode.getConvertedStatus());
        return ResponseEntity.status(analysisStatusCode.getHttpStatusCode()).build();
    }
}