# Initial schema

# --- !Ups

CREATE TABLE User (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    accessToken varchar(255),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE User;