package org.example.enrollment.entity;


/**
 * 选课记录实体类
 */
public class EnrollRecord {
    /**
     * 学生ID，格式：S+6位数字
     */
    private String studentId;

    /**
     * 课程ID，格式：C+6位数字
     */
    private String courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类型：公共课、专业课、选修课 (阶段三新增)
     */
    private String courseType;

    // --- 构造函数 ---

    // 兼容第一阶段任务的三参构造
    public EnrollRecord(String studentId, String courseId, String courseName) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseType = "未知"; // 默认兜底
    }

    // 适配第三阶段任务的全参构造
    public EnrollRecord(String studentId, String courseId, String courseName, String courseType) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseType = courseType;
    }

    // --- Getter 和 Setter (严禁使用 Lombok，防止环境不支持) ---

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }

    /**
     * 重写toString方法，满足输出格式要求
     */
    @Override
    public String toString() {
        return String.format("学生ID：%s，课程ID：%s，课程名称：%s，课程类型：%s",
                studentId, courseId, courseName, courseType);
    }
}