create schema aggregator

    create table aggregator.post (
                                     id serial primary key,
                                     name varchar(2000),
                                     text text,
                                     link varchar(2000) UNIQUE,
                                     created date
    )