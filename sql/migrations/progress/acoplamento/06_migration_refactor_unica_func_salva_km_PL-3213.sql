-- Necessário modificar a view de veículo para conter flag motorizado.
create or replace view veiculo
            (codigo,
             placa,
             identificador_frota,
             cod_unidade,
             cod_empresa,
             km,
             status_ativo,
             cod_diagrama,
             cod_tipo,
             cod_modelo,
             cod_eixos,
             data_hora_cadastro,
             cod_unidade_cadastro,
             foi_editado,
             possui_hubodometro,
             motorizado)
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
       v.motorizado
from veiculo_data v
where v.deletado = false;

-- Cria Enum de processos.
create type types.veiculo_processo_type as enum (
    'ACOPLAMENTO',
    'AFERICAO',
    'FECHAMENTO_SERVICO_PNEU',
    'CHECKLIST',
    'FECHAMENTO_ITEM_CHECKLIST',
    'EDICAO_DE_VEICULOS',
    'MOVIMENTACAO',
    'SOCORRO_EM_ROTA',
    'TRANSFERENCIA_DE_VEICULOS');

-- Cria Type.
create table types.veiculo_processo
(
    processo               types.veiculo_processo_type not null
        constraint pk_veiculo_processo
            primary key,
    processo_legivel_pt_br text                        not null,
    processo_legivel_es    text                        not null,
    ativo                  boolean                     not null
);

-- Insere types.
insert into types.veiculo_processo (processo, processo_legivel_pt_br, processo_legivel_es, ativo)
values ('ACOPLAMENTO', 'ACOPLAMENTO', 'ACOPLAMIENTO', true),
       ('AFERICAO', 'AFERIÇÃO', 'MEDIDA', true),
       ('FECHAMENTO_SERVICO_PNEU', 'FECHAMENTO SERVIÇO PNEU', 'CERRAR SERVICIOS NEUMATICOS', true),
       ('CHECKLIST', 'CHECKLIST', 'CHECKLIST', true),
       ('FECHAMENTO_ITEM_CHECKLIST', 'FECHAMENTO ITEM CHECKLIST', 'CERRAR ELEMENTOS CHECKLIST', true),
       ('EDICAO_DE_VEICULOS', 'EDIÇÃO DE VEÍCULOS', 'EDICIÓN DE VEHÍCULOS', true),
       ('MOVIMENTACAO', 'MOVIMENTAÇÃO', 'MOVIMIENTO', true),
       ('SOCORRO_EM_ROTA', 'SOCORRO EM ROTA', 'AYUDA EN RUTA ', true),
       ('TRANSFERENCIA_DE_VEICULOS', 'TRANSFERÊNCIA DE VEÍCULOS', 'TRANSFERENCIA DE VEHICULO ', true);

-- Function para realizar update do km.
create or replace function func_veiculo_update_km_atual(f_cod_unidade bigint,
                                                        f_cod_veiculo bigint,
                                                        f_km_coletado bigint,
                                                        f_tipo_processo types.veiculo_processo_type,
                                                        f_deve_propagar_km boolean)
    returns bigint
    language plpgsql
as
$$
declare
    v_km_atual                 bigint;
    v_diferenca_km             bigint;
    v_km_motorizado            bigint;
    v_possui_hubodometro       boolean;
    v_motorizado               boolean;
    v_cod_processo_acoplamento bigint;
    v_cod_veiculos_acoplados   bigint[];
begin
    if not f_deve_propagar_km
    then
        update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
        return f_km_coletado;
    end if;

    select v.km, v.possui_hubodometro, v.motorizado, vaa.cod_processo
    from veiculo v
             left join veiculo_acoplamento_atual vaa on v.codigo = vaa.cod_veiculo
    where v.cod_unidade = f_cod_unidade
      and v.codigo = f_cod_veiculo
    into strict v_km_atual, v_possui_hubodometro, v_motorizado, v_cod_processo_acoplamento;
    case when (f_km_coletado is not null) then
        case when ((v_motorizado is true or v_possui_hubodometro is true) and v_km_atual > f_km_coletado)
            then
                return f_km_coletado;
            else
                if (v_cod_processo_acoplamento is not null)
                then
                    v_cod_veiculos_acoplados = (select array_agg(vaa.cod_veiculo)

                                                from veiculo_acoplamento_atual vaa
                                                         join veiculo v
                                                              on vaa.cod_unidade = v.cod_unidade
                                                                  and vaa.cod_veiculo = v.codigo
                                                where vaa.cod_unidade = f_cod_unidade
                                                  and vaa.cod_processo = v_cod_processo_acoplamento
                                                  and v.possui_hubodometro is false);
                end if;
                case when ((v_possui_hubodometro is false and v_motorizado is false and
                            v_cod_processo_acoplamento is null))
                    then
                        return v_km_atual;
                    else
                        case when (v_possui_hubodometro is true or
                                   (v_motorizado is true and v_cod_processo_acoplamento is null))
                            then
                                update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
                                return f_km_coletado;
                            else
                                case when (v_possui_hubodometro is false and v_cod_processo_acoplamento is not null)
                                    then
                                        case when (v_motorizado is true)
                                            then
                                                v_diferenca_km = f_km_coletado - v_km_atual;
                                            else
                                                v_km_motorizado = (select v.km
                                                                   from veiculo v
                                                                   where v.cod_unidade = f_cod_unidade
                                                                     and v.codigo = any (v_cod_veiculos_acoplados)
                                                                     and v.motorizado is true);
                                                case when (v_km_motorizado > f_km_coletado)
                                                    then
                                                        return f_km_coletado;
                                                    else
                                                        v_diferenca_km = f_km_coletado - v_km_motorizado;
                                                    end case;
                                            end case;
                                        case when (v_diferenca_km is not null)
                                            then
                                                update veiculo v
                                                set km = km + v_diferenca_km
                                                where v.codigo = any (v_cod_veiculos_acoplados);
                                                /* !TODO
                                                   !Chama function para salvar o histórico dos veículos que sofreram
                                                   !alteração de km devido à propagação.
                                                   *f_tipo_processo será utilizado nessa function.
                                                !*/
                                                return v_km_atual + v_diferenca_km;
                                            else
                                                return v_km_atual;
                                            end case;
                                    end case;
                            end case;
                    end case;
            end case;
        else
            return v_km_atual;
        end case;
end;
$$;