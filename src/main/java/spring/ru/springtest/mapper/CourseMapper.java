package spring.ru.springtest.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import spring.ru.springtest.dto.Course;
import spring.ru.springtest.models.CourseModel;

import java.util.ArrayList;

@Mapper(componentModel = "spring", uses = StudentMapper.class)
public interface CourseMapper {

    Course toDto(CourseModel course);

    CourseModel toEntity(Course dto);

    void updateEntityFromDto(Course dto, @MappingTarget CourseModel course);

    @AfterMapping
    default void linkStudentToCourse(@MappingTarget CourseModel courseModel) {
        if (courseModel.getStudents() != null) {
            courseModel.getStudents().forEach(student -> {
                if (student.getCourses() == null) {
                    student.setCourses(new ArrayList<>());
                }
                if (!student.getCourses().contains(courseModel)) {
                    student.getCourses().add(courseModel);
                }
            });
        }
    }
}