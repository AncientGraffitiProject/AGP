--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.7
-- Dumped by pg_dump version 9.5.7

SET statement_timeout = 0;
SET lock_timeout = 0;\
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: inscription_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE inscription_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE inscription_id_seq OWNER TO sprenkle;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: agp_inscription_info; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE agp_inscription_info (
    id integer DEFAULT nextval('inscription_id_seq'::regclass) NOT NULL,
    edr_id character(9),
    comment text,
    content_translation text,
    lang_in_english character varying(30),
    writing_style_in_english character varying(30),
    height_from_ground character varying(30),
    graffito_height character varying(30),
    graffito_length character varying(30),
    letter_height_min character varying(20),
    letter_height_max character varying(20),
    property_id integer,
    cil character varying(100),
    langner character varying(100),
    has_figural_component boolean DEFAULT false,
    individual_letter_heights text,
    summary character varying(200),
    is_greatest_hit_translation boolean DEFAULT false,
    is_greatest_hit_figural boolean DEFAULT false,
    content_epidocified text,
    letter_with_flourishes_height_min character varying(10),
    letter_with_flourishes_height_max character varying(10)
);


ALTER TABLE agp_inscription_info OWNER TO sprenkle;

--
-- Name: city_key_sequence; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE city_key_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE city_key_sequence OWNER TO sprenkle;

--
-- Name: cities; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE cities (
    id integer DEFAULT nextval('city_key_sequence'::regclass) NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(300),
    pleiades_id character varying(15) NOT NULL
);


ALTER TABLE cities OWNER TO sprenkle;

--
-- Name: drawing_tag_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE drawing_tag_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE drawing_tag_id_seq OWNER TO sprenkle;

--
-- Name: drawing_tags; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE drawing_tags (
    id integer DEFAULT nextval('drawing_tag_id_seq'::regclass) NOT NULL,
    name character varying(30),
    description character varying(100)
);


ALTER TABLE drawing_tags OWNER TO sprenkle;

--
-- Name: edr_inscription_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE edr_inscription_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE edr_inscription_id_seq OWNER TO sprenkle;

--
-- Name: edr_inscriptions; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE edr_inscriptions (
    id integer DEFAULT nextval('edr_inscription_id_seq'::regclass) NOT NULL,
    edr_id character(9) NOT NULL,
    ancient_city character varying(30),
    find_spot character varying(150),
    measurements character varying(100),
    writing_style character varying(30),
    language character varying(30),
    content text,
    bibliography text,
    apparatus text,
    apparatus_displayed text
);


ALTER TABLE edr_inscriptions OWNER TO sprenkle;

--
-- Name: figural_graffiti_info; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE figural_graffiti_info (
    edr_id character(9) NOT NULL,
    description_in_latin text,
    description_in_english text
);


ALTER TABLE figural_graffiti_info OWNER TO sprenkle;

--
-- Name: graffitotodrawingtags_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE graffitotodrawingtags_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE graffitotodrawingtags_id_seq OWNER TO sprenkle;

--
-- Name: graffitotodrawingtags; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE graffitotodrawingtags (
    id integer DEFAULT nextval('graffitotodrawingtags_id_seq'::regclass) NOT NULL,
    graffito_id character(9) NOT NULL,
    drawing_tag_id integer NOT NULL
);


ALTER TABLE graffitotodrawingtags OWNER TO sprenkle;

--
-- Name: greatest_hits_info; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE greatest_hits_info (
    edr_id character(9) NOT NULL,
    commentary text,
    preferred_image character varying(30)
);


ALTER TABLE greatest_hits_info OWNER TO sprenkle;

--
-- Name: insula_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE insula_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE insula_id_seq OWNER TO sprenkle;

--
-- Name: insula; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE insula (
    id integer DEFAULT nextval('insula_id_seq'::regclass) NOT NULL,
    modern_city character varying(30),
    short_name character varying(20),
    full_name character varying(100),
    pleiades_id character varying(15)
);


ALTER TABLE insula OWNER TO sprenkle;

--
-- Name: property_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE property_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE property_id_seq OWNER TO sprenkle;

--
-- Name: properties; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE properties (
    id integer DEFAULT nextval('property_id_seq'::regclass) NOT NULL,
    property_number character varying(20),
    additional_properties character varying(20),
    property_name character varying(70),
    italian_property_name character varying(70),
    insula_id integer,
    pleiades_id character varying(15),
    commentary text,
    is_insula_based boolean
);


ALTER TABLE properties OWNER TO sprenkle;

--
-- Name: property_type_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE property_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE property_type_id_seq OWNER TO sprenkle;

--
-- Name: propertytopropertytype; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE propertytopropertytype (
    property_id integer NOT NULL,
    property_type integer NOT NULL
);


ALTER TABLE propertytopropertytype OWNER TO sprenkle;

--
-- Name: propertytypes; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE propertytypes (
    id integer DEFAULT nextval('property_type_id_seq'::regclass) NOT NULL,
    name character varying(30),
    description character varying(100)
);


ALTER TABLE propertytypes OWNER TO sprenkle;

create table property_links ( 
	property_id integer references properties(id) on delete cascade, 
	link_name varchar(70), 
	link varchar(200)
);

create table photos (
	edr_id character(9) references edr_inscriptions (edr_id) on delete cascade,
	photo_id varchar(20),
	UNIQUE(edr_id, photo_id)
);


--
-- Name: roles; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE roles (
    id integer,
    role character varying(30)
);


ALTER TABLE roles OWNER TO sprenkle;

--
-- Name: user_ids_sequence; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE user_ids_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_ids_sequence OWNER TO sprenkle;

--
-- Name: users; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE users (
    id integer DEFAULT nextval('user_ids_sequence'::regclass) NOT NULL,
    password character varying(64) NOT NULL,
    username character varying(20) NOT NULL,
    name character varying(30),
    role character varying(30),
    enabled boolean
);


ALTER TABLE users OWNER TO sprenkle;

--
-- Name: agp_inscription_annotations_edr_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY agp_inscription_info
    ADD CONSTRAINT agp_inscription_annotations_edr_id_key UNIQUE (edr_id);


--
-- Name: agp_inscription_annotations_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY agp_inscription_info
    ADD CONSTRAINT agp_inscription_annotations_pkey PRIMARY KEY (id);


--
-- Name: cities_unique_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY cities
    ADD CONSTRAINT cities_unique_key UNIQUE (name);


--
-- Name: city_primary_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY cities
    ADD CONSTRAINT city_primary_key PRIMARY KEY (id);


--
-- Name: drawing_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY drawing_tags
    ADD CONSTRAINT drawing_tags_pkey PRIMARY KEY (id);


--
-- Name: edr_inscriptions_edr_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY edr_inscriptions
    ADD CONSTRAINT edr_inscriptions_edr_id_key UNIQUE (edr_id);


--
-- Name: edr_inscriptions_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY edr_inscriptions
    ADD CONSTRAINT edr_inscriptions_pkey PRIMARY KEY (id);


--
-- Name: figural_graffiti_info_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY figural_graffiti_info
    ADD CONSTRAINT figural_graffiti_info_pkey PRIMARY KEY (edr_id);


--
-- Name: graffitotodrawingtags_graffito_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY graffitotodrawingtags
    ADD CONSTRAINT graffitotodrawingtags_graffito_id_key UNIQUE (graffito_id, drawing_tag_id);


--
-- Name: graffitotodrawingtags_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY graffitotodrawingtags
    ADD CONSTRAINT graffitotodrawingtags_pkey PRIMARY KEY (id);


--
-- Name: greatest_hits_primary_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY greatest_hits_info
    ADD CONSTRAINT greatest_hits_primary_key PRIMARY KEY (edr_id);


--
-- Name: insula_modern_city_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY insula
    ADD CONSTRAINT insula_modern_city_key UNIQUE (modern_city, short_name);


--
-- Name: insula_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY insula
    ADD CONSTRAINT insula_pkey PRIMARY KEY (id);


--
-- Name: properties_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY properties
    ADD CONSTRAINT properties_pkey PRIMARY KEY (id);


--
-- Name: properties_uniq_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY properties
    ADD CONSTRAINT properties_uniq_key UNIQUE (insula_id, property_number);


--
-- Name: propertyToPropertyTypes_primary_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY propertytopropertytype
    ADD CONSTRAINT "propertyToPropertyTypes_primary_key" PRIMARY KEY (property_id, property_type);


--
-- Name: propertytypes_name_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY propertytypes
    ADD CONSTRAINT propertytypes_name_key UNIQUE (name);


--
-- Name: propertytypes_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY propertytypes
    ADD CONSTRAINT propertytypes_pkey PRIMARY KEY (id);


--
-- Name: unique_drawing_tag_name; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY drawing_tags
    ADD CONSTRAINT unique_drawing_tag_name UNIQUE (name);


--
-- Name: agp_inscription_annotations_edr_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY agp_inscription_info
    ADD CONSTRAINT agp_inscription_annotations_edr_id_fkey FOREIGN KEY (edr_id) REFERENCES edr_inscriptions(edr_id) ON DELETE CASCADE;


--
-- Name: agp_inscription_annotations_properies_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY agp_inscription_info
    ADD CONSTRAINT agp_inscription_annotations_properies_fkey FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE;


--
-- Name: featured_graffiti_edr_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY greatest_hits_info
    ADD CONSTRAINT featured_graffiti_edr_fkey FOREIGN KEY (edr_id) REFERENCES agp_inscription_info(edr_id) ON DELETE CASCADE;


--
-- Name: figural_graffiti_info_edr_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY figural_graffiti_info
    ADD CONSTRAINT figural_graffiti_info_edr_id_fkey FOREIGN KEY (edr_id) REFERENCES edr_inscriptions(edr_id) ON DELETE CASCADE;


--
-- Name: graffitotodrawingtags_drawing_tag_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY graffitotodrawingtags
    ADD CONSTRAINT graffitotodrawingtags_drawing_tag_id_fkey FOREIGN KEY (drawing_tag_id) REFERENCES drawing_tags(id);


--
-- Name: graffitotodrawingtags_graffito_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY graffitotodrawingtags
    ADD CONSTRAINT graffitotodrawingtags_graffito_id_fkey FOREIGN KEY (graffito_id) REFERENCES edr_inscriptions(edr_id) ON DELETE CASCADE;


--
-- Name: greatest_hits_fk; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY greatest_hits_info
    ADD CONSTRAINT greatest_hits_fk FOREIGN KEY (edr_id) REFERENCES edr_inscriptions(edr_id);


--
-- Name: properties_insula_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY properties
    ADD CONSTRAINT properties_insula_id_fkey FOREIGN KEY (insula_id) REFERENCES insula(id) ON DELETE CASCADE;


--
-- Name: propertytopropertytype_property_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY propertytopropertytype
    ADD CONSTRAINT propertytopropertytype_property_id_fkey FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE;


--
-- Name: propertytopropertytype_property_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY propertytopropertytype
    ADD CONSTRAINT propertytopropertytype_property_type_fkey FOREIGN KEY (property_type) REFERENCES propertytypes(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: inscription_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE inscription_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE inscription_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE inscription_id_seq TO sprenkle;


--
-- Name: agp_inscription_info; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE agp_inscription_info FROM PUBLIC;
REVOKE ALL ON TABLE agp_inscription_info FROM sprenkle;
GRANT ALL ON TABLE agp_inscription_info TO sprenkle;


--
-- Name: cities; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE cities FROM PUBLIC;
REVOKE ALL ON TABLE cities FROM sprenkle;
GRANT ALL ON TABLE cities TO sprenkle;
GRANT SELECT,REFERENCES,UPDATE ON TABLE cities TO PUBLIC;


--
-- Name: drawing_tag_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE drawing_tag_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE drawing_tag_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE drawing_tag_id_seq TO sprenkle;


--
-- Name: drawing_tags; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE drawing_tags FROM PUBLIC;
REVOKE ALL ON TABLE drawing_tags FROM sprenkle;
GRANT ALL ON TABLE drawing_tags TO sprenkle;


--
-- Name: edr_inscription_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE edr_inscription_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE edr_inscription_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE edr_inscription_id_seq TO sprenkle;


--
-- Name: edr_inscriptions; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE edr_inscriptions FROM PUBLIC;
REVOKE ALL ON TABLE edr_inscriptions FROM sprenkle;
GRANT ALL ON TABLE edr_inscriptions TO sprenkle;


--
-- Name: figural_graffiti_info; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE figural_graffiti_info FROM PUBLIC;
REVOKE ALL ON TABLE figural_graffiti_info FROM sprenkle;
GRANT ALL ON TABLE figural_graffiti_info TO sprenkle;


--
-- Name: graffitotodrawingtags_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE graffitotodrawingtags_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE graffitotodrawingtags_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE graffitotodrawingtags_id_seq TO sprenkle;


--
-- Name: graffitotodrawingtags; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE graffitotodrawingtags FROM PUBLIC;
REVOKE ALL ON TABLE graffitotodrawingtags FROM sprenkle;
GRANT ALL ON TABLE graffitotodrawingtags TO sprenkle;


--
-- Name: greatest_hits_info; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE greatest_hits_info FROM PUBLIC;
REVOKE ALL ON TABLE greatest_hits_info FROM sprenkle;
GRANT ALL ON TABLE greatest_hits_info TO sprenkle;
GRANT SELECT,INSERT,UPDATE ON TABLE greatest_hits_info TO PUBLIC;


--
-- Name: insula_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE insula_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE insula_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE insula_id_seq TO sprenkle;


--
-- Name: insula; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE insula FROM PUBLIC;
REVOKE ALL ON TABLE insula FROM sprenkle;
GRANT ALL ON TABLE insula TO sprenkle;


--
-- Name: property_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE property_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE property_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE property_id_seq TO sprenkle;


--
-- Name: properties; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE properties FROM PUBLIC;
REVOKE ALL ON TABLE properties FROM sprenkle;
GRANT ALL ON TABLE properties TO sprenkle;


--
-- Name: property_type_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE property_type_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE property_type_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE property_type_id_seq TO sprenkle;


--
-- Name: propertytopropertytype; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE propertytopropertytype FROM PUBLIC;
REVOKE ALL ON TABLE propertytopropertytype FROM sprenkle;
GRANT ALL ON TABLE propertytopropertytype TO sprenkle;


--
-- Name: propertytypes; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE propertytypes FROM PUBLIC;
REVOKE ALL ON TABLE propertytypes FROM sprenkle;
GRANT ALL ON TABLE propertytypes TO sprenkle;


--
-- Name: roles; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE roles FROM PUBLIC;
REVOKE ALL ON TABLE roles FROM sprenkle;
GRANT ALL ON TABLE roles TO sprenkle;


--
-- Name: user_ids_sequence; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE user_ids_sequence FROM PUBLIC;
REVOKE ALL ON SEQUENCE user_ids_sequence FROM sprenkle;
GRANT ALL ON SEQUENCE user_ids_sequence TO sprenkle;


--
-- Name: users; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE users FROM PUBLIC;
REVOKE ALL ON TABLE users FROM sprenkle;
GRANT ALL ON TABLE users TO sprenkle;


--
-- PostgreSQL database dump complete
--