package com.orange.credicard.validation;

import com.orange.credicard.proposal.ProposalCreateForm;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class ProposalCreateFormValidator implements DefaultGroupSequenceProvider<ProposalCreateForm> {

    @Override
    public List<Class<?>> getValidationGroups(ProposalCreateForm form) {
        List<Class<?>> groups = new ArrayList<>();

        if (hasPersonType(form)) {
            if (form.isPF()) {
                groups.add(PhysicalPersonGroup.class);
            } else if (form.isPJ()) {
                groups.add(LegalPersonGroup.class);
            }
        }

        groups.add(ProposalCreateForm.class);

        return groups;
    }

    private boolean hasPersonType(ProposalCreateForm form) {
        return nonNull(form) && nonNull(form.getPersonType());
    }
}
