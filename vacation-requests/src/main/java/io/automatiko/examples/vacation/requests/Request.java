package io.automatiko.examples.vacation.requests;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Request extends PanacheEntity {

    @NotNull
    @Column(name = "START_FROM")
    public Date from;

    @NotNull
    @Column(name = "END_AT")
    public Date to;

    @NotNull
    @Min(value = 1, message = "Vacation request must be at least one day")
    @Column(name = "VACATION_LENGTH")
    public int length;

    @Column(name = "ADDITIONAL_INFO")
    public String comment;

    @NotNull
    @Column(name = "REQ_KEY")
    public String key;

    public Boolean approved = Boolean.FALSE;

    public Boolean cancelled = Boolean.FALSE;

    @Override
    public String toString() {
        return "Request [from=" + from + ", to=" + to + ", length=" + length + ", comment=" + comment + ", approved=" + approved
                + ", cancelled=" + cancelled + "]";
    }

}
