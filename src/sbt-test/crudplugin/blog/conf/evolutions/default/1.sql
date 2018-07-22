# SELECT datetime( (request_start_date/1000), 'unixepoch','localtime') , datetime( (request_required_for_date/1000), 'unixepoch','localtime')  from request

# --- !Ups

create table "USER" (
"USER_ID" INTEGER PRIMARY KEY,
"USER_NAME" VARCHAR NOT NULL,
"USER_PASSWORD" VARCHAR NOT NULL,
"USER_HANDLE" VARCHAR NOT NULL
);

create table "BLOG" (
"BLOG_ID" INTEGER PRIMARY KEY,
"BLOG_USER_ID" INTEGER NOT NULL,
"BLOG_USER_HANDLE" VARCHAR NOT NULL ,
"BLOG_CREATED" TIMESTAMP NOT NULL,
"BLOG_TEXT" VARCHAR NOT NULL,
 FOREIGN KEY("BLOG_USER_ID") REFERENCES "USER"("USER_ID")
);

insert into "USER" ("USER_NAME","USER_PASSWORD","USER_HANDLE") VALUES ("bernard","jason","bernie");

# --- !Downs

drop table BLOG;
drop table USER;
