package spring.ru.springtest.mapper;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.CourseRequestCreate;
import spring.ru.springtest.dto.CourseRequestUpdate;
import spring.ru.springtest.dto.CourseResponse;
import spring.ru.springtest.models.CourseModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-14T16:20:42+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class CourseMapperImpl implements CourseMapper {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public CourseResponse toResponse(CourseModel course) {
        if ( course == null ) {
            return null;
        }

        CourseResponse courseResponse = new CourseResponse();

        courseResponse.setId( course.getId() );
        courseResponse.setTitle( course.getTitle() );
        courseResponse.setStudents( studentMapper.toResponses( course.getStudents() ) );

        return courseResponse;
    }

    @Override
    public CourseModel toEntity(CourseRequestCreate requestCreate) {
        if ( requestCreate == null ) {
            return null;
        }

        CourseModel courseModel = new CourseModel();

        courseModel.setTitle( requestCreate.getTitle() );
        courseModel.setStudents( studentMapper.toEntity( requestCreate.getStudents() ) );

        return courseModel;
    }

    @Override
    public void updateEntityFromDto(CourseRequestUpdate dto, CourseModel course) {
        if ( dto == null ) {
            return;
        }

        course.setTitle( dto.getTitle() );
    }
}
