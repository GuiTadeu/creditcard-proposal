package com.orange.credicard.proposal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank private String street;
    @NotBlank private String number;
    @NotBlank private String postalCode;
    @NotBlank private String city;
    @NotBlank private String state;

    public Address(@NotBlank String street, @NotBlank String number,
                   @NotBlank String postalCode, @NotBlank String city, @NotBlank String state) {
        this.street = street;
        this.number = number;
        this.postalCode = postalCode;
        this.city = city;
        this.state = state;
    }

    @Deprecated
    public Address() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return street.equals(address.street) && number.equals(address.number)
                && postalCode.equals(address.postalCode) && city.equals(address.city) && state.equals(address.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, number, postalCode, city, state);
    }
}
