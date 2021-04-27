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

    public boolean approved;

    public boolean cancelled;

    @Override
    public String toString() {
        return "Request [from=" + from + ", to=" + to + ", length=" + length + ", comment=" + comment + ", approved=" + approved
                + ", cancelled=" + cancelled + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Request other = (Request) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

}
