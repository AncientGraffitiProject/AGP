--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

--
-- Name: inscription_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE inscription_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.inscription_id_seq OWNER TO sprenkle;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: agp_inscription_info; Type: TABLE; Schema: public; Owner: sprenkle; Tablespace: 
--

CREATE TABLE agp_inscription_info (
    id integer DEFAULT nextval('inscription_id_seq'::regclass) NOT NULL,
    edr_id character(9),
    "comment" text,
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
    is_greatest_hit_translation boolean DEFAULT false,
    is_greatest_hit_figural boolean DEFAULT false,
    epidoc text,
    letter_with_flourishes_height_min character varying(10), 
 	letter_with_flourishes_height_max character varying(10)
);


ALTER TABLE public.agp_inscription_info OWNER TO sprenkle;

--
-- Name: drawing_tag_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE drawing_tag_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.drawing_tag_id_seq OWNER TO sprenkle;

--
-- Name: drawing_tags; Type: TABLE; Schema: public; Owner: sprenkle; Tablespace: 
--

CREATE TABLE drawing_tags (
    id integer DEFAULT nextval('drawing_tag_id_seq'::regclass) NOT NULL,
    name character varying(30),
    description character varying(100)
);


ALTER TABLE public.drawing_tags OWNER TO sprenkle;

--
-- Name: eagle_inscription_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE eagle_inscription_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.eagle_inscription_id_seq OWNER TO sprenkle;

--
-- Name: edr_inscriptions; Type: TABLE; Schema: public; Owner: sprenkle; Tablespace: 
--

CREATE TABLE edr_inscriptions (
    id integer DEFAULT nextval('eagle_inscription_id_seq'::regclass) NOT NULL,
    edr_id character(9) NOT NULL,
    ancient_city character varying(30),
    find_spot character varying(150),
    measurements character varying(100),
    writing_style character varying(30),
    "language" character varying(30),
    content text,
    bibliography text,
    numberofimages integer DEFAULT 0,
    apparatus text,
    apparatus_displayed text
);


ALTER TABLE public.edr_inscriptions OWNER TO sprenkle;

--
-- Name: figural_graffiti_info; Type: TABLE; Schema: public; Owner: sprenkle; Tablespace: 
--

CREATE TABLE figural_graffiti_info (
    edr_id character(9),
    description_in_latin text,
    description_in_english text
);


ALTER TABLE public.figural_graffiti_info OWNER TO sprenkle;

--
-- Name: graffitotodrawingtags_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE graffitotodrawingtags_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.graffitotodrawingtags_id_seq OWNER TO sprenkle;

--
-- Name: graffitotodrawingtags; Type: TABLE; Schema: public; Owner: sprenkle; Tablespace: 
--

CREATE TABLE graffitotodrawingtags (
    id integer DEFAULT nextval('graffitotodrawingtags_id_seq'::regclass) NOT NULL,
    graffito_id character(9) NOT NULL,
    drawing_tag_id integer NOT NULL
);


ALTER TABLE public.graffitotodrawingtags OWNER TO sprenkle;

--
-- Name: insula_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE insula_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.insula_id_seq OWNER TO sprenkle;

--
-- Name: insula; Type: TABLE; Schema: public; Owner: sprenkle; Tablespace: 
--

CREATE TABLE insula (
    id integer DEFAULT nextval('insula_id_seq'::regclass) NOT NULL,
    modern_city character varying(30),
    name character varying(20),
    description character varying(100)
);


ALTER TABLE public.insula OWNER TO sprenkle;

--
-- Name: property_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE property_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.property_id_seq OWNER TO sprenkle;

--
-- Name: properties; Type: TABLE; Schema: public; Owner: sprenkle; Tablespace: 
--

CREATE TABLE properties (
    id integer DEFAULT nextval('property_id_seq'::regclass) NOT NULL,
    property_number character varying(20),
    additional_properties character varying(20),
    property_name character varying(70),
    italian_property_name character varying(70),
    insula_id integer
);


ALTER TABLE public.properties OWNER TO sprenkle;

--
-- Name: property_type_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE property_type_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.property_type_id_seq OWNER TO sprenkle;

--
-- Name: propertytopropertytype; Type: TABLE; Schema: public; Owner: sprenkle; Tablespace: 
--

CREATE TABLE propertytopropertytype (
    property_id integer,
    property_type integer
);


ALTER TABLE public.propertytopropertytype OWNER TO sprenkle;

--
-- Name: propertytypes; Type: TABLE; Schema: public; Owner: sprenkle; Tablespace: 
--

CREATE TABLE propertytypes (
    id integer DEFAULT nextval('property_type_id_seq'::regclass) NOT NULL,
    name character varying(30),
    description character varying(100)
);


ALTER TABLE public.propertytypes OWNER TO sprenkle;

--
-- Name: roles; Type: TABLE; Schema: public; Owner: sprenkle; Tablespace: 
--

CREATE TABLE roles (
    id integer,
    "role" character varying(30)
);


ALTER TABLE public.roles OWNER TO sprenkle;

--
-- Name: user_ids_sequence; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE user_ids_sequence
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.user_ids_sequence OWNER TO sprenkle;

--
-- Name: users; Type: TABLE; Schema: public; Owner: sprenkle; Tablespace: 
--

CREATE TABLE users (
    id integer DEFAULT nextval('user_ids_sequence'::regclass) NOT NULL,
    "password" character varying(64) NOT NULL,
    username character varying(20) NOT NULL,
    name character varying(30),
    "role" character varying(30),
    enabled boolean
);


ALTER TABLE public.users OWNER TO sprenkle;

--
-- Name: agp_inscription_annotations_eagle_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY agp_inscription_info
    ADD CONSTRAINT agp_inscription_annotations_eagle_id_key UNIQUE (edr_id);


--
-- Name: agp_inscription_annotations_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY agp_inscription_info
    ADD CONSTRAINT agp_inscription_annotations_pkey PRIMARY KEY (id);


--
-- Name: drawing_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY drawing_tags
    ADD CONSTRAINT drawing_tags_pkey PRIMARY KEY (id);


--
-- Name: eagle_inscriptions_eagle_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY edr_inscriptions
    ADD CONSTRAINT eagle_inscriptions_eagle_id_key UNIQUE (edr_id);


--
-- Name: eagle_inscriptions_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY edr_inscriptions
    ADD CONSTRAINT eagle_inscriptions_pkey PRIMARY KEY (id);


--
-- Name: graffitotodrawingtags_graffito_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY graffitotodrawingtags
    ADD CONSTRAINT graffitotodrawingtags_graffito_id_key UNIQUE (graffito_id, drawing_tag_id);


--
-- Name: graffitotodrawingtags_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY graffitotodrawingtags
    ADD CONSTRAINT graffitotodrawingtags_pkey PRIMARY KEY (id);


--
-- Name: insula_modern_city_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY insula
    ADD CONSTRAINT insula_modern_city_key UNIQUE (modern_city, name);


--
-- Name: insula_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY insula
    ADD CONSTRAINT insula_pkey PRIMARY KEY (id);


--
-- Name: properties_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY properties
    ADD CONSTRAINT properties_pkey PRIMARY KEY (id);


--
-- Name: properties_uniq_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY properties
    ADD CONSTRAINT properties_uniq_key UNIQUE (insula_id, property_number);


--
-- Name: propertytopropertytype_property_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY propertytopropertytype
    ADD CONSTRAINT propertytopropertytype_property_id_key UNIQUE (property_id, property_type);


--
-- Name: propertytypes_name_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY propertytypes
    ADD CONSTRAINT propertytypes_name_key UNIQUE (name);


--
-- Name: propertytypes_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY propertytypes
    ADD CONSTRAINT propertytypes_pkey PRIMARY KEY (id);


--
-- Name: unique_drawing_tag_name; Type: CONSTRAINT; Schema: public; Owner: sprenkle; Tablespace: 
--

ALTER TABLE ONLY drawing_tags
    ADD CONSTRAINT unique_drawing_tag_name UNIQUE (name);


--
-- Name: agp_inscription_annotations_eagle_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY agp_inscription_info
    ADD CONSTRAINT agp_inscription_annotations_eagle_id_fkey FOREIGN KEY (edr_id) REFERENCES edr_inscriptions(edr_id) ON DELETE CASCADE;


--
-- Name: agp_inscription_annotations_property_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY agp_inscription_info
    ADD CONSTRAINT agp_inscription_annotations_property_id_fkey FOREIGN KEY (property_id) REFERENCES properties(id);


--
-- Name: figural_graffiti_info_eagle_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY figural_graffiti_info
    ADD CONSTRAINT figural_graffiti_info_eagle_id_fkey FOREIGN KEY (edr_id) REFERENCES edr_inscriptions(edr_id) ON DELETE CASCADE;


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
GRANT SELECT,UPDATE ON SEQUENCE inscription_id_seq TO cs335;


--
-- Name: agp_inscription_info; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE agp_inscription_info FROM PUBLIC;
REVOKE ALL ON TABLE agp_inscription_info FROM sprenkle;
GRANT ALL ON TABLE agp_inscription_info TO sprenkle;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE agp_inscription_info TO cs335;


--
-- Name: drawing_tag_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE drawing_tag_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE drawing_tag_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE drawing_tag_id_seq TO sprenkle;
GRANT ALL ON SEQUENCE drawing_tag_id_seq TO cs335;


--
-- Name: drawing_tags; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE drawing_tags FROM PUBLIC;
REVOKE ALL ON TABLE drawing_tags FROM sprenkle;
GRANT ALL ON TABLE drawing_tags TO sprenkle;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE drawing_tags TO cs335;


--
-- Name: eagle_inscription_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE eagle_inscription_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE eagle_inscription_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE eagle_inscription_id_seq TO sprenkle;
GRANT ALL ON SEQUENCE eagle_inscription_id_seq TO jangha;
GRANT ALL ON SEQUENCE eagle_inscription_id_seq TO cs335;


--
-- Name: edr_inscriptions; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE edr_inscriptions FROM PUBLIC;
REVOKE ALL ON TABLE edr_inscriptions FROM sprenkle;
GRANT ALL ON TABLE edr_inscriptions TO sprenkle;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE edr_inscriptions TO cs335;


--
-- Name: figural_graffiti_info; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE figural_graffiti_info FROM PUBLIC;
REVOKE ALL ON TABLE figural_graffiti_info FROM sprenkle;
GRANT ALL ON TABLE figural_graffiti_info TO sprenkle;
GRANT ALL ON TABLE figural_graffiti_info TO web;


--
-- Name: graffitotodrawingtags_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE graffitotodrawingtags_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE graffitotodrawingtags_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE graffitotodrawingtags_id_seq TO sprenkle;
GRANT ALL ON SEQUENCE graffitotodrawingtags_id_seq TO cs335;


--
-- Name: graffitotodrawingtags; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE graffitotodrawingtags FROM PUBLIC;
REVOKE ALL ON TABLE graffitotodrawingtags FROM sprenkle;
GRANT ALL ON TABLE graffitotodrawingtags TO sprenkle;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE graffitotodrawingtags TO cs335;


--
-- Name: insula_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE insula_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE insula_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE insula_id_seq TO sprenkle;
GRANT SELECT,UPDATE ON SEQUENCE insula_id_seq TO web;


--
-- Name: insula; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE insula FROM PUBLIC;
REVOKE ALL ON TABLE insula FROM sprenkle;
GRANT ALL ON TABLE insula TO sprenkle;
GRANT SELECT,INSERT ON TABLE insula TO web;


--
-- Name: property_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE property_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE property_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE property_id_seq TO sprenkle;
GRANT ALL ON SEQUENCE property_id_seq TO cs335;


--
-- Name: properties; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE properties FROM PUBLIC;
REVOKE ALL ON TABLE properties FROM sprenkle;
GRANT ALL ON TABLE properties TO sprenkle;
GRANT ALL ON TABLE properties TO cs335;


--
-- Name: property_type_id_seq; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE property_type_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE property_type_id_seq FROM sprenkle;
GRANT ALL ON SEQUENCE property_type_id_seq TO sprenkle;
GRANT ALL ON SEQUENCE property_type_id_seq TO cs335;


--
-- Name: propertytopropertytype; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE propertytopropertytype FROM PUBLIC;
REVOKE ALL ON TABLE propertytopropertytype FROM sprenkle;
GRANT ALL ON TABLE propertytopropertytype TO sprenkle;
GRANT ALL ON TABLE propertytopropertytype TO cs335;


--
-- Name: propertytypes; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE propertytypes FROM PUBLIC;
REVOKE ALL ON TABLE propertytypes FROM sprenkle;
GRANT ALL ON TABLE propertytypes TO sprenkle;
GRANT ALL ON TABLE propertytypes TO cs335;


--
-- Name: roles; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE roles FROM PUBLIC;
REVOKE ALL ON TABLE roles FROM sprenkle;
GRANT ALL ON TABLE roles TO sprenkle;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE roles TO cs335;


--
-- Name: user_ids_sequence; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON SEQUENCE user_ids_sequence FROM PUBLIC;
REVOKE ALL ON SEQUENCE user_ids_sequence FROM sprenkle;
GRANT ALL ON SEQUENCE user_ids_sequence TO sprenkle;
GRANT ALL ON SEQUENCE user_ids_sequence TO jangha;
GRANT ALL ON SEQUENCE user_ids_sequence TO cs335;


--
-- Name: users; Type: ACL; Schema: public; Owner: sprenkle
--

REVOKE ALL ON TABLE users FROM PUBLIC;
REVOKE ALL ON TABLE users FROM sprenkle;
GRANT ALL ON TABLE users TO sprenkle;
GRANT ALL ON TABLE users TO jangha;
GRANT ALL ON TABLE users TO web;
GRANT ALL ON TABLE users TO cs335;


--
-- PostgreSQL database dump complete
--

