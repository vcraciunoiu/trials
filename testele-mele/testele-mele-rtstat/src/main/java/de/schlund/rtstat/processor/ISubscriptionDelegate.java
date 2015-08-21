package de.schlund.rtstat.processor;

public interface ISubscriptionDelegate<T> {
    public void addConsumer(Processor<T> consumer);
    public void publish(T event);
}
