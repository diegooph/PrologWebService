-- Modificações estruturais.
alter table veiculo_data
    add column acoplado boolean default false not null;

alter table veiculo_acoplamento_atual
    add column acoplado boolean default true not null;

alter table veiculo_acoplamento_atual
    add constraint check_acoplado_true check (acoplado is true);

alter table veiculo_data
    add constraint unico_veiculo_acoplado unique (codigo, acoplado);

update veiculo_data vd
set acoplado = true
where vd.codigo in (select vaa.cod_veiculo from veiculo_acoplamento_atual vaa);

alter table veiculo_acoplamento_atual
    add constraint fk_veiculo_acoplado foreign key (cod_veiculo, acoplado)
        references veiculo_data (codigo, acoplado) deferrable initially deferred;

-- Adiciona flag na view de veículo
create or replace view veiculo
as
select v.codigo,
       v.placa,
       v.identificador_frota,
       v.cod_unidade,
       v.cod_empresa,
       v.km,
       v.status_ativo,
       v.cod_diagrama,
       v.cod_tipo,
       v.cod_modelo,
       v.cod_eixos,
       v.data_hora_cadastro,
       v.cod_unidade_cadastro,
       v.foi_editado,
       v.possui_hubodometro,
       v.motorizado,
       v.acoplado
from veiculo_data v
where v.deletado = false;

-- Faz com que a func de insert de acoplamento altere a flag em veículo_data para true antes de acoplar o veiculo.
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

alter table veiculo_acoplamento_historico
    rename column km_coletado to km_veiculo;

drop function if exists func_veiculo_insert_historico_acoplamento(f_cod_processo_acoplamento bigint,
    f_cod_veiculo bigint,
    f_cod_diagrama_veiculo bigint,
    f_posicao_acao_realizada smallint,
    f_veiculo_motorizado boolean,
    f_km_coletado bigint,
    f_acao_realizada text);
-- Faz com que a func de deleção de acoplamento altere a flag em veículo_data para false.
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