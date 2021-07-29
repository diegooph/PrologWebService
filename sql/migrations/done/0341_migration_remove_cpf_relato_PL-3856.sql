alter table relato
    add column cod_colaborador bigint;
update relato
set cod_colaborador = cd.codigo
from colaborador_data cd
where cd.cpf = relato.cpf_colaborador;
alter table relato
    alter column cod_colaborador set not null;

alter table relato
    add column cod_colaborador_classificacao bigint;
update relato
set cod_colaborador_classificacao = cd.codigo
from colaborador_data cd
where cd.cpf = relato.cpf_classificacao;

alter table relato
    add column cod_colaborador_fechamento bigint;
update relato
set cod_colaborador_fechamento = cd.codigo
from colaborador_data cd
where cd.cpf = relato.cpf_fechamento;

alter table relato drop column cpf_colaborador, drop column cpf_classificacao, drop column cpf_fechamento;


create or replace function func_relatorio_extrato_relatos(f_data_inicial date, f_data_final date, f_cod_unidade bigint, f_equipe text)
    returns TABLE("CÓDIGO" bigint, "DATA DO ENVIO" text, "INVÁLIDO" character, "ENVIADO" character, "CLASSIFICADO" character, "FECHADO" character, "ALTERNATIVA" text, "DESCRIÇÃO" text, "COD_PDV" integer, "RELATADO POR - CPF" text, "RELATADO POR - NOME" text, "EQUIPE" text, "DATA CLASSIFICAÇÃO" text, "TEMPO PARA CLASSIFICAÇÃO (DIAS)" integer, "CLASSIFICADO POR - CPF" text, "CLASSIFICADO POR - NOME" text, "DATA FECHAMENTO" text, "TEMPO PARA FECHAMENTO (DIAS)" integer, "FECHADO POR - CPF" text, "FECHADO POR - NOME" text, "OBS FECHAMENTO" text, "LATITUDE" text, "LONGITUDE" text, "LINK MAPS" text, "FOTO 1" text, "FOTO 2" text, "FOTO 3" text)
    language sql
as
$$
select r.codigo                                                                                as cod_relato,
       to_char(r.data_hora_database, 'DD/MM/YYYY HH24:MI')                                     as data_envio,
       case when r.status = 'INVALIDO' THEN 'X' ELSE '' END                                    as invalido,
       'X'::CHAR                                                                               as enviado,
       case when r.status = 'PENDENTE_FECHAMENTO' OR r.status = 'FECHADO' THEN 'X' ELSE '' END as classificado,
       case when r.status = 'FECHADO' THEN 'X' ELSE '' END                                     as fechado,
       ra.alternativa                                                                          as alternativa_selecionada,
       r.resposta_outros                                                                       as descricao,
       r.cod_pdv                                                                               as cod_pdv,
       lpad(relator.cpf::text, 11, '0')                                                        as cpf_relator,
       relator.nome                                                                            as nome_relator,
       e.nome                                                                                  as equipe,
       to_char(r.data_hora_classificacao, 'DD/MM/YYYY HH24:MI')                                as data_classificacao,
       extract(day from r.data_hora_classificacao - r.data_hora_database)::INT                 as dias_para_classificacao,
       lpad(classificador.cpf::text, 11, '0')                                                  as classificador_cpf,
       classificador.nome                                                                      as classificador_nome,
       to_char(r.data_hora_fechamento, 'DD/MM/YYYY HH24:MI')                                   as data_fechamento,
       extract(DAY FROM r.data_hora_fechamento - r.data_hora_database)::INT                    as dias_para_fechamento,
       lpad(fechamento.cpf::text, 11, '0')                                                     as cpf_fechamento,
       fechamento.nome                                                                         as colaborador_fechamento,
       r.feedback_fechamento,
       r.latitude,
       r.longitude,
       'http://maps.google.com/?q=' || r.latitude || ',' || r.longitude                        as link_maps,
       r.url_foto_1,
       r.url_foto_2,
       r.url_foto_3
from relato r
         join colaborador relator on relator.codigo = r.cod_colaborador
         left join colaborador classificador on classificador.codigo = r.cod_colaborador_classificacao
         left join colaborador fechamento on fechamento.codigo = r.cod_colaborador_fechamento
         left join relato_alternativa ra on ra.cod_unidade = r.cod_unidade and r.cod_alternativa = ra.codigo
         join unidade u on u.codigo = relator.cod_unidade
         join funcao f on f.codigo = relator.cod_funcao and f.cod_empresa = u.cod_empresa
         join equipe e on e.codigo = relator.cod_equipe and e.cod_unidade = relator.cod_unidade
where r.cod_unidade = f_cod_unidade
  and r.data_hora_database::date >= f_data_inicial
  and r.data_hora_database::date <= f_data_final
  and e.codigo::text like f_equipe
$$;