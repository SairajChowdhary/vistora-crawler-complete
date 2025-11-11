DROP TABLE IF EXISTS book_authors;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;
CREATE TABLE authors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  email VARCHAR(200),
  active TINYINT(1) DEFAULT 1
);
CREATE TABLE books (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  price DECIMAL(10,2),
  published_at DATETIME,
  genre ENUM('Fiction','NonFiction','Poetry') NOT NULL
);
CREATE TABLE book_authors (
  author_id BIGINT NOT NULL,
  book_id BIGINT NOT NULL,
  PRIMARY KEY (author_id, book_id),
  CONSTRAINT fk_book_author_author FOREIGN KEY (author_id) REFERENCES authors(id),
  CONSTRAINT fk_book_author_book FOREIGN KEY (book_id) REFERENCES books(id)
);
