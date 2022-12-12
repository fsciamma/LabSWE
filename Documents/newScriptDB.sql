drop table if exists add_on cascade;
drop table if exists add_on_type cascade;
drop table if exists customer cascade;
drop table if exists invoice;
drop table if exists reservable_asset cascade;
drop table if exists reservable_type cascade;
drop table if exists reservation cascade;
drop table if exists reserved_add_on;
drop table if exists reserved_assets cascade;

CREATE TABLE "laZattera".add_on_type
(
    "typeID" integer NOT NULL,
    type_name character varying COLLATE pg_catalog."default" NOT NULL,
    price numeric(10,2) NOT NULL,
    CONSTRAINT add_on_type_pkey PRIMARY KEY ("typeID"),
    CONSTRAINT type UNIQUE (type_name)
);

CREATE TABLE "laZattera".add_on
(
    "add_onID" integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    add_on_type integer NOT NULL,
    "sub_classID" integer NOT NULL,
    CONSTRAINT add_on_pkey PRIMARY KEY ("add_onID"),
    CONSTRAINT doppioni_addon UNIQUE (add_on_type, "sub_classID"),
    CONSTRAINT add_on_add_on_type_fkey FOREIGN KEY (add_on_type)
        REFERENCES "laZattera".add_on_type ("typeID") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE "laZattera".customer
(
    email character varying COLLATE pg_catalog."default" NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    surname character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT customer_pkey PRIMARY KEY (email)
);

CREATE TABLE "laZattera".reservation
(
    "reservationID" integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    "customerID" character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT reservation_pkey PRIMARY KEY ("reservationID"),
    CONSTRAINT customer FOREIGN KEY ("customerID")
        REFERENCES "laZattera".customer (email) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE "laZattera".invoice
(
    "reservationID" integer NOT NULL,
    total numeric(10,2) NOT NULL,
    paid boolean NOT NULL DEFAULT false,
    CONSTRAINT invoice_pkey PRIMARY KEY ("reservationID"),
    CONSTRAINT "ID" FOREIGN KEY ("reservationID")
        REFERENCES "laZattera".reservation ("reservationID") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE "laZattera".reservable_type
(
    "typeID" integer NOT NULL,
    type_name character varying(64) COLLATE pg_catalog."default" NOT NULL,
    price numeric(10,2) NOT NULL,
    CONSTRAINT reservable_table_pkey PRIMARY KEY ("typeID"),
    CONSTRAINT "non ci sono " UNIQUE (type_name)
);

CREATE TABLE "laZattera".reservable_asset
(
    "assetID" integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    asset_type integer NOT NULL,
    purchase_date date,
    last_revision date,
    history text COLLATE pg_catalog."default",
    "sub_classID" integer NOT NULL,
    CONSTRAINT reservable_asset_pkey PRIMARY KEY ("assetID"),
    CONSTRAINT doppioni UNIQUE (asset_type, "sub_classID"),
    CONSTRAINT asset_type FOREIGN KEY (asset_type)
        REFERENCES "laZattera".reservable_type ("typeID") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT revisione CHECK (last_revision >= purchase_date)
);

CREATE TABLE "laZattera".reserved_assets
(
    "reservedID" integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    "reservationID" integer NOT NULL,
    "assetID" integer NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    CONSTRAINT reserved_assets_pkey PRIMARY KEY ("reservedID"),
    CONSTRAINT "prenotazioni sovrappsote" UNIQUE ("assetID", start_date),
    CONSTRAINT asset FOREIGN KEY ("assetID")
        REFERENCES "laZattera".reservable_asset ("assetID") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT reservation FOREIGN KEY ("reservationID")
        REFERENCES "laZattera".reservation ("reservationID") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "validità date" CHECK (start_date <= end_date)
);

CREATE TABLE "laZattera".reserved_add_on
(
    "reserved_assetsID" integer NOT NULL,
    "add_onID" integer NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    CONSTRAINT "una prenotazione addon" UNIQUE ("add_onID", start_date),
    CONSTRAINT addons FOREIGN KEY ("add_onID")
        REFERENCES "laZattera".add_on ("add_onID") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "reservation reference" FOREIGN KEY ("reserved_assetsID")
        REFERENCES "laZattera".reserved_assets ("reservedID") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "validità date" CHECK (start_date <= end_date)
);