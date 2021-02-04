package input;

import java.util.List;

public final class Update {
    private List<ConsumerInput> newConsumers;
    private List<DistributorChange> distributorChanges;
    private List<ProducerChange> producerChanges;

    public List<ConsumerInput> getNewConsumers() {
        return newConsumers;
    }

    public void setNewConsumers(final List<ConsumerInput> newConsumers) {
        this.newConsumers = newConsumers;
    }

    public List<DistributorChange> getDistributorChanges() {
        return distributorChanges;
    }

    public void setDistributorChanges(final List<DistributorChange>
                                              distributorChanges) {
        this.distributorChanges = distributorChanges;
    }

    public List<ProducerChange> getProducerChanges() {
        return producerChanges;
    }

    public void setProducerChanges(final List<ProducerChange> producerChanges) {
        this.producerChanges = producerChanges;
    }
}
