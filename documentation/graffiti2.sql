
CREATE SEQUENCE eagle_inscription_id_seq;

create table EAGLE_inscriptions (
    id integer NOT NULL default nextval('eagle_inscription_id_seq'),
    --- Fill in from EAGLE database
    eagle_id character(9) NOT NULL,
    ancient_city character varying(30),
    find_spot character varying(100),
    measurements character varying(100),
    writing_style character varying(30),
    "language" character varying(30),
    content text,
    bibliography character varying(250), -- make 250
    image text,
    PRIMARY KEY(id),
    UNIQUE (eagle_id)
   );

CREATE SEQUENCE property_type_id_seq;

create table propertyTypes (
	id integer NOT NULL default nextval('property_type_id_seq'),
	name varchar(30),
	description varchar(100),
    PRIMARY KEY(id),
    UNIQUE(name)
);

CREATE SEQUENCE property_id_seq;

create table properties (
	id integer NOT NULL default nextval('property_id_seq'),
	modern_city varchar(50),
 	insula varchar(10) ,
 	property_number varchar(20),
 	property_name varchar(70), 
    PRIMARY KEY(id),
    UNIQUE(modern_city, insula, property_number)
);

create table propertyToPropertyType (
	property_id integer REFERENCES properties(id),
    property_type integer REFERENCES propertyTypes(id),
    UNIQUE(property_id, property_type)
);

CREATE SEQUENCE inscription_id_seq;

create table AGP_inscription_annotations (
	id integer NOT NULL default nextval('inscription_id_seq'),
	eagle_id char(9),
	floor_to_graffito_height varchar(30),
	description text,
	comment text,
	translation text,
	--- replace with property id
	modern_city_find_spot varchar(50),
	property_name_find_spot varchar(40), 
 	insula_find_spot varchar(10) ,
 	property_number_find_spot integer,    
 	property_id integer references properties(id),s
	FOREIGN KEY ( eagle_id ) REFERENCES EAGLE_inscriptions(eagle_id),
	PRIMARY KEY (id),
	UNIQUE(eagle_id)
);



CREATE SEQUENCE drawing_tag_id_seq;

create table drawing_tags (
    id integer NOT NULL default nextval('drawing_tag_id_seq'),
    name varchar(30),
    description varchar(100),
    PRIMARY KEY(id),
    UNIQUE(name)
);

CREATE SEQUENCE graffitoToDrawingTags_id_seq;

create table graffitoToDrawingTags (
    id integer NOT NULL default nextval('graffitoToDrawingTags_id_seq'),
    graffito_id char(9) NOT NULL,
    drawing_tag_id integer NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY ( graffito_id ) REFERENCES eagle_inscriptions(eagle_id),
    FOREIGN KEY ( drawing_tag_id ) REFERENCES drawing_tags(id),
    UNIQUE(graffito_id, drawing_tag_id)
);