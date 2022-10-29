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
    created_at timestamp without time zone NOT NULL,
    hidden boolean DEFAULT false NOT NULL,
    allergens text[] NOT NULL,
    flavors text[] NOT NULL
);


ALTER TABLE "food-app".meal OWNER TO doadmin;


ALTER TABLE ONLY "food-app".meal
    ADD CONSTRAINT meal_pkey PRIMARY KEY (id);

GRANT ALL ON TABLE "food-app".meal TO read_write_journal;

