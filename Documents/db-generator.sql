drop table if exists Customer cascade;
drop table if exists Reservation cascade;
drop table if exists CustomerInvoice cascade;
drop table if exists TipoOmbrellone cascade;
drop table if exists Ombrellone cascade;
drop table if exists ExtraType cascade;

create table Customer(
	customerID smallserial primary key,
	first_name varchar(128) not null,
	last_name varchar(128) not null,
	email varchar(255) not null);
	
create table Reservation(
	reservationID smallserial primary key,
	customerID int constraint customer_fk references Customer(customerID),
	start_date Date not null,
	end_date Date not null check (end_date > start_date),
	total_price decimal(10, 2));
	
create table CustomerInvoice(
	invoiceID int primary key constraint reservationID references Reservation(reservationID),
	customerID int constraint customerID references Customer(customerID),
	invoice_amount decimal(10, 2), -- è diverso da Reservation(totalPrice)?
	is_paid boolean);
	
create table TipoOmbrellone(
	typeID smallserial primary key,
	type_name varchar(128),
	daily_price decimal(5, 2));
	
create table Ombrellone(
	ombrelloneID smallserial primary key,
	-- eccessivo creare una tabella stabilimento se ho un solo stabilimento  -- stabilimentoID int constraint stabilimentoID references Stabilimento(stabilimentoID),
	tipo_ombrellone smallint constraint tipo_ombrellone references tipoOmbrellone(typeID));

create table ExtraType(
	typeID smallserial primary key,
	type_name varchar(128),
	daily_price decimal(5, 2));

-- popolamento tabelle e primi test

insert into Customer(first_name, last_name, email) values('Filippo', 'Sciammacca', 'filipposciammacca@gmail.com');
insert into Customer(first_name, last_name, email) values('Francesco', 'Sciammacca', 'francescosciammacca@gmail.com');
insert into Customer(first_name, last_name, email) values('Niccolò', 'Menghini', 'niccolomenghini@gmail.com');


insert into Reservation(customerID, start_date, end_date, total_price) values(1,'2022-08-08', '2022-08-09', 100.50);
insert into Reservation(customerID, start_date, end_date, total_price) values(1,'2022-08-10', '2022-08-11', 110.50);
insert into Reservation(customerID, start_date, end_date, total_price) values(2,'2022-08-12', '2022-08-13', 122.50);
insert into Reservation(customerID, start_date, end_date, total_price) values(3,'2022-08-14', '2022-08-15', 113.50);
insert into Reservation(customerID, start_date, end_date, total_price) values(1,'2022-08-16', '2022-08-17', 124.50);

insert into TipoOmbrellone(type_name, daily_price) values('Due lettini', 30.00);
insert into TipoOmbrellone(type_name, daily_price) values('Lettino e sdraio', 27.50);
insert into TipoOmbrellone(type_name, daily_price) values('Lettino e regista', 26.00);

insert into Ombrellone(tipo_ombrellone) values(1);
insert into Ombrellone(tipo_ombrellone) values(1);
insert into Ombrellone(tipo_ombrellone) values(1);
insert into Ombrellone(tipo_ombrellone) values(1);
insert into Ombrellone(tipo_ombrellone) values(2);
insert into Ombrellone(tipo_ombrellone) values(2);
insert into Ombrellone(tipo_ombrellone) values(2);
insert into Ombrellone(tipo_ombrellone) values(3);
insert into Ombrellone(tipo_ombrellone) values(3);
