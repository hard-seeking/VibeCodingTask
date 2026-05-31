package org.example.enrollment.controller;

import org.example.enrollment.entity.EnrollRecord;
import org.example.enrollment.exception.EnrollmentNotFoundException;
import org.example.enrollment.entity.EnrollmentResult;
import org.example.enrollment.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 选课管理 REST 接口。
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    /** 导入 CSV 文本（请求体为多行字符串），返回去重排序后按课程类型分类的数据。 */
    @PostMapping(value = "/import", consumes = "text/plain")
    public EnrollmentResult importCsv(@RequestBody String csv) {
        return service.importCsv(csv);
    }

    /** 按关键字检索（学生ID/课程ID/课程名称/课程类型）。 */
    @GetMapping("/search")
    public List<EnrollRecord> search(@RequestParam(required = false) String keyword) {
        return service.search(keyword);
    }

    /** 检索无结果时返回 404 与提示信息。 */
    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EnrollmentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }
}
