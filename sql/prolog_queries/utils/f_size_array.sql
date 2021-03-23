create function f_size_array(anyarray) returns integer
    language plpgsql
as
$$
begin
    return coalesce(array_length($1, 1), 0);
end;
$$;