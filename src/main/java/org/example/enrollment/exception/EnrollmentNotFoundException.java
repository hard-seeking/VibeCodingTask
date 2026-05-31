package org.example.enrollment.exception;

/**
 * 检索无匹配结果时抛出的自定义异常。
 */
public class EnrollmentNotFoundException extends RuntimeException {
    public EnrollmentNotFoundException(String message) {
        super(message);
    }
}
