package entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Comparator;

public class Producer {
    private int id;
    private int maxDistributors;
    private double priceKW;
    private EnergyType energyType;
    private int energyPerDistributor;
    private List<MonthlyStat> monthlyStats = new ArrayList<>();
    private final Collection<Distributor> distributors = new TreeSet<>(
            Comparator.comparingInt(Distributor::getId));
    private List<Distributor> oldDistributors = new ArrayList<>();

    class MonthlyStat {
        /**
         * id-ul unei luni
         */
        private int month;
        private List<Integer> distributorsIds;

        MonthlyStat(final int month, final List<Integer> distributorsIds) {
            this.month = month;
            this.distributorsIds = distributorsIds;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(final int month) {
            this.month = month;
        }

        public List<Integer> getDistributorsIds() {
            return distributorsIds;
        }

        public void setDistributorsIds(final List<Integer> distributorsIds) {
            this.distributorsIds = distributorsIds;
        }
    }

    /**
     * Salveaza id-urile distribuitorilor care au primit energie de la un
     * producator in luna month
     */
    public void addStat(final int month) {
        monthlyStats.add(new MonthlyStat(month, getDistributorsIds()));
    }

    /**
     * Adauga un distribuitor in lista cu cei care primesc energie de la acest
     * producator
     */
    public void addDistributor(final Distributor client) {
        distributors.add(client);
    }

    /**
     * Sterge din lista de distribuitori un element
     */
    public void removeDistributor(final Distributor client) {
        distributors.remove(client);
    }

    /**
     * @return lista cu observatorii producatorului
     */
    @JsonIgnore
    public TreeSet<Distributor> getObservers() {
        return (TreeSet<Distributor>) distributors;
    }

    /**
     * Verifica daca producatorul curent poate oferi energie inca unui
     * distribuitor
     */
    @JsonIgnore
    public boolean isAvailable() {
        return distributors.size() < maxDistributors;
    }

    /**
     * Getter standard pentru id
     */
    public int getId() {
        return id;
    }

    /**
     * Setter standard pentru id
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     *  Getter standard pentru maxDistributors
     */
    public int getMaxDistributors() {
        return maxDistributors;
    }

    /**
     * Setter standard pentru maxDistributors
     */
    public void setMaxDistributors(final int maxDistributors) {
        this.maxDistributors = maxDistributors;
    }

    /**
     * Getter standard pentru priceKW
     */
    public double getPriceKW() {
        return priceKW;
    }

    /**
     * Setter standard pentru priceKW
     */
    public void setPriceKW(final double priceKW) {
        this.priceKW = priceKW;
    }

    /**
     * Getter standard pentru energyType
     */
    public EnergyType getEnergyType() {
        return energyType;
    }

    /**
     * Setter standard pentru energyType
     */
    public void setEnergyType(final EnergyType energyType) {
        this.energyType = energyType;
    }

    /**
     * Getter standard pentru energyPerDistributor
     */
    public int getEnergyPerDistributor() {
        return energyPerDistributor;
    }

    /**
     * Setter standard pentru energyPerDistributor
     */
    public void setEnergyPerDistributor(final int energyPerDistributor) {
        this.energyPerDistributor = energyPerDistributor;
    }

    /**
     * Getter standard pentru monthlyStats
     */
    public List<MonthlyStat> getMonthlyStats() {
        return monthlyStats;
    }

    /**
     * Setter standard pentru monthlyStats
     */
    public void setMonthlyStats(final List<MonthlyStat> monthlyStats) {
        this.monthlyStats = monthlyStats;
    }

    /**
     * Pune id-urile tuturor distribuitorilor (in ordine crescatoare) intr-o
     * lista
     * @return lista cu id-urile
     */
    @JsonIgnore
    private List<Integer> getDistributorsIds() {
        List<Integer> list = new ArrayList<>();
        for (Distributor d : distributors) {
            list.add(d.getId());
        }
        return list;
    }
}
