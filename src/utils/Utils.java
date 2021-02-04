package utils;

import entities.EnergyType;
import strategies.EnergyChoiceStrategyType;

import javax.naming.NameNotFoundException;

public final class Utils {

    private Utils() { }

    /**
     * Metoda se foloseste la initializarea distribuitorilor
     * @param name informatia dupa care selectez selectez constanta din enum
     *             corespunzatoare unei entitati
     * @return un obiect enum
     * @throws NameNotFoundException in caz ca numele dat e invalid
     */
    public static EnergyChoiceStrategyType stringToChoiceStrategy(final
                                                                  String name)
            throws NameNotFoundException {
        return switch (name) {
            case "GREEN" -> EnergyChoiceStrategyType.GREEN;
            case "PRICE" -> EnergyChoiceStrategyType.PRICE;
            case "QUANTITY" -> EnergyChoiceStrategyType.QUANTITY;
            default -> throw new NameNotFoundException("Strategy " + name
                + "does not exist");
        };
    }

    /**
     * Se foloseste la initializarea producatorilor
     * @param type numele dupa care se creeaza obiectul enum
     * @return instanta de tip enum
     * @throws NameNotFoundException daca numele nu e valid
     */
    public static EnergyType stringToEnergyType(final String type) throws
            NameNotFoundException {
        return switch (type) {
            case "WIND" -> EnergyType.WIND;
            case "SOLAR" -> EnergyType.SOLAR;
            case "HYDRO" -> EnergyType.HYDRO;
            case "COAL" -> EnergyType.COAL;
            case "NUCLEAR" -> EnergyType.NUCLEAR;
            default -> throw new NameNotFoundException("Strategy " + type
                + "does not exist");
        };
    }
}
