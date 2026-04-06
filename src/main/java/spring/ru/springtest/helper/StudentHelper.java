package spring.ru.springtest.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.StudentRequestUpdate;
import spring.ru.springtest.exceptions.EntityNotFoundException;
import spring.ru.springtest.mapper.StudentMapper;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.models.StudentModel;
import spring.ru.springtest.repositories.StudentRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StudentHelper {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public List<StudentModel> processStudents(List<StudentRequestUpdate> studentDto, CourseModel course) {
        if (studentDto == null) {
            return null;
        }

        List<StudentModel> students = new ArrayList<>();
        for (StudentRequestUpdate dto : studentDto) {
            StudentModel student;
            if (dto.getId() != null) {
                student = studentRepository.findById(dto.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Student", dto.getId()));
                if (dto.getName() != null) {
                    student.setName(dto.getName());
                }
            } else {
                student = studentMapper.toEntity(dto);
            }
            if (student.getCourses() == null) {
                student.setCourses(new ArrayList<>());
            }
            if (!student.getCourses().contains(course)) {
                student.getCourses().add(course);
            }
            students.add(student);
        }
        return students;
    }
}
