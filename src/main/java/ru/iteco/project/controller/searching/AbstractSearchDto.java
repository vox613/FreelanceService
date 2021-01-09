package ru.iteco.project.controller.searching;

import ru.iteco.project.controller.dto.DtoInterface;
import ru.iteco.project.service.specifications.JoinOperations;

/**
 * Суперкласс для всех dto предназначенных для поиска данных
 */
public class AbstractSearchDto implements SearchDto {

    /** Тип операции объединяющей различные предикаты */
    private JoinOperations joinOperation;


    public AbstractSearchDto() {
    }


    public JoinOperations getJoinOperation() {
        return joinOperation;
    }

    public void setJoinOperation(JoinOperations joinOperation) {
        this.joinOperation = joinOperation;
    }

    @Override
    public DtoInterface searchData() {
        return this;
    }
}
