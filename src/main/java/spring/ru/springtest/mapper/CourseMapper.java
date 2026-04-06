package spring.ru.springtest.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import spring.ru.springtest.dto.CourseRequestCreate;
import spring.ru.springtest.dto.CourseRequestUpdate;
import spring.ru.springtest.dto.CourseResponse;
import spring.ru.springtest.helper.StudentHelper;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.models.StudentModel;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = StudentMapper.class)
public abstract class CourseMapper {

    @Autowired
    protected StudentHelper studentHelper;

    public abstract CourseResponse toResponse(CourseModel course);

    @Mapping(target = "id", ignore = true)
    public abstract CourseModel toEntity(CourseRequestCreate requestCreate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    public abstract void updateEntityFromDto(CourseRequestUpdate dto, @MappingTarget CourseModel course);

    @AfterMapping
    protected void updateStudents(@MappingTarget CourseModel courseModel, CourseRequestUpdate dto) {
        if (dto.getStudents() != null) {
            List<StudentModel> students = studentHelper.processStudents(dto.getStudents(), courseModel);
            courseModel.getStudents().clear();
            courseModel.getStudents().addAll(students);
        }
    }
}