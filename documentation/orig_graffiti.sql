
CREATE SEQUENCE inscription_id_seq;

create table inscriptions (
    id integer NOT NULL default nextval('inscription_id_seq'),
    eagle_id char(9) NOT NULL,
    ancient_city varchar(30),
    find_spot varchar(100),
    measurements varchar(100),
    writing_style varchar(30),
    "language" varchar(30),
    content text,
    bibliography varchar(100),
    PRIMARY KEY(id),
    UNIQUE (eagle_id)
);

CREATE SEQUENCE tag_id_seq;

create table tags (
    id integer NOT NULL default nextval('tag_id_seq'),
    name varchar(30),
    PRIMARY KEY(id)
);

CREATE SEQUENCE inscriptionsToTags_id_seq;

create table inscriptionsToTags (
    id integer NOT NULL default nextval('inscriptionsToTags_id_seq'),
    inscription_id integer NOT NULL,
    tag_id integer NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY ( inscription_id ) REFERENCES inscriptions(id),
    FOREIGN KEY ( tag_id ) REFERENCES tags(id),
    UNIQUE(inscription_id, tag_id)
);