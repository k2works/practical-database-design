package com.example.pms.domain.exception;

/**
 * 工程が見つからない例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ProcessNotFoundException extends DomainException {

    public ProcessNotFoundException(String processCode) {
        super("工程が見つかりません: " + processCode);
    }
}
