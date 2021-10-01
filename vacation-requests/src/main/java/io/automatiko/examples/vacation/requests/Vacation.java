package io.automatiko.examples.vacation.requests;

import java.math.BigDecimal;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Vacation extends PanacheEntity {

    public BigDecimal eligible;

    public int used;

    @Override
    public String toString() {
        return "Vacation [eligible=" + eligible + ", used=" + used + "]";
    }

}
