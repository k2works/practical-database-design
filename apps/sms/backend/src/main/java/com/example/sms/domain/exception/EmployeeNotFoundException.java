package com.example.sms.domain.exception;

/**
 * 社員が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class EmployeeNotFoundException extends ResourceNotFoundException {

    public EmployeeNotFoundException(String employeeCode) {
        super("社員が見つかりません: " + employeeCode);
    }
}
