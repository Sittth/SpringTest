package spring.ru.springtest.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Student;
import spring.ru.springtest.models.StudentModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-27T20:27:18+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 25 (Oracle Corporation)"
)
@Component
public class StudentMapperImpl implements StudentMapper {

    @Override
    public Student toDto(StudentModel student) {
        if ( student == null ) {
            return null;
        }

        Student student1 = new Student();

        student1.setId( student.getId() );
        student1.setName( student.getName() );

        return student1;
    }

    @Override
    public StudentModel toEntity(Student dto) {
        if ( dto == null ) {
            return null;
        }

        StudentModel studentModel = new StudentModel();

        studentModel.setId( dto.getId() );
        studentModel.setName( dto.getName() );

        return studentModel;
    }

    @Override
    public List<Student> toDto(List<StudentModel> students) {
        if ( students == null ) {
            return null;
        }

        List<Student> list = new ArrayList<Student>( students.size() );
        for ( StudentModel studentModel : students ) {
            list.add( toDto( studentModel ) );
        }

        return list;
    }

    @Override
    public List<StudentModel> toEntity(List<Student> students) {
        if ( students == null ) {
            return null;
        }

        List<StudentModel> list = new ArrayList<StudentModel>( students.size() );
        for ( Student student : students ) {
            list.add( toEntity( student ) );
        }

        return list;
    }
}
