package input;

public final class DistributorChange {
    private int id;
    private int infrastructureCost;

    /**
     * id-ul distribuitorului ale carui costuri trebuie modificate
     */
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    /**
     * noul cost pentru infrastructura
     */
    public int getInfrastructureCost() {
        return infrastructureCost;
    }

    public void setInfrastructureCost(final int infrastructureCost) {
        this.infrastructureCost = infrastructureCost;
    }
}
