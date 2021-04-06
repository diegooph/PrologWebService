create or replace function to_bigint_array(variadic list bigint[])
    returns bigint[]
    language plpgsql
as
$$
begin
    return list::bigint[];
end;
$$;