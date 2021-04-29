-- Sobre:
--
-- Insere o estado atual do acoplamento no banco.
--
-- Histórico:
-- 2020-11-11 -> Function criada (luizfp - PL-3210).
-- 2020-11-17 -> Altera a flag possui_acoplamento para true em veículo_data antes de acoplar o veiculo
-- (thaisksf - PL-3320).
create or replace function func_veiculo_insert_estado_atual_acoplamentos(f_cod_processo_acoplamento bigint,
                                                                         f_cod_unidade bigint,
                                                                         f_cod_veiculo bigint,
                                                                         f_cod_diagrama_veiculo bigint,
                                                                         f_posicao_acoplamento smallint,
                                                                         f_veiculo_motorizado boolean)
    returns void
    language plpgsql
as
$$
begin
    update veiculo_data vd
    set acoplado = true
    where vd.cod_unidade = f_cod_unidade
      and vd.codigo = f_cod_veiculo;

    insert into veiculo_acoplamento_atual(cod_processo,
                                          cod_unidade,
                                          cod_veiculo,
                                          cod_diagrama,
                                          cod_posicao,
                                          motorizado)
    values (f_cod_processo_acoplamento,
            f_cod_unidade,
            f_cod_veiculo,
            f_cod_diagrama_veiculo,
            f_posicao_acoplamento,
            f_veiculo_motorizado);

    if not found
    then
        perform throw_server_side_error(format(
            'Erro ao inserir estado atual de acoplamento para o veículo: %s.', f_cod_veiculo));
    end if;
end;
$$;