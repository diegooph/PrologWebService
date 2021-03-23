-- Sobre:
--
-- Insere o histórico de uma ação realizada em um processo de acoplamento.
--
-- Histórico:
-- 2020-11-11 -> Function criada (luizfp - PL-3210).
-- 2020-11-17 -> Altera a flag possui_acoplamento para false em veículo_data antes de desacoplar o veiculo
-- (thaisksf - PL-3320).
create or replace function func_veiculo_insert_historico_acoplamento(f_cod_processo_acoplamento bigint,
                                                                     f_cod_veiculo bigint,
                                                                     f_cod_diagrama_veiculo bigint,
                                                                     f_posicao_acao_realizada smallint,
                                                                     f_veiculo_motorizado boolean,
                                                                     f_acao_realizada text)
    returns void
    language plpgsql
as
$$
begin
    insert into veiculo_acoplamento_historico(cod_processo,
                                              cod_veiculo,
                                              cod_diagrama,
                                              cod_posicao,
                                              motorizado,
                                              km_veiculo,
                                              acao)
    values (f_cod_processo_acoplamento,
            f_cod_veiculo,
            f_cod_diagrama_veiculo,
            f_posicao_acao_realizada,
            f_veiculo_motorizado,
            (select v.km from veiculo v where v.codigo = f_cod_veiculo),
            f_acao_realizada::types.veiculo_acoplamento_acao_type);

    if (f_acao_realizada::types.veiculo_acoplamento_acao_type = ('DESACOPLADO'::types.veiculo_acoplamento_acao_type))
    then
        update veiculo_data vd
        set acoplado = false
        where vd.codigo = f_cod_veiculo;
    end if;

    if not found
    then
        perform throw_server_side_error(format('Erro ao inserir histórico de acoplamento para o veículo: %s.',
                                               f_cod_veiculo));
    end if;
end;
$$;