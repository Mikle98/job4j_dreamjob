create table candidates (
    id serial primary key,
    name varchar not null,
    description varchar not null,
    creationDate timestamp,
    cityId int references cities(id),
    fileId int references files(id)
);