-- Sobre:
--
-- Insere um processo de acoplamento.
--
-- Histórico:
-- 2020-11-11 -> Function criada (luizfp - PL-3210).
create or replace function func_veiculo_insert_processo_acoplamento(f_cod_unidade bigint,
                                                                    f_cod_colaborador_realizacao bigint,
                                                                    f_data_hora_atual timestamp with time zone,
                                                                    f_observacao text)
    returns bigint
    language sql
as
$$
insert into veiculo_acoplamento_processo(cod_unidade,
                                         cod_colaborador,
                                         data_hora,
                                         observacao)
values (f_cod_unidade,
        f_cod_colaborador_realizacao,
        f_data_hora_atual,
        f_observacao)
returning codigo as codigo;
$$;