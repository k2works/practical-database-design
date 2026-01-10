package com.example.fas.domain.exception;

/**
 * 部門が見つからない場合の例外.
 */
public class DepartmentNotFoundException extends AccountingException {

    private static final long serialVersionUID = 1L;

    public DepartmentNotFoundException(String departmentCode) {
        super("DEP001", "部門が見つかりません: " + departmentCode);
    }
}
