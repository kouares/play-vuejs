create table play_catchup.notebook (
    id    int auto_increment primary key,
    title varchar(190),
    main_text varchar(10000),
    upadted_at datetime,
    created_at datetime not null
);

create table play_catchup.tag (
    id int auto_increment primary key, 
    name varchar(100)
);

create table play_catchup.tag_mapping (
    title varchar(255) not null,
    tag_id int,
    foreign key (tag_id) references tag_mst(id)
);