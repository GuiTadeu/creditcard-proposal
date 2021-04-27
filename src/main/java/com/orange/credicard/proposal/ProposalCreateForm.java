package com.orange.credicard.proposal;

import com.orange.credicard.validation.LegalPersonGroup;
import com.orange.credicard.validation.PhysicalPersonGroup;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.group.GroupSequenceProvider;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

import static com.orange.credicard.proposal.PersonType.PF;
import static com.orange.credicard.proposal.PersonType.PJ;

@GroupSequenceProvider(value = ProposalCreateFormValidator.class)
public class ProposalCreateForm {

    @NotBlank
    @CPF(groups = PhysicalPersonGroup.class)
    @CNPJ(groups = LegalPersonGroup.class)
    private String document;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private Address address;

    @NotNull
    @Positive
    private BigDecimal salary;

    @NotNull
    private PersonType personType;

    public ProposalCreateForm(@NotBlank String document, @NotBlank String name, @Email @NotBlank String email,
                              @NotNull Address address, @NotNull @Positive BigDecimal salary, @NotNull PersonType personType) {
        this.document = document;
        this.name = name;
        this.email = email;
        this.address = address;
        this.salary = salary;
        this.personType = personType;
    }

    public Proposal toModel() {
        return new Proposal(document, name, email, address, salary, personType);
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    public boolean isPF() {
        return PF.equals(personType);
    }

    public boolean isPJ() {
        return PJ.equals(personType);
    }
}
