create
or replace function to_text(value anyelement)
    returns text
    language sql
as
$$
select value::text
$$;