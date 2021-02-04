package strategies;

import entities.Producer;

import java.util.List;

public interface EnergyChoiceStrategy {
    /**
     * @param producers lista de producatori disponibili
     * @param energyNeeded energia necesara unui distribuitor
     * @return producatorii alesi dupa logica strategiei implementate
     */
    List<Producer> producers(List<Producer> producers, int energyNeeded);
}
