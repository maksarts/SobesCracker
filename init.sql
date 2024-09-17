-- BASE TABLES

create table if not exists tg_user(
                                      id uuid primary key not null,
                                      nickname varchar(50),
                                      name text,
                                      chat_id int not null,
                                      created_at timestamp with time zone
);

create table if not exists question(
                                       id uuid primary key not null,
                                       type uuid not null,
                                       content text unique not null,
                                       answer text not null,
                                       grade int not null,
                                       created_at timestamp with time zone
);

create table if not exists subscription(
                                           id uuid primary key not null,
                                           user_id uuid not null,
                                           course_id uuid not null,
                                           created_at timestamp with time zone
);

create table if not exists excluded_question(
                                                user_id uuid not null,
                                                question_id uuid not null,
                                                primary key (user_id, question_id)
);

create table if not exists settings(
                                       id uuid primary key not null,
                                       user_id uuid unique not null,
                                       setting jsonb
);

create table if not exists course(
                                     id uuid primary key not null,
                                     name varchar(50) not null unique,
                                     min_grade int,
                                     max_grade int
);

create table if not exists type(
                                   id uuid primary key not null,
                                   name varchar(50) not null unique
);

create table if not exists course_type(
                                          course_id uuid not null,
                                          type_id uuid not null,
                                          primary key (course_id, type_id)
);
