package ru.iteco.project.service.specifications;

import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;
import ru.iteco.project.service.mappers.DateTimeMapper;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static ru.iteco.project.service.specifications.SearchOperations.GREATER_THAN_OR_EQUAL;
import static ru.iteco.project.service.specifications.SearchOperations.LESS_THAN_OR_EQUAL;

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


    static PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate> equal() {
        return (CriteriaBuilder cb, Path path, CriteriaObject.RestrictionValues restriction) ->
                new ComparisonPredicate((CriteriaBuilderImpl) cb,
                        ComparisonPredicate.ComparisonOperator.EQUAL,
                        path,
                        restriction.getTypedValue()
                );
    }

    static PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate> notEqual() {
        return (CriteriaBuilder cb, Path path, CriteriaObject.RestrictionValues restriction) ->
                new ComparisonPredicate((CriteriaBuilderImpl) cb,
                        ComparisonPredicate.ComparisonOperator.NOT_EQUAL,
                        path,
                        restriction.getTypedValue()
                );
    }


    static PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate> between() {
        return (CriteriaBuilder cb, Path path, CriteriaObject.RestrictionValues restriction) -> {
            if (LocalDateTime.class.equals(path.getJavaType())) {
                return cb.between(path,
                        DateTimeMapper.stringToObject(restriction.getMinValue()),
                        DateTimeMapper.stringToObject(restriction.getMaxValue())
                );
            }
            if (BigDecimal.class.equals(path.getJavaType())) {
                return cb.between(path,
                        new BigDecimal(restriction.getMinValue()),
                        new BigDecimal(restriction.getMaxValue())
                );
            }
            return cb.isNull(path);
        };
    }


    static PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate> like() {
        return (CriteriaBuilder cb, Path path, CriteriaObject.RestrictionValues restriction) ->
                cb.like(path, "%" + restriction.getValue() + "%");
    }

    static PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate> notLike() {
        return (CriteriaBuilder cb, Path path, CriteriaObject.RestrictionValues restriction) ->
                cb.notLike(path, "%" + restriction.getValue() + "%");
    }


    static PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate> lessThan() {
        return (CriteriaBuilder cb, Path path, CriteriaObject.RestrictionValues restriction) -> {
            if (LocalDateTime.class.equals(path.getJavaType())) {
                return cb.lessThan(path, DateTimeMapper.stringToObject(restriction.getValue()));
            }
            if (BigDecimal.class.equals(path.getJavaType())) {
                return cb.lessThan(path, new BigDecimal(restriction.getValue()));
            }
            return cb.isNull(path);
        };
    }

    static PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate> greaterThan() {
        return (CriteriaBuilder cb, Path path, CriteriaObject.RestrictionValues restriction) -> {
            if (LocalDateTime.class.equals(path.getJavaType())) {
                return cb.greaterThan(path, DateTimeMapper.stringToObject(restriction.getValue()));
            }
            if (BigDecimal.class.equals(path.getJavaType())) {
                return cb.greaterThan(path, new BigDecimal(restriction.getValue()));
            }
            return cb.isNull(path);
        };
    }


    static PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate> lessThanOrEqual() {
        return (CriteriaBuilder cb, Path path, CriteriaObject.RestrictionValues restriction) -> {
            if (LocalDateTime.class.equals(path.getJavaType())) {
                return cb.lessThanOrEqualTo(path, DateTimeMapper.stringToObject(restriction.getValue()));
            }
            if (BigDecimal.class.equals(path.getJavaType())) {
                return cb.lessThanOrEqualTo(path, new BigDecimal(restriction.getValue()));
            }
            return cb.isNull(path);
        };
    }

    static PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate> greaterThanOrEqual() {
        return (CriteriaBuilder cb, Path path, CriteriaObject.RestrictionValues restriction) -> {
            if (LocalDateTime.class.equals(path.getJavaType())) {
                return cb.greaterThanOrEqualTo(path, DateTimeMapper.stringToObject(restriction.getValue()));
            }
            if (BigDecimal.class.equals(path.getJavaType())) {
                return cb.greaterThanOrEqualTo(path, new BigDecimal(restriction.getValue()));
            }
            return cb.isNull(path);
        };
    }


    static PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate> notBetween() {
        return (CriteriaBuilder cb, Path path, CriteriaObject.RestrictionValues restriction) -> {

            if (LocalDateTime.class.equals(path.getJavaType())) {
                return cb.or(
                        lessThanOrEqual().produce(cb, path, new CriteriaObject.RestrictionValues(
                                restriction.getKey(),
                                restriction.getMaxValue(),
                                GREATER_THAN_OR_EQUAL
                        )),

                        lessThan().produce(cb, path, new CriteriaObject.RestrictionValues(
                                restriction.getKey(),
                                restriction.getMinValue(),
                                LESS_THAN_OR_EQUAL
                        ))
                );
            }

            if (BigDecimal.class.equals(path.getJavaType())) {
                return cb.or(
                        greaterThan().produce(cb, path, new CriteriaObject.RestrictionValues(
                                restriction.getKey(),
                                restriction.getMaxValue(),
                                GREATER_THAN_OR_EQUAL
                        )),
                        lessThan().produce(cb, path, new CriteriaObject.RestrictionValues(
                                restriction.getKey(),
                                restriction.getMinValue(),
                                LESS_THAN_OR_EQUAL
                        ))
                );
            }
            return cb.isNull(path);
        };
    }

}
