create table product (
  prod_id serial PRIMARY KEY not null,
  prod_name varchar(255) not null,
  category varchar(255) not null,
  image_url varchar(255),
  price varchar(255) not null,
  min_quantity integer not null,
  deleted boolean not null
);

