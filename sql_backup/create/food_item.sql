CREATE TABLE "food-app".food_item (
    id uuid NOT NULL,
    name text NOT NULL,
    created_by uuid NOT NULL,
    created_at timestamp without time zone NOT NULL,
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


ALTER TABLE "food-app".food_item OWNER TO doadmin;

ALTER TABLE ONLY "food-app".food_item
    ADD CONSTRAINT food_item_pkey PRIMARY KEY (id);


ALTER TABLE "food-app".food_item
    ADD CONSTRAINT servings CHECK ((array_length(serving_size, 1) = array_length(serving_size_name, 1))) NOT VALID;

GRANT ALL ON TABLE "food-app".food_item TO read_write_journal;

