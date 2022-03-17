create

create table USERS (
    id uuid not null primary key,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    age integer not null,
    login varchar(50) unique not null,
    email varchar(255) unique not null,
    registration_date date
);

INSERT INTO USERS VALUES (random_uuid(),  'Default', 'User', 18, 'defUser', 'default.user@gmail.com', '2022-03-016');
INSERT INTO USERS VALUES (random_uuid(),  'Default', 'User', 18, 'defser', 'deault.user@gmail.com', '2022-03-016');

SELECT * FROM USERS;

drop table USERS;