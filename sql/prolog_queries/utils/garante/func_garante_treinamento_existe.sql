-- Sobre:
--
-- Verifica se um existe existe com o código informando, lançando uma exception caso não exista.
--
-- Histórico:
-- 2020-09-21 -> Function criada (luiz_fp - PS-1248).
create or replace function func_garante_treinamento_existe(f_cod_treinamento bigint)
    returns void
    language plpgsql
as
$$
begin
    if not exists(select t.codigo from treinamento t where t.codigo = f_cod_treinamento)
    then
        raise exception 'O treinamento de codigo % não existe.', f_cod_treinamento;
    end if;
end;
$$;