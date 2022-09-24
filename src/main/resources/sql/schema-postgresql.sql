drop sequence if exists hibernate_sequence;

drop table if exists sparql_endpoint_status;

drop table if exists sparql_endpoint;

drop table if exists users_authorities;

drop table if exists authorities;

drop table if exists users;


create sequence hibernate_sequence;

alter sequence hibernate_sequence owner to root;

create table authorities
(
    id   bigserial
        primary key,
    role varchar(255)
);

alter table authorities
    owner to root;

create table sparql_endpoint
(
    id   bigserial
        primary key,
    name varchar(255) not null,
    url  varchar(255) not null
        constraint uk_g2yy9i9eu120ffv3ko3yvwvo4
        unique
);

alter table sparql_endpoint
    owner to root;

create table sparql_endpoint_status
(
    id                 integer                             not null
        primary key,
    active             boolean                             not null,
    query_date         timestamp default CURRENT_TIMESTAMP not null,
    sparql_endpoint_id bigint
        constraint fki6ceffuo3wdnlf1g9n76whfrx
        references sparql_endpoint
);

alter table sparql_endpoint_status
    owner to root;

create table users
(
    id                      bigserial
        primary key,
    account_non_expired     boolean not null,
    account_non_locked      boolean not null,
    credentials_non_expired boolean not null,
    enabled                 boolean not null,
    jwt_token               oid,
    password                varchar(255),
    username                varchar(255)
        constraint uk_r43af9ap4edm43mmtq01oddj6
        unique
);

alter table users
    owner to root;

create table users_authorities
(
    users_id       bigint not null
        constraint fk2cmfwo8tbjcpmltse0rh5ir0t
        references users,
    authorities_id bigint not null
        constraint fkmfxncv8ke1jjgna64c8kclry5
        references authorities,
    primary key (users_id, authorities_id)
);

alter table users_authorities
    owner to root;

