package entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import strategies.ContractPriceStrategy;
import strategies.EnergyChoiceStrategyType;
import strategies.EnergyChoiceStrategy;
import strategies.GreenStrategy;
import strategies.PriceStrategy;
import strategies.QuantityStrategy;
import utils.Constants;
import utils.Observer;

import java.util.LinkedHashSet;
import java.util.List;

@JsonPropertyOrder({"id", "energyNeededKW", "contractCost", "budget",
        "producerStrategy", "isBankrupt", "contracts"})
public class Distributor implements Payer, Observer {
    private int id;
    private int energyNeededKW;
    /**
     * Pretul unei rate lunare
     */
    private int contractCost;
    private int budget;
    private EnergyChoiceStrategyType producerStrategy;
    private EnergyChoiceStrategy selectorStrategy;
    private boolean isBankrupt;
    private LinkedHashSet<Contract> contracts = new LinkedHashSet<>();

    private int contractLength;
    private int infrastructureCost;
    private int productionCost;

    /**
     * Producatorii curenti
     */
    private List<Producer> producers;
    /**
     * Lista de producatori din care poate alege atunci cand cei curenti isi
     * schimba pretul
     */
    private List<Producer> otherProducers;

    private ContractPriceStrategy contractPriceStrategy = () -> {
        int profit = (int) Math.round(
                Math.floor(Constants.PROFIT_PERCENTAGE * productionCost));

        int x;
        if (contracts.size() != 0) {
            x = (int) Math.round(
                    Math.floor(infrastructureCost / contracts.size()));

        } else {
            x = (int) Math.round(Math.floor(infrastructureCost));
        }
        return x + profit + productionCost;
    };

    /**
     *  Metoda pentru plata costurilor de infrastructura si de productie
     */
    public void pay() {
        budget -= (infrastructureCost + productionCost * contracts.size());
        if (budget < 0) {
            isBankrupt = true;
        }
    }

    /**
     * Actualizarea facuta atunci cand un producator se schimba
     */
    @Override
    public void update() {
        producers.forEach(producer -> producer.removeDistributor(this));
        producers = selectorStrategy.producers(otherProducers, energyNeededKW);
        producers.forEach(producer -> producer.addDistributor(this));

        int sum = 0;
        for (Producer producer : producers) {
            sum += producer.getEnergyPerDistributor() * producer.getPriceKW();
        }
        productionCost = (int) Math.round(Math
                .floor(sum / Constants.COST_DIVIDER));
    }

    /**
     * Aduna la buget un venit
     */
    public void addIncome(final int sum) {
        budget += sum;
    }

    /**
     * Returneaza id-ul distribuitorului
     */
    public int getId() {
        return id;
    }

    /**
     * Se foloseste la initializarea unui distribuitor
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Returneaza perioada de valabilitate a unui contract
     */
    @JsonIgnore
    public int getContractLength() {
        return contractLength;
    }

    /**
     * Initializeaza perioada de valabilitate a unui contract
     */
    @JsonIgnore
    public void setContractLength(final int contractLength) {
        this.contractLength = contractLength;
    }

    /**
     * Returneaza bugetul distribuitorului
     */
    public int getBudget() {
        return budget;
    }

    /**
     * Seteaza bugetul distribuitorului
     */
    public void setBudget(final int budget) {
        this.budget = budget;
    }

    /**
     * Returneaza costul de infrastructura specific distribuitorului
     */
    @JsonIgnore
    public int getInfrastructureCost() {
        return infrastructureCost;
    }

    /**
     * Schimba costul de infrastructura al unui distribuitor
     */
    @JsonIgnore
    public void setInfrastructureCost(final int infrastructureCost) {
        this.infrastructureCost = infrastructureCost;
    }

    /**
     * Returneaza statutul unui distribuitor
     */
    @JsonGetter("isBankrupt")
    public boolean isBankrupt() {
        return isBankrupt;
    }

    /**
     * Initializeaza statutul unui distribuitor
     */
    @JsonSetter("isBankrupt")
    public void setBankrupt(final boolean bankrupt) {
        isBankrupt = bankrupt;
    }

    /**
     * Returneaza lista cu contractele curente ale distribuitorului
     */
    public LinkedHashSet<Contract> getContracts() {
        return contracts;
    }

    /**
     * Actualizeaza lista de contracte a distribuitorului
     */
    public void setContracts(final LinkedHashSet<Contract> contracts) {
        this.contracts = contracts;
    }

    /**
     * Actualizeaza pretul unui contract oferit de distribuitor intr-o luna
     */
    @JsonIgnore
    public void setContractCost() {
        contractCost = contractPriceStrategy.price();
    }

    /**
     * Getter standard pentru energyNeededKW
     */
    public int getEnergyNeededKW() {
        return energyNeededKW;
    }

    /**
     * Setter standard pentru energyNeededKW
     */
    public void setEnergyNeededKW(final int energyNeededKW) {
        this.energyNeededKW = energyNeededKW;
    }

    /**
     * Returneaza pretul contractului oferit de catre distrubuitor
     */
    public int getContractCost() {
        return contractCost;
    }

    /**
     * seteaza pretul unei rate lunare
     */
    public void setContractCost(final int contractCost) {
        this.contractCost = contractCost;
    }

    /**
     * Getter standard pentru producerStrategy
     */
    public EnergyChoiceStrategyType getProducerStrategy() {
        return producerStrategy;
    }

    /**
     * Atribuie un tip de energie distribuitorului si initializeaza instanta
     * strategiei care returneaza producatorii potriviti tipului
     */
    public void setProducerStrategy(final EnergyChoiceStrategyType
                                            producerStrategy) {
        this.producerStrategy = producerStrategy;
        setSelectorStrategy();
    }

    /**
     * Initializeaza strategia dupa care un distributor isi alege producatorul
     */
    private void setSelectorStrategy() {
        selectorStrategy = switch (producerStrategy) {
            case PRICE -> new PriceStrategy();
            case GREEN -> new GreenStrategy();
            case QUANTITY -> new QuantityStrategy();
            default -> null;
        };
    }

    /**
     * @return producatorii curenti
     */
    @JsonIgnore
    public List<Producer> getProducers() {
        return producers;
    }

    /**
     * Initializeaza lista de producatori de la care obtine energie
     */
    @JsonIgnore
    public void setProducers(final List<Producer> producers) {
        this.producers = producers;
    }

    /**
     * Initializeaza referinta catre lista cu toti producatorii
     */
    public void setOtherProducers(final List<Producer> allProducers) {
        otherProducers = allProducers;
    }
}
