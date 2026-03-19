CREATE TABLE IF NOT EXISTS authors (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    name text
);

CREATE TABLE IF NOT EXISTS books (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    title text,
    author_id uuid,
    CONSTRAINT fk_books_author
        FOREIGN KEY (author_id)
            REFERENCES authors(id)
            ON DELETE CASCADE
);