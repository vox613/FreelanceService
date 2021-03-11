package ru.iteco.project.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.iteco.project.enumaration.JoinOperations;
import ru.iteco.project.enumaration.SearchOperations;
import ru.iteco.project.resource.SearchUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.function.Function;

import static ru.iteco.project.enumaration.SearchOperations.*;
import static ru.iteco.project.specification.SearchPredicatesUtil.*;

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
        SearchOperations searchOperation = fromString(searchUnit.getSearchOperation());
        return (searchOperation == BETWEEN) || (searchOperation == NOT_BETWEEN);
    }

    /**
     * Метод определяет относится ли операция к определяющей эквивалентность
     *
     * @param searchUnit - элемент поиска со всеми данными
     * @return - true - операция является определяющей эквивалентность, false - операция не является определяющей эквивалентность
     */
    public static boolean isEqualOperation(SearchUnit searchUnit) {
        SearchOperations searchOperation = fromString(searchUnit.getSearchOperation());
        return (searchOperation == EQUAL) || (searchOperation == NOT_EQUAL);
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


    /**
     * Метод формирует restrictionValue и добавляет его в список
     *
     * @param restrictionValues - список ограничений для поиска
     * @param searchUnit        - элемент поиска со всеми данными
     * @param key               - наименование поля по которому осуществляется поиск
     * @param function          - вспомогатеьный метод получения необходимого значения
     */
    public static void prepareRestrictionValue(ArrayList<CriteriaObject.RestrictionValues> restrictionValues,
                                               SearchUnit searchUnit, String key, Function function) {
        if (searchUnitIsValid(searchUnit)) {
            restrictionValues.add(enrichByOperationType(searchUnit, function)
                    .setKey(key)
                    .setSearchOperation(searchUnit.getSearchOperation())
                    .build());
        }
    }

    /**
     * Метод обогащает ограничение поиска необходимыми данными в зависимости от типа запроса
     *
     * @param searchUnit - элемент поиска со всеми данными
     * @param function   - вспомогатеьный метод получения необходимого значения
     * @return объект RestrictionValues.Builder для дальнейшего формирования RestrictionValue
     */
    private static CriteriaObject.RestrictionValues.Builder enrichByOperationType(SearchUnit searchUnit, Function function) {
        CriteriaObject.RestrictionValues.Builder builder = CriteriaObject.RestrictionValues.newBuilder();
        if (isEqualOperation(searchUnit)) {
            builder.setTypedValue(function.apply(searchUnit));
        } else if (isBetweenOperation(searchUnit)) {
            builder.setMinValue(searchUnit.getMinValue())
                    .setMaxValue(searchUnit.getMaxValue());
        } else {
            builder.setValue(searchUnit.getValue());
        }
        return builder;
    }
}
