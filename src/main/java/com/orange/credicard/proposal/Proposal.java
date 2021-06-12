package com.orange.credicard.proposal;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String encryptDocument;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotNull
    @ManyToOne
    private Address address;

    @NotNull
    @Positive
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @Enumerated(EnumType.STRING)
    private ProposalStatus status = ProposalStatus.CRIADO;

    public Proposal() {
    }

    public Proposal(@NotBlank String plainDocument, @NotBlank String name,
                    @Email @NotBlank String email, @NotNull Address address,
                    @NotNull @Positive BigDecimal salary, PersonType personType) {
        this.encryptDocument = DocumentEncode.simpleEncode(plainDocument);
        this.name = name;
        this.email = email;
        this.address = address;
        this.salary = salary;
        this.personType = personType;
    }

    public Long getId() {
        return id;
    }

    public String getEncryptDocument() {
        return encryptDocument;
    }

    public String getDecryptDocument() {
        return DocumentEncode.simpleDecode(encryptDocument);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public ProposalStatus getStatus() {
        return status;
    }

    public void setStatus(ProposalStatus status) {
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposal proposal = (Proposal) o;
        return encryptDocument.equals(proposal.encryptDocument) && email.equals(proposal.email)
                && address.equals(proposal.address) && salary.equals(proposal.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encryptDocument, email, address, salary);
    }
}
