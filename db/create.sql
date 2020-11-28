create schema aggregator

    create table aggregator.post (
                                     id serial primary key,
                                     name varchar(2000),
                                     text text,
                                     lint varchar(2000),
                                     created date
    )