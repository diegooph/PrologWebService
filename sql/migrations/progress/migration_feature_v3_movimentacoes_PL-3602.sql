create
or replace function to_date(f_date_time timestamp with time zone )
    returns date
    language sql
as
$$
select f_date_time::date;
$$;