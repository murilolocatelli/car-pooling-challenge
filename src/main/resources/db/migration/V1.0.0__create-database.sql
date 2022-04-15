CREATE TABLE public.car (
    id int4 NOT NULL,
    seats int8 NOT NULL,
    created_at timestamp NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE public.journey (
    id int4 NOT NULL,
    people int8 NOT NULL,
    status varchar(20) NOT NULL,
    created_at timestamp NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE public.registered_journey (
    id bigserial NOT NULL,
    id_car int4 NOT NULL,
    id_journey int8 NOT NULL,
    created_at timestamp NOT NULL,
    PRIMARY KEY(id)
);

ALTER TABLE public.registered_journey
    ADD CONSTRAINT fk_registered_journey_car
    FOREIGN KEY(id_car)
    REFERENCES public.car(id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;

ALTER TABLE public.registered_journey
    ADD CONSTRAINT fk_registered_journey_journey
    FOREIGN KEY(id_journey)
    REFERENCES public.journey(id)
    ON DELETE NO ACTION 
    ON UPDATE NO ACTION;
