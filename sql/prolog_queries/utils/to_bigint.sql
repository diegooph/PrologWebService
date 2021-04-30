create
or replace function to_bigint(value anyelement)
    returns bigint
    language sql
as
$$
select value::text::bigint
$$;