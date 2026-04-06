CREATE TABLE IF NOT EXISTS courses (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    title text NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS students (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    name text NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS course_student (
    course_id uuid NOT NULL,
    student_id uuid NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (course_id, student_id),

    CONSTRAINT fk_course_student_course
        FOREIGN KEY(course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_course_student_student
        FOREIGN KEY(student_id)
        REFERENCES students(id)
        ON DELETE CASCADE
);