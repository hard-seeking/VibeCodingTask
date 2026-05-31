package org.example.enrollment.service;

import org.example.enrollment.entity.EnrollRecord;
import org.example.enrollment.entity.EnrollmentResult;
import org.example.enrollment.exception.EnrollmentNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 选课业务逻辑层：CSV 解析、批量去重排序、按课程类型分类、多字段检索。
 */
@Service
public class EnrollmentService {

    /** 学号格式：S+6位数字；课程号格式：C+6位数字。 */
    private static final java.util.regex.Pattern STUDENT_ID = java.util.regex.Pattern.compile("S\\d{6}");
    private static final java.util.regex.Pattern COURSE_ID = java.util.regex.Pattern.compile("C\\d{6}");
    /** 内存中保存最近一次导入处理后的记录，供检索使用（线程安全）。 */
    private final List<EnrollRecord> store = new CopyOnWriteArrayList<>();

    /**
     * 将 CSV 文本（每行：studentId,courseId,courseName,courseType）解析为实体列表。
     * 忽略空行、列数不足的行，以及学号/课程号不符合格式规则的行。
     */
    public List<EnrollRecord> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return csv.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> line.split(",", -1))
                .filter(cols -> cols.length >= 4)                                 //后续追加
                .filter(cols -> STUDENT_ID.matcher(cols[0].trim()).matches()
                        && COURSE_ID.matcher(cols[1].trim()).matches())
                .map(cols -> new EnrollRecord(
                        cols[0].trim(), cols[1].trim(), cols[2].trim(), cols[3].trim()))
                .collect(Collectors.toList());
        
    }

    /**
     * 批量处理：以 学生ID+课程ID 去重（保留首条），再按 学生ID、课程ID 升序排序，
     * 结果写入内存 store 并返回。
     */
    public List<EnrollRecord> process(List<EnrollRecord> records) {
        List<EnrollRecord> result = records.stream()
                .collect(Collectors.toMap(
                        r -> r.getStudentId() + "|" + r.getCourseId(),
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new))
                .values().stream()
                .sorted(Comparator.comparing(EnrollRecord::getStudentId)
                        .thenComparing(EnrollRecord::getCourseId))
                .collect(Collectors.toCollection(ArrayList::new));

        store.clear();
        store.addAll(result);
        return result;
    }

    /** 按课程类型分组（保持类型出现顺序）。 */
    public Map<String, List<EnrollRecord>> classify(List<EnrollRecord> records) {
        return records.stream().collect(Collectors.groupingBy(
                r -> r.getCourseType() == null ? "未分类" : r.getCourseType(),
                LinkedHashMap::new,
                Collectors.toList()));
    }

    /** 导入入口：解析 -> 处理 -> 分类，封装为结果 DTO。 */
    public EnrollmentResult importCsv(String csv) {
        List<EnrollRecord> processed = process(parseCsv(csv));
        return new EnrollmentResult(processed.size(), classify(processed));
    }

    /**
     * 在内存 store 中按关键字检索（对 学生ID/课程ID/课程名称/课程类型 做不区分大小写的模糊匹配）。
     * 无匹配时抛出自定义异常。
     */
    public List<EnrollRecord> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>(store);
        }
        String kw = keyword.trim().toLowerCase(Locale.ROOT);
        List<EnrollRecord> matched = store.stream()
                .filter(r -> contains(r.getStudentId(), kw)
                        || contains(r.getCourseId(), kw)
                        || contains(r.getCourseName(), kw)
                        || contains(r.getCourseType(), kw))
                .collect(Collectors.toList());
        if (matched.isEmpty()) {
            throw new EnrollmentNotFoundException("未找到与 \"" + keyword + "\" 匹配的选课记录");
        }
        return matched;
    }

    private boolean contains(String value, String lowerKeyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(lowerKeyword);
    }
}
