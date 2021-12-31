create table users_list(
    id uuid primary key default uuid_generate_v4(),
    login varchar(100) unique not null,
    email varchar(100) unique not null,
    password varchar not null
);

create table users_token(
    id bigserial primary key not null,
    token uuid not null references users_list(id)
);
