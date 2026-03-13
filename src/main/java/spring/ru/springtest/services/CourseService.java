package spring.ru.springtest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.springtest.models.CourseModel;
import spring.ru.springtest.repositories.CourseRepository;

import java.util.ArrayList;
import java.util.UUID;

@Service
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public CourseModel findById(UUID id) {
        return courseRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public void save(CourseModel course) {
        if (course.getStudents() != null) {
            course.getStudents().forEach(student -> {

                if (student.getCourses() == null) {
                    student.setCourses(new ArrayList<>());
                }

                student.getCourses().add(course);
            });
        }

        courseRepository.save(course);
    }

    public void update(UUID id, CourseModel updatedCourse) {

        CourseModel existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        existingCourse.setTitle(updatedCourse.getTitle());

        existingCourse.getStudents().clear();

        if (updatedCourse.getStudents() != null) {
            updatedCourse.getStudents().forEach(student -> {
                existingCourse.getStudents().add(student);

                if (student.getCourses() == null) {
                    student.setCourses(new ArrayList<>());
                }
                student.getCourses().add(existingCourse);
            });
        }

        courseRepository.save(existingCourse);
    }

    public void delete(UUID id) {
        courseRepository.deleteById(id);
    }
}
