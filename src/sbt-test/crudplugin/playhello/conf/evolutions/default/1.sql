# SELECT datetime( (request_start_date/1000), 'unixepoch','localtime') , datetime( (request_required_for_date/1000), 'unixepoch','localtime')  from request

# --- !Ups

create table "REQUEST" (
"REQUEST_ID" INTEGER PRIMARY KEY,
"REQUEST_NAME" VARCHAR NOT NULL,
"REQUEST_DESC" VARCHAR NOT NULL,
"REQUEST_START_DATE" TIMESTAMP ,
"REQUEST_REQUIRED_FOR_DATE" TIMESTAMP
);


insert into "REQUEST" values (0,"NA","NA",strftime('%s', 'now'),strftime('%s', 'now'));

# --- !Downs

drop table REQUEST;
