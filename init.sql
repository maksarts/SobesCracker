create table if not exists question(
                                       id uuid primary key not null,
                                       content text unique not null,
                                       answer text not null,
                                       type varchar(50) not null,
                                       grade int not null,
                                       created_at timestamp with time zone
);

create table if not exists subscriber(
                                         id uuid primary key not null,
                                         nickname varchar(50),
                                         name varchar(50),
                                         chat_id varchar(50) not null,
                                         created_at timestamp with time zone
);

create table if not exists excluded_question(
                                                id uuid primary key not null,
                                                user_id uuid not null,
                                                question_id uuid not null,
                                                created_at timestamp with time zone
);

create table if not exists settings(
                                       id uuid primary key not null,
                                       user_id uuid not null,
                                       setting jsonb
);