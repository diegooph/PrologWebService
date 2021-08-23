create or replace function func_pneu_format_dimensao(f_largura numeric,
                                                     f_altura numeric,
                                                     f_aro numeric)
    returns text
    immutable strict
    language plpgsql
as
$$
begin
    return (((f_largura || '/' :: text) || f_altura) || ' r' :: text) || f_aro;
end;
$$;