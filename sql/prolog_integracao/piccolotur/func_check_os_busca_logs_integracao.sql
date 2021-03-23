-- Sobre:
--
-- Function utilizada pela integração da Piccolotur para buscar as informações dos checklists que nã puderam ser
-- sincronizados. Essa function irá prover as informações necessárias para que o usuário consiga visualizar quais os
-- checklists que não estão sincronizados e por qual motivo não foram sincronizados.
--
-- Histórico:
-- 2020-03-09 -> Function criada (diogenesvanzella - PLI-100).
create or replace function piccolotur.func_check_os_busca_logs_integracao(f_data_inicio date default null,
                                                                          f_data_fim date default null)
    returns table
            (
                data_hora_realizado          timestamp without time zone,
                codigo_checklist_prolog      bigint,
                codigo_modelo_checklist      bigint,
                cpf_colaborador              text,
                placa_veiculo                text,
                mensagem_erro                text,
                tentativas_sincronias        integer,
                data_hora_ultima_atualizacao timestamp without time zone
            )
    language sql
as
$$
select c.data_hora at time zone tz_unidade(c.cod_unidade)                       as data_hora_realizado,
       c.codigo                                                                 as codigo_checklist_prolog,
       c.cod_checklist_modelo                                                   as codigo_modelo_checklist,
       lpad(c.cpf_colaborador::text, 11, '0')                                   as cpf_colaborador,
       c.placa_veiculo::text                                                    as placa_veiculo,
       cpps.mensagem_erro_ao_sincronizar                                        as mensagem_erro,
       cpps.qtd_tentativas                                                      as tentativas_sincronias,
       cpps.data_hora_ultima_atualizacao at time zone tz_unidade(c.cod_unidade) as data_hora_ultima_atualizacao
from piccolotur.checklist_pendente_para_sincronizar cpps
         join checklist c on cpps.cod_checklist_para_sincronizar = c.codigo
where cpps.sincronizado is false
  and cpps.precisa_ser_sincronizado is true
  and (f_if(f_data_inicio is null, true, c.data_hora::date >= f_data_inicio) and
       f_if(f_data_fim is null, true, c.data_hora::date <= f_data_fim));
$$;