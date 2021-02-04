package entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public final class Contract {
    private int consumerId;
    private int price;
    private int remainedContractMonths;
    @JsonIgnore
    private Distributor paymentDestination;

    public Contract(final int consumerId, final int price,
                    final int remainedContractMonths,
                    final Distributor distributor) {
        this.consumerId = consumerId;
        this.price = price;
        this.remainedContractMonths = remainedContractMonths;
        paymentDestination = distributor;
    }

    /**
     * Actualizeaza numarul de luni ramase din contract
     */
    public void decreaseValidity() {
        --remainedContractMonths;
    }

    public int getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(final int consumerId) {
        this.consumerId = consumerId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public int getRemainedContractMonths() {
        return remainedContractMonths;
    }

    public void setRemainedContractMonths(final int remainedContractMonths) {
        this.remainedContractMonths = remainedContractMonths;
    }

    @JsonIgnore
    public Distributor getPaymentDestination() {
        return paymentDestination;
    }

    @JsonIgnore
    public void setPaymentDestination(final Distributor paymentDestination) {
        this.paymentDestination = paymentDestination;
    }
}
