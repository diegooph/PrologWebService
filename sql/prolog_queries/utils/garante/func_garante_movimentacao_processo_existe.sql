-- Sobre:
--
-- Verifica se um processo de movimentação existe com o código informando, lançando uma exception caso não exista.
--
-- Histórico:
-- 2020-09-30 -> Function criada (luiz_fp - PS-1260).
create or replace function func_garante_movimentacao_processo_existe(f_cod_movimentacao_processo bigint)
    returns void
    language plpgsql
as
$$
begin
    if not exists(select mp.codigo from movimentacao_processo mp where mp.codigo = f_cod_movimentacao_processo)
    then
        raise exception 'O processo de movimentação de codigo % não existe.', f_cod_movimentacao_processo;
    end if;
end;
$$;