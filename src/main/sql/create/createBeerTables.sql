create sequence id_beer_type as integer increment by 1 minvalue 0 no maxvalue start with 0;

create table beer_types
(
    id        integer unique not null primary key default nextval('id_beer_type'),
    type_name varchar(50)    not null unique
);

alter sequence id_beer_type owned by beer_types.id;

--

create sequence id_beer_containers as integer increment by 1 minvalue 0 no maxvalue start with 0;

create table beer_containers
(
    id        integer primary key not null unique default nextval('id_beer_containers'),
    name_type varchar(50)         not null,
    volume    numeric(4, 2)       not null,
    check ( volume > 0 )
);

alter sequence id_beer_containers owned by beer_containers.id;

--

create sequence id_beer as bigint increment by 1 minvalue 0 start with 0;

create table beers
(
    id               bigint primary key unique          not null default nextval('id_beer'),
    name             varchar(50)                        not null,
    id_container     integer references beer_containers not null,
    id_beer_type     integer references beer_types      not null,
    alcohol_content  numeric(4, 2)                      not null,
    check ( alcohol_content > 0.1 and beers.alcohol_content < 30),
    ibu              integer                            not null,
    check (ibu > 0 and beers.ibu < 200),
    count_containers bigint,
    check ( count_containers >= 0 )
);
