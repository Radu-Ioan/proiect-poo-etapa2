package factory;

import entities.Consumer;
import entities.Distributor;
import entities.Producer;
import input.ConsumerInput;
import input.DistributorInput;
import input.ProducerInput;
import utils.Utils;

import javax.naming.NameNotFoundException;

public final class Factory {

    private Factory() { }

    private static Factory instance;

    /**
     * @return instanta de factory care creeaza entitati folosind datele din
     * input
     */
    public static Factory getInstance() {
        if (instance == null) {
            instance = new Factory();
        }
        return instance;
    }

    /**
     * @param data contine informatiile despre un distribuitor primite din
     * input
     * @return un obiect pentru un distribuitor folosind data
     */
    public Distributor createDistributor(final DistributorInput data) {
        Distributor distributor = new Distributor();
        distributor.setId(data.getId());
        distributor.setEnergyNeededKW(data.getEnergyNeededKW());
        distributor.setBudget(data.getInitialBudget());
        try {
            distributor.setProducerStrategy(Utils.stringToChoiceStrategy(data
                    .getProducerStrategy()));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        distributor.setBankrupt(false);
        distributor.setInfrastructureCost(data.getInitialInfrastructureCost());
        distributor.setContractLength(data.getContractLength());
        return distributor;
    }

    /**
     * @param data contine informatiile despre un consumator din input
     * @return un obiect consumer folosind data
     */
    public Consumer createConsumer(final ConsumerInput data) {
        Consumer consumer = new Consumer();
        consumer.setId(data.getId());
        consumer.setBankrupt(false);
        consumer.setBudget(data.getInitialBudget());
        consumer.setMonthlyIncome(data.getMonthlyIncome());
        consumer.setContract(null);
        return consumer;
    }

    /**
     * @param data contine informatiile despre un producator din input
     * @return un obiect producator folosind data
     */
    public Producer createProducer(final ProducerInput data) {
        Producer producer = new Producer();
        producer.setId(data.getId());
        producer.setMaxDistributors(data.getMaxDistributors());
        producer.setPriceKW(data.getPriceKW());
        try {
            producer.setEnergyType(Utils.stringToEnergyType(data
                    .getEnergyType()));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        producer.setEnergyPerDistributor(data.getEnergyPerDistributor());
        return producer;
    }
}
