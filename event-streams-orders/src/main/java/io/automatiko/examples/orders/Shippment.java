package io.automatiko.examples.orders;

import java.util.Date;

public class Shippment {

    private String shippmentId;

    private Date expectedDeliveryDate;

    public Shippment() {

    }

    public Shippment(String shippmentId, Date expectedDeliveryDate) {
        this.shippmentId = shippmentId;
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public String getShippmentId() {
        return shippmentId;
    }

    public void setShippmentId(String shippmentId) {
        this.shippmentId = shippmentId;
    }

    public Date getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((shippmentId == null) ? 0 : shippmentId.hashCode());
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
        Shippment other = (Shippment) obj;
        if (shippmentId == null) {
            if (other.shippmentId != null)
                return false;
        } else if (!shippmentId.equals(other.shippmentId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Shippment [shippmentId=" + shippmentId + ", expectedDeliveryDate=" + expectedDeliveryDate + "]";
    }

}
