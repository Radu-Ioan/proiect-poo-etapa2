package factory;

import entities.Consumer;
import entities.Distributor;
import entities.Producer;

import java.util.List;

/**
 * Obiectul json scris in fisierele de out
 */
public final class Result {
    private List<Consumer> consumers;
    private List<Distributor> distributors;
    private List<Producer> energyProducers;

    public Result(final List<Consumer> consumers,
                  final List<Distributor> distributors,
                  final List<Producer> producers) {
        this.consumers = consumers;
        this.distributors = distributors;
        energyProducers = producers;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(final List<Consumer> consumers) {
        this.consumers = consumers;
    }

    public List<Distributor> getDistributors() {
        return distributors;
    }

    public void setDistributors(final List<Distributor> distributors) {
        this.distributors = distributors;
    }

    public List<Producer> getEnergyProducers() {
        return energyProducers;
    }

    public void setEnergyProducers(final List<Producer> energyProducers) {
        this.energyProducers = energyProducers;
    }
}
