package com.orange.credicard.proposal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.credicard.service.solicitation.AnalysisResponseDTO;
import com.orange.credicard.service.solicitation.AnalysisStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.BindException;
import java.net.URI;
import java.util.Optional;

import static com.orange.credicard.service.solicitation.AnalysisEndpoints.POST_SOLICITATION;

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

    @PostMapping("/analysis")
    public ResponseEntity<?> analysis(@RequestBody @Valid ProposalAnalysisForm form, RestTemplate restTemplate) throws Exception {
        Proposal proposal = proposalRepository.findById(form.getIdProposta()).orElseThrow(BindException::new);

        AnalysisResponseDTO serviceResponse;
        try {
            serviceResponse = restTemplate.postForEntity(POST_SOLICITATION, form, AnalysisResponseDTO.class).getBody();
        } catch (HttpClientErrorException clientErrorException) {
            serviceResponse = new ObjectMapper().readValue(clientErrorException.getResponseBodyAsString(), AnalysisResponseDTO.class);
            return getConvertedStatusServiceResponse(proposal, serviceResponse);
        }

        return getConvertedStatusServiceResponse(proposal, serviceResponse);
    }

    private ResponseEntity<?> getConvertedStatusServiceResponse(Proposal proposal, AnalysisResponseDTO serviceResponse) {
        var analysisStatusCode = AnalysisStatusCode.valueOf(serviceResponse.getResultadoSolicitacao());
        proposal.setStatus(analysisStatusCode.getConvertedStatus());
        return ResponseEntity.status(analysisStatusCode.getHttpStatusCode()).build();
    }
}