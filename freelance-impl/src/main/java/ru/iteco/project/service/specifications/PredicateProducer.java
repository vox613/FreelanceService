package ru.iteco.project.service.specifications;

/**
 * Функциональный интерфейс для производства предикатов поиска
 *
 * @param <C> - экземпляр CriteriaBuilder
 * @param <P> - экземпляр Path
 * @param <R> - экземпляр CriteriaObject.RestrictionValues
 * @param <K> - экземпляр Predicate
 */
@FunctionalInterface
public interface PredicateProducer<C, P, R, K> {

    /**
     * Сигнатура метода для формирования предиката
     *
     * @param c - экземпляр CriteriaBuilder
     * @param p - экземпляр Path
     * @param r - экземпляр CriteriaObject.RestrictionValues
     * @return - экземпляр Predicate
     */
    K produce(C c, P p, R r);

}
