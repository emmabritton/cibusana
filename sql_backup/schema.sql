SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE journal;

DROP ROLE IF EXISTS read_write_journal;

CREATE ROLE read_write_journal WITH
  NOLOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION;

CREATE ROLE foodserver WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION;

GRANT read_write_journal TO foodserver WITH ADMIN OPTION;

CREATE DATABASE journal WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'en_US.UTF-8';

ALTER DATABASE journal OWNER TO doadmin;

\connect journal

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE SCHEMA "food-app";

ALTER SCHEMA "food-app" OWNER TO doadmin;

SET default_tablespace = '';

SET default_table_access_method = heap;

CREATE TABLE "food-app".food_item (
    id uuid NOT NULL,
    name text NOT NULL,
    created_by uuid NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    calories_p100 integer NOT NULL,
    carbs_p100 real NOT NULL,
    fat_p100 real NOT NULL,
    protein_p100 real NOT NULL,
    category text NOT NULL,
    sub_category text NOT NULL,
    company text,
    range text,
    flavors text[] NOT NULL,
    serving_size integer[] NOT NULL,
    serving_size_name text[] NOT NULL,
    allergens text[] NOT NULL,
    flags text[] NOT NULL,
    hidden boolean DEFAULT false NOT NULL
);

CREATE TABLE "food-app".meal (
    id uuid NOT NULL,
    name text NOT NULL,
    food_ids uuid[] NOT NULL,
    food_amounts integer[] NOT NULL,
    cuisine text NOT NULL,
    serving_size integer NOT NULL,
    calories_p100 integer NOT NULL,
    calories_total integer NOT NULL,
    carbs_p100 real NOT NULL,
    carbs_total real NOT NULL,
    fat_p100 real NOT NULL,
    fat_total real NOT NULL,
    protein_p100 real NOT NULL,
    protein_total real NOT NULL,
    recipe text,
    equipment text[],
    cook_time integer,
    prep_time integer,
    flags text[] NOT NULL,
    created_by uuid NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    hidden boolean DEFAULT false NOT NULL,
    allergens text[] NOT NULL,
    flavors text[] NOT NULL
);

CREATE TABLE "food-app".meal_entry (
    id bigint DEFAULT nextval('"food-app".meal_entry_id_seq'::regclass) NOT NULL,
    user_id uuid NOT NULL,
    entry_id uuid NOT NULL,
    is_meal boolean NOT NULL,
    meal_time text NOT NULL,
    date timestamp with time zone NOT NULL,
    amount integer NOT NULL,
    calories integer NOT NULL
);

CREATE TABLE "food-app".measurement (
    id uuid DEFAULT public.uuid_generate_v1() NOT NULL,
    user_id uuid NOT NULL,
    created_at timestamp with time zone NOT NULL,
    m_names text[] NOT NULL,
    m_values real[] NOT NULL
);

CREATE TABLE "food-app".user_tokens (
    user_id uuid NOT NULL,
    token uuid DEFAULT public.uuid_generate_v1() NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);

CREATE TABLE "food-app".users (
    id uuid NOT NULL,
    name text NOT NULL,
    email_address text NOT NULL,
    password text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);

CREATE TABLE "food-app".weight (
    id bigint DEFAULT nextval('"food-app".weight_id_seq'::regclass) NOT NULL,
    user_id uuid NOT NULL,
    kgs real NOT NULL,
    date timestamp with time zone NOT NULL
);

CREATE SEQUENCE "food-app".meal_entry_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE "food-app".weight_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE "food-app".food_item OWNER TO doadmin;
ALTER TABLE "food-app".meal OWNER TO doadmin;
ALTER TABLE "food-app".meal_entry_id_seq OWNER TO doadmin;
ALTER TABLE "food-app".meal_entry OWNER TO doadmin;
ALTER TABLE "food-app".measurement OWNER TO doadmin;
ALTER TABLE "food-app".user_tokens OWNER TO doadmin;
ALTER TABLE "food-app".users OWNER TO doadmin;
ALTER TABLE "food-app".weight OWNER TO doadmin;
ALTER TABLE "food-app".weight_id_seq OWNER TO doadmin;

ALTER TABLE ONLY "food-app".users
    ADD CONSTRAINT email UNIQUE (email_address);

ALTER TABLE ONLY "food-app".food_item
    ADD CONSTRAINT food_item_pkey PRIMARY KEY (id);

ALTER TABLE ONLY "food-app".meal_entry
    ADD CONSTRAINT meal_entry_pkey PRIMARY KEY (id);

ALTER TABLE ONLY "food-app".meal
    ADD CONSTRAINT meal_pkey PRIMARY KEY (id);

ALTER TABLE ONLY "food-app".measurement
    ADD CONSTRAINT measurement_pkey PRIMARY KEY (id);

ALTER TABLE "food-app".food_item
    ADD CONSTRAINT servings CHECK ((array_length(serving_size, 1) = array_length(serving_size_name, 1))) NOT VALID;

ALTER TABLE ONLY "food-app".user_tokens
    ADD CONSTRAINT user_tokens_pkey PRIMARY KEY (token);

ALTER TABLE ONLY "food-app".users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY "food-app".weight
    ADD CONSTRAINT weight_pkey PRIMARY KEY (id);

CREATE INDEX user_id_idx ON "food-app".weight USING btree (user_id);

GRANT CONNECT ON DATABASE journal TO read_write_journal;

GRANT USAGE ON SCHEMA "food-app" TO read_write_journal;

GRANT ALL ON TABLE "food-app".food_item TO read_write_journal;

GRANT ALL ON TABLE "food-app".meal TO read_write_journal;

GRANT ALL ON SEQUENCE "food-app".meal_entry_id_seq TO read_write_journal;

GRANT ALL ON TABLE "food-app".meal_entry TO read_write_journal;

GRANT ALL ON TABLE "food-app".measurement TO read_write_journal;

GRANT ALL ON TABLE "food-app".user_tokens TO read_write_journal;

GRANT ALL ON TABLE "food-app".users TO read_write_journal;

GRANT ALL ON SEQUENCE "food-app".weight_id_seq TO read_write_journal;

GRANT ALL ON TABLE "food-app".weight TO read_write_journal;

