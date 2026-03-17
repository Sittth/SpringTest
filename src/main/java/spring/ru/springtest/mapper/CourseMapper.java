package spring.ru.springtest.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Course;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.models.StudentModel;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final StudentMapper studentMapper;

    public Course toDto(CourseModel course) {
        if (course == null) {
            return null;
        }

        return new Course().id(course.getId()).title(course.getTitle())
                .students(studentMapper.toDto(course.getStudents()));
    }

    public CourseModel toEntity(Course dto) {
        if (dto == null) {
            return null;
        }

        CourseModel courseModel = new CourseModel();
        courseModel.setId(dto.getId());
        courseModel.setTitle(dto.getTitle());

        List<StudentModel> students = studentMapper.toEntity(dto.getStudents());

        if (students != null) {
            students.forEach(student -> {
                if (student.getCourses() == null) {
                    student.setCourses(new ArrayList<>());
                }
                student.getCourses().add(courseModel);
            });
        }

        courseModel.setStudents(studentMapper.toEntity(dto.getStudents()));
        return courseModel;
    }
}
