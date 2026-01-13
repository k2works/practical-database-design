package com.example.pms.domain.exception;

/**
 * 作業指示が見つからない例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class WorkOrderNotFoundException extends DomainException {

    public WorkOrderNotFoundException(String workOrderNumber) {
        super("作業指示が見つかりません: " + workOrderNumber);
    }
}
