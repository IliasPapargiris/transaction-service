package com.okto.transactionservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "country_supported_currencies", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"country_code", "currency_code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountrySupportedCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_code")
    private Country country;

    @ManyToOne(optional = false)
    @JoinColumn(name = "currency_code")
    private Currency currency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CountrySupportedCurrency that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
