// This file implements the Observer Design Pattern
interface ObserverInterfaces {
    void update();
}

interface Subject {
    void registerObserver(ObserverInterfaces o);
    void removeObserver(ObserverInterfaces o);
    void notifyObservers();
}