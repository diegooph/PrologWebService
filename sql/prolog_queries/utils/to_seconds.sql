create or replace function to_seconds(t interval)
    returns bigint
    language sql
as
$$
select (extract(epoch from t))::bigint;
$$;

create function to_seconds(t text) returns integer
    language plpgsql
as
$$
declare
    hs integer;
    ms integer;
    s  integer;
begin
    select (extract(hour from t::time) * 60 * 60)::integer into hs;
    select (extract(minutes from t::time) * 60)::integer into ms;
    select (extract(seconds from t::time))::integer into s;
    select (hs + ms + s) into s;
    return s;
end;
$$;