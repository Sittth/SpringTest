package spring.ru.springtest.mapper;

import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Course;
import spring.ru.springtest.models.CourseModel;

@Component
public class CourseMapper {

    public Course toDto(Course course) {
        if (course == null) {
            return null;
        }

        return new Course().id(course.getId()).title(course.getTitle());
    }

    public CourseModel toEntity(Course dto) {
        if (dto == null) {
            return null;
        }

        CourseModel courseModel = new CourseModel();
        courseModel.setId(dto.getId());
        courseModel.setTitle(dto.getTitle());
        return courseModel;
    }
}
