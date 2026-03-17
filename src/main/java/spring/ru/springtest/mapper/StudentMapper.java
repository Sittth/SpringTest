package spring.ru.springtest.mapper;

import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Student;
import spring.ru.springtest.models.StudentModel;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    public Student toDto(StudentModel model) {
        if (model == null) {
            return null;
        }

        return new Student()
                .id(model.getId())
                .name(model.getName());
    }

    public StudentModel toEntity(Student dto) {
        if (dto == null) {
            return null;
        }

        StudentModel model = new StudentModel();
        model.setId(dto.getId());
        model.setName(dto.getName());

        return model;
    }

    public List<Student> toDto(List<StudentModel> students) {
        if (students == null) {
            return null;
        }

        return students.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<StudentModel> toEntity(List<Student> dto) {
        if (dto == null) {
            return null;
        }

        return dto.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
