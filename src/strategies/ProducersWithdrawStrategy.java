package strategies;

import entities.Producer;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

interface ProducersWithdrawStrategy {
    /**
     * Extrage din coada producatori pana cand energia furnizata de acestia
     * este suficienta
     * @param queue colectia de producatori ordonati
     * @param energyNeeded cantitatea de energie necesara unui distribuitor
     * @return
     */
    static List<Producer> execute(final PriorityQueue<Producer> queue,
                                  final int energyNeeded) {
        List<Producer> list = new ArrayList<>();
        int energy = 0;

        while (energy < energyNeeded) {
            Producer producer = queue.poll();
            if (producer.isAvailable()) {
                list.add(producer);
                energy += producer.getEnergyPerDistributor();
            }
        }
        return list;
    }
}
