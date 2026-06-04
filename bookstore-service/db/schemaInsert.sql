INSERT INTO baskets (total_price, quantity_books, created_at, updated_at)
VALUES
    (59.40, 4, '2026-05-20 10:00:00', '2026-05-21 11:00:00'),
    (405.00, 3, '2026-03-10 14:00:00', '2026-04-09 13:00:00'),
    (6500.00, 5, '2026-02-13 12:00:00', '2026-05-08 09:00:00');

CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO accounts (basket_id, name, surname, middle_name, email, phone_number, password)
VALUES
    (1, 'Ivan', 'Ivanov', 'Ivanovich', 'email1@mail.ru', '+7952346670', crypt('password1', gen_salt('bf'))),
    (2, 'Petr', 'Petrov', NULL, 'email2@mail.ru', '+7923579935', crypt('password2', gen_salt('bf')));

INSERT INTO authors (name, surname, middle_name, pseudonym)
VALUES
    ('Aizek', 'Azimov', NULL, 'Aizek'),
    ('Boris', 'Akunin', NULL, 'Boric'),
    ('Anastasia', 'Konovalova', 'Alexsandrovna', 'Nastia');

INSERT INTO publishers (name, description, country, created_at, updated_at)
VALUES
    ('Exmo','Description1', 'USA', '1940-05-21 11:00:00', '2026-05-22 10:00:00'),
    ('Line', 'Description2', 'Canada', '1990-05-15 11:00:00', '2026-05-20 10:00:00'),
    ('Battom', 'Description3', 'Russia', '2002-01-10 13:00:00', '2026-01-20 20:30:00');

INSERT INTO publisher_series (publisher_id, name, created_at, updated_at)
VALUES
    (1, 'Harry Potter', '2000-05-21 11:00:00', '2026-04-14 10:00:00'),
    (2, 'Foundation Universe', '1950-05-21 11:00:00', '2026-05-17 10:00:00'),
    (3, 'Smile', '2002-01-10 13:00:00', '2002-09-10 13:00:00');

INSERT INTO books (title, author_id, genre, publisher_id, series_id, price,
isbn, release_date, description, pages, weight, review_count, average_rating)
VALUES
    ('Harry Potter and the Chamber of Secrets', 2, 'DRAMA', 1, 1, 500.00, '234-324-234-43', '2001-05-21 11:00:00', 'Description1', 400, 300.00, 0, 0),
    ('I''m robot', 1, 'HORROR', 2, 2, 650.00, '213-231-679-67', '1950-05-21 11:00:00', 'Description2', 300, 300.00, 0, 0),
    ('Child', 2, 'COMEDY', 2, 2, 1050.00, '335-456-245-54', '2001-06-13 19:00:00', 'Description3', 500, 700.00, 0, 0),
    ('Cats and Dogs', 3, 'COMEDY', 3, 3, 1550.00, '341-234-567-89', '2019-09-05 14:00:00', 'Description4', 400, 500.00, 0, 0);

INSERT INTO basket_detail (basket_id, book_id, price, quantity)
VALUES
    (1, 1, 500.00, 1),
    (2, 2, 650.00, 1);

INSERT INTO book_reviews (book_id, reviewer_id, text, rating, created_at, updated_at)
VALUES
    (1, 1, 'Good', 4, '2010-05-21 11:00:00','2015-06-23 11:34:00'),
    (2, 2, 'Awesome', 5, '2020-10-21 12:00:00',NULL);

INSERT INTO favorite_books (account_id, book_id)
VALUES
    (1, 1),
    (2, 2);

INSERT INTO orders (account_id, recipient_name, created_at, updated_at)
VALUES
    (1, 'Ivan Ivanov', '2025-03-19 11:00:00', NULL),
    (2, 'Ivan Petrov', '2026-09-19 20:00:00', NULL);

INSERT INTO auth (email, password)
VALUES
    ('email@email', '324567'),
    ('email2@email', 'DFRFS#4455e7')
