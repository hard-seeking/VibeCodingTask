package org.example.enrollment.utils;

import org.example.enrollment.entity.EnrollRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 学生选课基础处理工具类。
 */
public class EnrollmentProcessor {

    /**
     * 处理选课记录：按 学生ID+课程ID 去重，再按 学生ID、课程ID 升序排序，
     * 逐行打印格式化信息并返回处理后的列表。
     *
     * @param records 原始选课记录
     * @return 去重并排序后的选课记录列表
     */
    public static List<EnrollRecord> processEnrollments(List<EnrollRecord> records) {
        List<EnrollRecord> result = records.stream()
                // 1. 去重：以 学生ID|课程ID 为唯一键，借助 LinkedHashMap 只保留首次出现的记录
                .collect(Collectors.toMap(
                        r -> r.getStudentId() + "|" + r.getCourseId(),
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new))
                .values().stream()
                // 2. 排序：先按学生ID升序，相同时按课程ID升序
                .sorted(Comparator.comparing(EnrollRecord::getStudentId)
                        .thenComparing(EnrollRecord::getCourseId))
                .collect(Collectors.toCollection(ArrayList::new));

        // 3. 逐行打印（直接调用 toString）
        result.forEach(System.out::println);
        return result;
    }

    public static void main(String[] args) {
        List<EnrollRecord> records = List.of(
                new EnrollRecord("S02", "C01", "数据结构"),
                new EnrollRecord("S01", "C03", "操作系统"),
                new EnrollRecord("S01", "C01", "数据结构"),
                new EnrollRecord("S01", "C01", "数据结构(重复)"), // 与上一条 学生+课程 相同，应被去重
                new EnrollRecord("S02", "C01", "数据结构"),       // 完全重复，应被去重
                new EnrollRecord("S01", "C02", "计算机网络")
        );

        System.out.println("处理后的选课记录：");
        processEnrollments(records);
    }
}
