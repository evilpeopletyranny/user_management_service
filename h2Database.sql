create table USERS (
    id bigint auto_increment not null primary key,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    age integer not null,
    login varchar(50) not null,
    email varchar(255) not null,
    registration_date smalldatetime
);

INSERT INTO USERS VALUES (0,  'Default', 'User', 18, 'defUser', 'default.user@gmail.com', '2022-03-016 15:35:00');

drop table USERS;