DROP TABLE if exists comments;
DROP TABLE if exists requests;
DROP TABLE IF EXISTS bookings;
DROP TABLE if exists items;
DROP TABLE if exists users;

CREATE  table If not exists users(
id integer generated by default as identity primary key,
name varchar(100) not null,
email varchar(100) not null unique);

CREATE TABLE if not exists items(
id integer generated by default as identity primary key,
name varchar(100) not null,
description varchar(200) not null,
is_available boolean not null,
owner_id integer references users(id),
request_id integer);

CREATE TABLE if not exists bookings(
id integer generated by default as identity primary key,
start_date timestamp without time zone,
end_date timestamp without time zone,
item_id integer references items(id),
booker_id integer references users(id),
status varchar(20));

CREATE TABLE If not exists requests(
id integer generated by default as identity primary key,
description varchar(200) not null,
requester_id integer references users(id));

CREATE TABLE if not exists comments(
id integer generated by default as identity primary key,
text varchar(1000) not null,
item_id integer references items(id),
author_id integer references users(id));


