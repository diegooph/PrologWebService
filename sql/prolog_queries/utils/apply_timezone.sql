create
or replace function apply_timezone(datetime timestamp with time zone, tz text)
    returns timestamp without time zone
    language sql
as
$$
select datetime at time zone tz;
$$;