drop table if exists hibernate_sequence;

drop table if exists sparql_endpoint_status;

drop table if exists sparql_endpoint;

drop table if exists users_authorities;

drop table if exists authorities;

drop table if exists users;

create table authorities
(
    id   bigint auto_increment
        primary key,
    role varchar(255) null
);

create table hibernate_sequence
(
    next_val bigint null
);

create table sparql_endpoint
(
    id   bigint auto_increment
        primary key,
    name varchar(255) not null,
    url  varchar(255) not null,
    constraint UK_g2yy9i9eu120ffv3ko3yvwvo4
        unique (url)
);

create table sparql_endpoint_status
(
    id                 int                                 not null
        primary key,
    active             bit                                 not null,
    query_date         timestamp default CURRENT_TIMESTAMP not null,
    sparql_endpoint_id bigint                              null,
    constraint FKi6ceffuo3wdnlf1g9n76whfrx
        foreign key (sparql_endpoint_id) references sparql_endpoint (id)
);

create table users
(
    id                      bigint auto_increment
        primary key,
    account_non_expired     bit          not null,
    account_non_locked      bit          not null,
    credentials_non_expired bit          not null,
    enabled                 bit          not null,
    jwt_token               longtext     null,
    password                varchar(255) null,
    username                varchar(255) null,
    constraint UK_r43af9ap4edm43mmtq01oddj6
        unique (username)
);

create table users_authorities
(
    users_id       bigint not null,
    authorities_id bigint not null,
    primary key (users_id, authorities_id),
    constraint FK2cmfwo8tbjcpmltse0rh5ir0t
        foreign key (users_id) references users (id),
    constraint FKmfxncv8ke1jjgna64c8kclry5
        foreign key (authorities_id) references authorities (id)
);

