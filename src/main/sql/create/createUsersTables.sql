create table users_token(
    id bigserial primary key not null,
    token varchar not null
);


create table users_list(
    id bigserial primary key unique not null,
    login varchar(100) unique not null,
    password_hash varchar not null,
    id_user_token bigint references users_token unique not null
);
