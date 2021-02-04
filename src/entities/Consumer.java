package entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import utils.Constants;

@JsonPropertyOrder({"id", "isBankrupt", "budget"})
public class Consumer implements Payer {
    private int id;
    private boolean isBankrupt;
    private int budget;
    private int monthlyIncome;

    private Contract contract;

    /**
     * Pentru situatiile in care ramane dator
     */
    private int remainingPayment = 0;
    private Distributor remainingDistributor = null;

    /**
     * Metoda in care un consumator primeste venitul lunar si plateste
     * o rata din contract daca are suficienti bani. In caz contrar, fie ramane
     * dator pentru o luna, fie daca se repeta situatia de 2 ori consecutiv
     * devine falit
     */
    @Override
    public void pay() {
        if (isBankrupt) {
            return;
        }
        budget += monthlyIncome;

        // Verific mai intai daca poate plati pentru luna curenta
        int delta = budget - contract.getPrice();
        if (delta < 0) {
            if (remainingDistributor != null) {
                isBankrupt = true;
            } else {
                remainingDistributor = contract.getPaymentDestination();
                remainingPayment = contract.getPrice();
                remainingPayment *= (1 + Constants.CONSUMER_PENALTY);
                contract.decreaseValidity();
            }
            return;
        }

        // Iar apoi verific daca poate plati restanta
        if (remainingDistributor != null) {
            delta -= remainingPayment;
            if (delta < 0) {
                isBankrupt = true;
                return;
            } else if (remainingDistributor
                    == contract.getPaymentDestination()) {
                remainingDistributor.addIncome(remainingPayment);
                budget = delta;
                remainingDistributor = null;
                remainingPayment = 0;
            } else {
                remainingDistributor.addIncome(remainingPayment);
                budget = delta;
                remainingDistributor = contract.getPaymentDestination();
                remainingPayment = contract.getPrice();
                remainingPayment *= (1 + Constants.CONSUMER_PENALTY);
                contract.decreaseValidity();
                return;
            }
        }
        contract.getPaymentDestination().addIncome(contract.getPrice());
        budget = delta;
        contract.decreaseValidity();
    }

    /**
     * Metoda prin care un consumator face o intelegere cu un distribuitor si
     * isi seteaza referinta catre noul contract
     * @param distributor cel cu care se face contractul
     */
    public void makeContractWith(final Distributor distributor) {
        if (distributor == null) {
            contract = null;
            return;
        }
        contract = new Contract(id, distributor.getContractCost(),
                distributor.getContractLength(), distributor);
        distributor.getContracts().add(contract);
    }

    /**
     * Returneaza id-ul consumatorului
     */
    public int getId() {
        return id;
    }

    /**
     * Se foloseste la initializarea unui consumator
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Returneaza statutul unui consumator
     */
    @JsonGetter("isBankrupt")
    public boolean isBankrupt() {
        return isBankrupt;
    }

    /**
     * Initializeaza statutul unui consumator
     */
    @JsonSetter("isBankrupt")
    public void setBankrupt(final boolean bankrupt) {
        isBankrupt = bankrupt;
    }

    /**
     * Returneaza bugetul curent al unui consumator
     */
    public int getBudget() {
        return budget;
    }

    /**
     * Actualizeaza bugetul unui consumator
     */
    public void setBudget(final int budget) {
        this.budget = budget;
    }

    /**
     * Returneaza venitul lunar al unui consumator
     */
    @JsonIgnore
    public int getMonthlyIncome() {
        return monthlyIncome;
    }

    /**
     * Actualizeaza venitul lunar al unui consumator
     */
    @JsonIgnore
    public void setMonthlyIncome(final int monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    /**
     * Returneaza referinta catre contractul curent al consumatorului
     */
    @JsonIgnore
    public Contract getContract() {
        return contract;
    }

    /**
     * Actualizeaza contractul consumatorului
     */
    @JsonIgnore
    public void setContract(final Contract contract) {
        this.contract = contract;
    }
}
