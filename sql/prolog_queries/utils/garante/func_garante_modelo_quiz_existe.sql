create or replace function func_garante_modelo_quiz_existe(f_cod_modelo_quiz bigint)
    returns void
    language plpgsql
as
$$
begin
    if not exists(select qm.codigo from quiz_modelo qm where qm.codigo = f_cod_modelo_quiz)
    then
        raise exception 'O modelo de quiz de codigo % não existe.', f_cod_modelo_quiz;
    end if;
end;
$$;