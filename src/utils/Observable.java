package utils;

public interface Observable {
    /**
     * Metoda care actualizeaza atributele observatorilor dupa ce clasa
     * observabila a suferit o modificare
     */
    void notifyObservers();

    /**
     * Metoda care adauga un observator
     */
    void addObserver(Observer observer);
}
