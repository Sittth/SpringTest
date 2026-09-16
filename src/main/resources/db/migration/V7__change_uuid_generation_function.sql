ALTER TABLE test.users
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE test.profiles
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE test.authors
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE test.books
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE test.courses
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE test.students
    ALTER COLUMN id SET DEFAULT gen_random_uuid();