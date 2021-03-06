package strategies;

import entities.Producer;

import java.util.List;
import java.util.PriorityQueue;

public class PriceStrategy implements EnergyChoiceStrategy {
    private PriorityQueue<Producer> queue = new PriorityQueue<>((o1, o2) -> {
        if (Double.compare(o1.getPriceKW(), o2.getPriceKW()) == 0) {
            if (o1.getEnergyPerDistributor()
                    == o2.getEnergyPerDistributor()) {
                return o1.getId() - o2.getId();
            }
            return o2.getEnergyPerDistributor() - o1.getEnergyPerDistributor();
        }
        return Double.compare(o1.getPriceKW(), o2.getPriceKW());
    });

    /**
     * @param producers lista de producatori disponibili
     * @param energyNeeded energia necesara unui distribuitor
     * @return producatorii alesi dupa logica strategiei implementate
     */
    @Override
    public List<Producer> producers(final List<Producer> producers, final int
                                    energyNeeded) {
        queue.clear();
        queue.addAll(producers);
        return ProducersWithdrawStrategy.execute(queue, energyNeeded);
    }
}
