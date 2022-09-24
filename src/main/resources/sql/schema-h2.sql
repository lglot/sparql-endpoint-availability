create sequence HIBERNATE_SEQUENCE;

create table AUTHORITIES
(
    ID   BIGINT auto_increment
        primary key,
    ROLE CHARACTER VARYING(255)
);

create table SPARQL_ENDPOINT
(
    ID   BIGINT auto_increment
        primary key,
    NAME CHARACTER VARYING(255) not null,
    URL  CHARACTER VARYING(255) not null
        constraint UK_G2YY9I9EU120FFV3KO3YVWVO4
        unique
);

create table SPARQL_ENDPOINT_STATUS
(
    ID                 INTEGER                             not null
        primary key,
    ACTIVE             BOOLEAN                             not null,
    QUERY_DATE         TIMESTAMP default CURRENT_TIMESTAMP not null,
    SPARQL_ENDPOINT_ID BIGINT,
    constraint FKI6CEFFUO3WDNLF1G9N76WHFRX
        foreign key (SPARQL_ENDPOINT_ID) references SPARQL_ENDPOINT
);

create table USERS
(
    ID                      BIGINT auto_increment
        primary key,
    ACCOUNT_NON_EXPIRED     BOOLEAN not null,
    ACCOUNT_NON_LOCKED      BOOLEAN not null,
    CREDENTIALS_NON_EXPIRED BOOLEAN not null,
    ENABLED                 BOOLEAN not null,
    JWT_TOKEN               CHARACTER LARGE OBJECT,
    PASSWORD                CHARACTER VARYING(255),
    USERNAME                CHARACTER VARYING(255)
        constraint UK_R43AF9AP4EDM43MMTQ01ODDJ6
        unique
);

create table USERS_AUTHORITIES
(
    USERS_ID       BIGINT not null,
    AUTHORITIES_ID BIGINT not null,
    primary key (USERS_ID, AUTHORITIES_ID),
    constraint FK2CMFWO8TBJCPMLTSE0RH5IR0T
        foreign key (USERS_ID) references USERS,
    constraint FKMFXNCV8KE1JJGNA64C8KCLRY5
        foreign key (AUTHORITIES_ID) references AUTHORITIES
);

