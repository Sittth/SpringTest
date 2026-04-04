package spring.ru.springtest.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.StudentRequestCreate;
import spring.ru.springtest.dto.StudentResponse;
import spring.ru.springtest.models.StudentModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-04T15:16:40+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 25 (Oracle Corporation)"
)
@Component
public class StudentMapperImpl implements StudentMapper {

    @Override
    public StudentResponse toResponse(StudentModel student) {
        if ( student == null ) {
            return null;
        }

        StudentResponse studentResponse = new StudentResponse();

        studentResponse.setId( student.getId() );
        studentResponse.setName( student.getName() );

        return studentResponse;
    }

    @Override
    public List<StudentResponse> toResponses(List<StudentModel> students) {
        if ( students == null ) {
            return null;
        }

        List<StudentResponse> list = new ArrayList<StudentResponse>( students.size() );
        for ( StudentModel studentModel : students ) {
            list.add( toResponse( studentModel ) );
        }

        return list;
    }

    @Override
    public StudentModel toEntity(StudentRequestCreate requestCreate) {
        if ( requestCreate == null ) {
            return null;
        }

        StudentModel studentModel = new StudentModel();

        studentModel.setName( requestCreate.getName() );

        return studentModel;
    }

    @Override
    public List<StudentModel> toEntity(List<StudentRequestCreate> requests) {
        if ( requests == null ) {
            return null;
        }

        List<StudentModel> list = new ArrayList<StudentModel>( requests.size() );
        for ( StudentRequestCreate studentRequestCreate : requests ) {
            list.add( toEntity( studentRequestCreate ) );
        }

        return list;
    }
}
