drop table if exists "laZattera".add_on cascade;
drop table if exists "laZattera".add_on_type cascade;
drop table if exists "laZattera".customer cascade;
drop table if exists "laZattera".invoice;
drop table if exists "laZattera".reservable_asset cascade;
drop table if exists "laZattera".reservable_type cascade;
drop table if exists "laZattera".reservation cascade;
drop table if exists "laZattera".reserved_add_on;
drop table if exists "laZattera".reserved_assets cascade;

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
    "reservationID" integer,
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

insert into "laZattera".reservable_type values(1, 'Ombrellone', 15.00),
                                              (2, 'Gazebo', 45.00);

insert into "laZattera".add_on_type values(1, 'Sedia', 3.00),
                                          (2, 'Sdraio', 3.50),
                                          (3, 'Lettino', 4.50),
                                          (4, 'Cabina', 5.00);

insert into "laZattera".reservable_asset values
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 1),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 2),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 3),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 4),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 5),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 6),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 7),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 8),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 9),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 10),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 11),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 12),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 13),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 14),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 15),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 16),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 17),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 18),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 19),
                                             (DEFAULT, 1, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 20),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 1),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 2),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 3),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 4),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 5),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 6),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 7),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 8),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 9),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 10),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 11),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 12),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 13),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 14),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 15),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 16),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 17),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 18),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 19),
                                             (DEFAULT, 2, '2019-03-15', '2022-05-19', 'Non sono stati riscontrati problemi', 20);

insert into "laZattera".add_on values (DEFAULT, 1, 1),
                                      (DEFAULT, 1, 2),
                                      (DEFAULT, 1, 3),
                                      (DEFAULT, 1, 4),
                                      (DEFAULT, 1, 5),
                                      (DEFAULT, 1, 6),
                                      (DEFAULT, 1, 7),
                                      (DEFAULT, 1, 8),
                                      (DEFAULT, 1, 9),
                                      (DEFAULT, 1, 10),
                                      (DEFAULT, 2, 1),
                                      (DEFAULT, 2, 2),
                                      (DEFAULT, 2, 3),
                                      (DEFAULT, 2, 4),
                                      (DEFAULT, 2, 5),
                                      (DEFAULT, 2, 6),
                                      (DEFAULT, 2, 7),
                                      (DEFAULT, 2, 8),
                                      (DEFAULT, 2, 9),
                                      (DEFAULT, 2, 10),
                                      (DEFAULT, 3, 1),
                                      (DEFAULT, 3, 2),
                                      (DEFAULT, 3, 3),
                                      (DEFAULT, 3, 4),
                                      (DEFAULT, 3, 5),
                                      (DEFAULT, 3, 6),
                                      (DEFAULT, 3, 7),
                                      (DEFAULT, 3, 8),
                                      (DEFAULT, 3, 9),
                                      (DEFAULT, 3, 10),
                                      (DEFAULT, 4, 1),
                                      (DEFAULT, 4, 2),
                                      (DEFAULT, 4, 3),
                                      (DEFAULT, 4, 4),
                                      (DEFAULT, 4, 5);

insert into "laZattera".customer values ('filipposciammacca@gmail.com', 'Filippo', 'Sciammacca'),
                                        ('niccolo.menghini@gmail.com', 'Niccolò', 'Menghini'),
                                        ('mariorossi@gimli.com', 'Mario', 'Rossi'),
                                        ('francescotodino@gimli.com', 'Francesco', 'Todino'),
                                        ('francescocorazzi@gimli.com', 'Francesco', 'Corazzi');

insert into "laZattera".reservation values (DEFAULT, 'mariorossi@gimli.com'),
                                           (DEFAULT, 'filipposciammacca@gmail.com'),
                                           (DEFAULT, 'filipposciammacca@gmail.com'),
                                           (DEFAULT, 'francescotodino@gimli.com'),
                                           (DEFAULT, 'francescocorazzi@gimli.com');

insert into "laZattera".reserved_assets values (DEFAULT, 1, 37, '2023-06-07', '2023-06-15'),
                                               (DEFAULT, 2, 30, '2023-06-12', '2023-06-26'),
                                               (DEFAULT, 3, 5, '2023-06-02', '2023-06-08'),
                                               (DEFAULT, 4, 27, '2023-06-03', '2023-06-17'),
                                               (DEFAULT, 5, 25, '2023-06-01', '2023-06-10'),
                                               (DEFAULT, 5, 38, '2023-06-21', '2023-06-28'),
                                               (DEFAULT, 1, 20, '2023-06-21', '2023-06-26'),
                                               (DEFAULT, 1, 20, '2023-07-01', '2023-07-04');

insert into "laZattera".reserved_add_on values (1, 4, '2023-06-07', '2023-06-15'),
                                               (1, 16, '2023-06-07', '2023-06-15'),
                                               (1, 27, '2023-06-07', '2023-06-15'),
                                               (1, 32, '2023-06-07', '2023-06-15'),
                                               (2, 31, '2023-06-12', '2023-06-26'),
                                               (2, 27, '2023-06-12', '2023-06-19'),
                                               (2, 28, '2023-06-19', '2023-06-26'),
                                               (3, 14, '2023-06-02', '2023-06-06'),
                                               (4, 12, '2023-06-03', '2023-06-17'),
                                               (4, 13, '2023-06-03', '2023-06-17'),
                                               (5, 33, '2023-06-01', '2023-06-10'),
                                               (5, 30, '2023-06-01', '2023-06-10'),
                                               (6, 32, '2023-06-21', '2023-06-28');

insert into "laZattera".invoice values (1, 699.00, false),
                                       (2, 822.00, false),
                                       (3, 122.50, false),
                                       (4, 780.00, false),
                                       (5, 945.00, false);

