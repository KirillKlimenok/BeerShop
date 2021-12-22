create table users_token(
    id bigserial primary key not null,
    token varchar not null,
    id_user bigint not null
);


create table users_list(
    id bigserial primary key unique not null,
    login varchar(100) unique not null,
    password varchar not null
);
