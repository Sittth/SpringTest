package spring.ru.springtest.mapper;

import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.ru.springtest.dto.Course;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.models.StudentModel;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-27T20:27:18+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 25 (Oracle Corporation)"
)
@Component
public class CourseMapperImpl implements CourseMapper {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Course toDto(CourseModel course) {
        if ( course == null ) {
            return null;
        }

        Course course1 = new Course();

        course1.setId( course.getId() );
        course1.setTitle( course.getTitle() );
        course1.setStudents( studentMapper.toDto( course.getStudents() ) );

        return course1;
    }

    @Override
    public CourseModel toEntity(Course dto) {
        if ( dto == null ) {
            return null;
        }

        CourseModel courseModel = new CourseModel();

        courseModel.setId( dto.getId() );
        courseModel.setTitle( dto.getTitle() );
        courseModel.setStudents( studentMapper.toEntity( dto.getStudents() ) );

        linkStudentToCourse( courseModel );

        return courseModel;
    }

    @Override
    public void updateEntityFromDto(Course dto, CourseModel course) {
        if ( dto == null ) {
            return;
        }

        course.setId( dto.getId() );
        course.setTitle( dto.getTitle() );
        if ( course.getStudents() != null ) {
            List<StudentModel> list = studentMapper.toEntity( dto.getStudents() );
            if ( list != null ) {
                course.getStudents().clear();
                course.getStudents().addAll( list );
            }
            else {
                course.setStudents( null );
            }
        }
        else {
            List<StudentModel> list = studentMapper.toEntity( dto.getStudents() );
            if ( list != null ) {
                course.setStudents( list );
            }
        }

        linkStudentToCourse( course );
    }
}
