package utils;

import entities.Producer;
import entities.Contract;
import entities.Consumer;
import entities.Distributor;
import factory.Factory;
import input.ConsumerInput;
import input.DistributorChange;
import input.DistributorInput;
import input.InitialData;
import input.ProducerChange;
import input.ProducerInput;
import input.Update;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Comparator;
import java.util.PriorityQueue;

public final class BusinessFlow implements Observable {
    private final List<Consumer> consumers = new ArrayList<>();
    /**
     * Obseravatorii acestei clase
     */
    private final List<Distributor> distributors = new ArrayList<>();
    private final List<Producer> producers = new ArrayList<>();
    private final List<Update> monthlyRounds;
    private final HashSet<Integer> bankruptDistributors = new HashSet<>();
    private final Factory factory = Factory.getInstance();
    private static final boolean GAME_OVER = true;
    /**
     * Se foloseste la scrierea in fisierele json pentru producatori
     */
    private static int monthIdentifier;
    /**
     * Colectie cu distribuitorii care sunt anuntati ca producatorii lor au fost
     * modificati
     */
    private PriorityQueue<Distributor> distributorsFollowingToChange;

    public BusinessFlow(final InitialData initialData,
                        final List<Update> rounds) {

        for (ConsumerInput data : initialData.getConsumers()) {
            consumers.add(factory.createConsumer(data));
        }

        for (DistributorInput data : initialData.getDistributors()) {
            addObserver(factory.createDistributor(data));
        }

        for (ProducerInput data : initialData.getProducers()) {
            producers.add(factory.createProducer(data));
        }

        monthlyRounds = rounds;
    }

    @Override
    public void notifyObservers() {
        while (!distributorsFollowingToChange.isEmpty()) {
            distributorsFollowingToChange.poll().update();
        }
    }

    @Override
    public void addObserver(final Observer observer) {
        distributors.add((Distributor) observer);
    }

    /**
     * Simuleaza rundele din fiecare luna
     */
    public void play() {
        monthIdentifier = 0;
        round0();

        for (Update month : monthlyRounds) {
            if (playRound(month) == GAME_OVER) {
                break;
            }
        }
    }

    private void round0() {
        for (Distributor distributor : distributors) {
            distributor.setOtherProducers(producers);
            distributor.setProducers(new ArrayList<>());
            distributor.update();
            distributor.setContractCost();
        }
        Distributor chosenDistributor = chooseDistributor();

        for (Consumer consumer : consumers) {
            if (consumer.isBankrupt()) {
                continue;
            }
            consumer.makeContractWith(chosenDistributor);
            consumer.pay();
        }

        distributors.forEach(Distributor::pay);
    }

    private Distributor chooseDistributor() {
        int minRate = Integer.MAX_VALUE;
        Distributor seller = null;

        for (Distributor d : distributors) {
            if (!d.isBankrupt() && minRate > d.getContractCost()) {
                seller = d;
                minRate = d.getContractCost();
            }
        }

        return seller;
    }

    private boolean playRound(final Update month) {
        // se citesc update-uri
        updateDistributors(month.getDistributorChanges());
        addConsumers(month.getNewConsumers());

        // se calculeaza preturile pentru noile contracte
        Distributor chosenDistributor = chooseDistributor();

        for (Consumer consumer : consumers) {
            ensureContractForConsumer(consumer, chosenDistributor);

            // consumatorii primesc salariu si platesc rata
            consumer.pay();
        }

        // distribuitorii isi platesc cheltuielile
        boolean allDistributorsAreBankrupt = distributorsPayCosts();

        if (allDistributorsAreBankrupt) {
            return GAME_OVER;
        }

        removeBankruptDistributors();

        TreeSet<Producer> changedProducers
                = updateProducers(month.getProducerChanges());
        distributorsFollowingToChange
                = getDistributorsToBeChanged(changedProducers);

        notifyObservers();

        saveProducersMonthlyStats();
        cleanUpContractsOfBankruptConsumers();
        return !GAME_OVER;
    }

    /**
     * Actualizeaza costurile de infrastructura pentru distribuitorii precizati
     * in inputul pentru luna curenta
     * @param changes lista cu schimbarile de facut asupra distribuitorilor
     */
    private void updateDistributors(final List<DistributorChange> changes) {
        for (DistributorChange change : changes) {
            Distributor provider = distributors.get(change.getId());
            if (provider == null) {
                continue;
            }

            provider.setInfrastructureCost(change.getInfrastructureCost());
        }

        distributors.stream().filter(distributor -> !distributor.isBankrupt())
                .forEach(Distributor::setContractCost);
    }

    private void addConsumers(final List<ConsumerInput> newConsumers) {
        if (newConsumers == null) {
            return;
        }
        for (ConsumerInput data : newConsumers) {
            consumers.add(factory.createConsumer(data));
        }
    }

    /**
     * Asigura existenta unuei surse de energie pentru un consumator
     * @param chosenDistributor referinta catre distribuitorul cu cea mai mica
     *                          rata lunara
     */
    private void ensureContractForConsumer(final Consumer consumer,
                                           final Distributor
                                                   chosenDistributor) {
        if (consumer.isBankrupt()) {
            return;
        }

        // consumatorii aleg contracte
        Contract contract = consumer.getContract();

        if (contract != null
                && contract.getRemainedContractMonths() == 0) {
            contract.getPaymentDestination().getContracts().remove(contract);
            consumer.makeContractWith(chosenDistributor);
        } else if (contract == null) {
            consumer.makeContractWith(chosenDistributor);
        }
    }

    private boolean distributorsPayCosts() {
        for (Distributor distributor : distributors) {
            if (!distributor.isBankrupt()) {
                distributor.pay();
            }

            if (distributor.isBankrupt()
                    && !bankruptDistributors.contains(distributor.getId())) {

                bankruptDistributors.add(distributor.getId());
                deleteContracts(distributor.getContracts());

                if (bankruptDistributors.size() == distributors.size()) {
                    return GAME_OVER;
                }
            }
        }
        return !GAME_OVER;
    }

    /**
     * Se sterg distribuitorii care au dat faliment
     */
    private void removeBankruptDistributors() {
        for (int id : bankruptDistributors) {
            Distributor d = distributors.get(id);
            d.getProducers().forEach(producer -> producer.removeDistributor(d));
        }
    }

    private void saveProducersMonthlyStats() {
        ++monthIdentifier;
        producers.forEach(producer -> producer.addStat(monthIdentifier));
    }

    private TreeSet<Producer> updateProducers(
                final List<ProducerChange> updates) {

        TreeSet<Producer> changedProducers = new TreeSet<>(Comparator
                .comparingInt(Producer::getId));

        for (ProducerChange data : updates) {
            producers.get(data.getId())
                    .setEnergyPerDistributor(data.getEnergyPerDistributor());
            changedProducers.add(producers.get(data.getId()));
        }
        return changedProducers;
    }

    private PriorityQueue<Distributor> getDistributorsToBeChanged(
            final TreeSet<Producer> changedProducers) {

        PriorityQueue<Distributor> queue = new PriorityQueue<>(Comparator
                    .comparingInt(Distributor::getId));

        while (!changedProducers.isEmpty()) {
            for (Distributor distributor : changedProducers.pollFirst()
                    .getObservers()) {
                queue.add(distributor);
            }
        }
        return queue;
    }

    /**
     * Se reziliaza contractele consumatorilor care au dat faliment
     */
    private void cleanUpContractsOfBankruptConsumers() {
        for (Consumer consumer : consumers) {
            Contract contract = consumer.getContract();

            // In cazul in care vechiul distribuitor a dat faliment, nu
            // mai trebuie reziliat nimic
            if (contract == null) {
                continue;
            }

            if (consumer.isBankrupt()) {
                contract.getPaymentDestination().getContracts()
                        .remove(contract);
            }
        }
    }

    /**
     * Anuleaza contractele consumatorilor cu un distribuitor care a dat
     * faliment
     * @param contracts contractele unui distribuitor care a dat faliment
     */
    private void deleteContracts(final LinkedHashSet<Contract> contracts) {
        for (Contract contract : contracts) {

            Consumer consumer = consumers.get(contract.getConsumerId());

            // Se reziliaza contractele facute cu consumatorii de catre
            // distribuitorul care a dat faliment
            if (!consumer.isBankrupt()) {
                consumer.setContract(null);
            }
        }
    }

    /**
     * Returneaza consumatorii de la finalul jocului
     */
    public List<Consumer> getConsumers() {
        return consumers;
    }

    /**
     * Returneaza distribuitorii de la finalul jocului
     */
    public List<Distributor> getDistributors() {
        return distributors;
    }

    /**
     * Returneaza producatorii de la finalul jocului
     */
    public List<Producer> getProducers() {
        return producers;
    }
}
