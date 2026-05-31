package org.example.enrollment.entity;

import java.util.List;
import java.util.Map;

/**
 * 处理结果 DTO：总记录数 + 按课程类型分组的数据（key 为课程类型）。
 */
public record EnrollmentResult(int total, Map<String, List<EnrollRecord>> grouped) {
}
