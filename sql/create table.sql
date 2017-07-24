create table play_catchup.notebook (
    id    int auto_increment primary key,
    title varchar(190),
    main_text varchar(10000),
    updated_at datetime,
    created_at datetime not null
);

create table play_catchup.tag (
    id int auto_increment primary key, 
    name varchar(100),
    updated_at datetime,
    created_at datetime not null
);

create table play_catchup.tag_mapping (
    notebook_id int not null,
    tag_id int not null,
    foreign key (notebook_id) references notebook(id),
    foreign key (tag_id) references tag_mst(id)
);