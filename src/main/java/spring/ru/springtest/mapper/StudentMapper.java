package spring.ru.springtest.mapper;

import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Student;
import spring.ru.springtest.models.StudentModel;

@Component
public class StudentMapper {

    public Student toDto(Student student) {
        if (student == null) {
            return null;
        }

        return new Student().id(student.getId()).name(student.getName());
    }

    public StudentModel toEntity(Student dto) {
        if (dto == null) {
            return null;
        }

        StudentModel studentModel = new StudentModel();
        studentModel.setId(dto.getId());
        studentModel.setName(dto.getName());
        return studentModel;
    }
}
