-- Sobre:
-- Verifica se a placa informada está presente na unidade.
--
-- Histórico:
-- 2019-07-04 -> Function criada (luizfp - PL-2097).
-- 2020-03-29 -> Altera function para lançar mensagem genérica (diogenesvanzella - PLI-80).
-- 2020-09-15 -> Altera function para receber parâmetro opcional de considerar deletados (luiz_fp - PL-3133).
create or replace function func_garante_veiculo_existe(f_cod_unidade_veiculo bigint,
                                                       f_placa_veiculo text,
                                                       f_considerar_deletados boolean default true,
                                                       f_error_message text default null)
    returns void
    language plpgsql
as
$$
declare
    v_error_message text :=
        f_if(f_error_message is null,
             format('Não foi possível encontrar o veículo com estes parâmetros: Unidade %s, Placa %s',
                    f_cod_unidade_veiculo, f_placa_veiculo),
             f_error_message);
begin
    if not exists(select vd.codigo
                  from veiculo_data vd
                  where vd.placa = f_placa_veiculo
                    and vd.cod_unidade = f_cod_unidade_veiculo
                    and f_if(f_considerar_deletados, true, vd.deletado = false))
    then
        perform throw_generic_error(v_error_message);
    end if;
end;
$$;