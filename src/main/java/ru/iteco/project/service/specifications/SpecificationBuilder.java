package ru.iteco.project.service.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.iteco.project.controller.searching.SearchUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.EnumMap;

import static ru.iteco.project.service.specifications.SearchOperations.*;
import static ru.iteco.project.service.specifications.SearchPredicatesUtil.*;

/**
 * Сервис предоставляет функционал универсального формировавния спецификаций для поиска данных
 */
@Service
public class SpecificationBuilder<T> {

    /*** Справочник содержащий наименование операции поиска против метода формирования предиката для этой операции**/
    private final EnumMap<SearchOperations, PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate>>
            predicatesForSearchOperations = fillPredicatesForSearchOperations();

    /**
     * Метод заполнения справочника предикатов predicatesForSearchOperations
     *
     * @return - заполенный экземпляр EnumMap со значениями предикатов реализованных
     * в ru.iteco.project.service.specifications.PredicateProducer.java
     */
    private EnumMap fillPredicatesForSearchOperations() {
        return new EnumMap<SearchOperations, PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate>>(SearchOperations.class) {{
            put(EQUAL, equal());
            put(NOT_EQUAL, notEqual());

            put(BETWEEN, between());
            put(NOT_BETWEEN, notBetween());

            put(LIKE, like());
            put(NOT_LIKE, notLike());

            put(LESS_THAN, lessThan());
            put(GREATER_THAN, greaterThan());

            put(LESS_THAN_OR_EQUAL, lessThanOrEqual());
            put(GREATER_THAN_OR_EQUAL, greaterThanOrEqual());
        }};
    }


    /**
     * Метод получения спецификации для поиска
     *
     * @param criteriaObject - объект со всей информаций о критериях и условиях поиска
     * @return - объект спецификации для поиска данных
     */
    public Specification<T> getSpec(final CriteriaObject criteriaObject) {
        return (root, query, builder) -> buildPredicates(root, builder, criteriaObject);
    }

    /**
     * Метод формирует итоговый предикат на основании списка предоставленных
     *
     * @param root           - корневой тип
     * @param builder        - объект для построения критериев и сложных выборок
     * @param criteriaObject - объект со всей информаций о критериях и условиях поиска
     * @return - итоговый предикат на основании списка предоставленных
     */
    private Predicate buildPredicates(Root root, CriteriaBuilder builder, final CriteriaObject criteriaObject) {
        Predicate[] predicates = criteriaObject.getRestrictions().stream()
                .map(restriction -> predicatesForSearchOperations.get(restriction.getSearchOperation())
                        .produce(builder, root.get(restriction.getKey()), restriction))

                .toArray(Predicate[]::new);

        if (criteriaObject.getJoinOperation() == JoinOperations.AND) {
            return builder.and(predicates);
        }
        return builder.or(predicates);
    }

    /**
     * Метод определяет относится ли операция к диапазонной
     *
     * @param searchUnit - элемент поиска со всеми данными
     * @return - true - операция является диапазонной, false - операция не является диапазонной
     */
    public static boolean isBetweenOperation(SearchUnit searchUnit) {
        return (searchUnit.getSearchOperation() == BETWEEN) || (searchUnit.getSearchOperation() == NOT_BETWEEN);
    }

    /**
     * Метод проверяет наличие необходимых значений в searchUnit для конкретного типа операции
     *
     * @param searchUnit - элемент поиска со всеми данными
     * @return true - элемент поиска содержит необходимые данные, false - элемент поиска не содержит необходимые данные
     */
    public static boolean searchUnitIsValid(SearchUnit searchUnit) {
        if (!ObjectUtils.isEmpty(searchUnit) && !ObjectUtils.isEmpty(searchUnit.getSearchOperation())) {
            if (isBetweenOperation(searchUnit)) {
                return !ObjectUtils.isEmpty(searchUnit.getMinValue()) && !ObjectUtils.isEmpty(searchUnit.getMaxValue());
            } else {
                return !ObjectUtils.isEmpty(searchUnit.getValue());
            }
        }
        return false;
    }
}
