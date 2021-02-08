package ru.iteco.project.exception;

/**
 * Исключение, возникающее при попытке формирования бухгалтерского отчета из пустого документа
 */
public class EmptyReportException extends RuntimeException {

    public EmptyReportException() {
        super();
    }

    public EmptyReportException(String message) {
        super(message);
    }

    public EmptyReportException(String message, Throwable cause) {
        super(message, cause);
    }

}
