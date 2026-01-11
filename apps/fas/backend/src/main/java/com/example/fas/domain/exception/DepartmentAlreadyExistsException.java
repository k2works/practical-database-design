package com.example.fas.domain.exception;

/**
 * 部門が既に存在する場合の例外.
 */
public class DepartmentAlreadyExistsException extends AccountingException {

    private static final long serialVersionUID = 1L;

    public DepartmentAlreadyExistsException(String departmentCode) {
        super("DEP002", "部門は既に存在します: " + departmentCode);
    }
}
