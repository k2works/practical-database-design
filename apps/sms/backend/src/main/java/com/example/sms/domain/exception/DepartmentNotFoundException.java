package com.example.sms.domain.exception;

/**
 * 部門が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class DepartmentNotFoundException extends ResourceNotFoundException {

    public DepartmentNotFoundException(String departmentCode) {
        super("部門が見つかりません: " + departmentCode);
    }
}
