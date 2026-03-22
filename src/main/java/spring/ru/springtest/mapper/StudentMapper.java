package spring.ru.springtest.mapper;

import org.mapstruct.Mapper;
import spring.ru.springtest.dto.Student;
import spring.ru.springtest.models.StudentModel;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    Student toDto(StudentModel student);

    StudentModel toEntity(Student dto);

    List<Student> toDto(List<StudentModel> students);

    List<StudentModel> toEntity(List<Student> students);
}