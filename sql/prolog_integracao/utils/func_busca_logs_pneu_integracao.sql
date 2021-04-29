-- Sobre:
--
-- Function utilizada para consultar e extratificar os logs de pneus da integração.
--
-- Histórico:
-- 2020-08-17 -> Function criado (diogenesvanzella - PL-2924).
-- 2020-10-22 -> Modifica schema da function e da tabela de log (PL-2939).
create or replace function log.func_busca_logs_pneu_integracao(f_cod_empresa bigint,
                                                               f_data_inicio_filtro date,
                                                               f_cod_cliente_pneu text,
                                                               f_apenas_logs_erro boolean default true)
    returns table
            (
                data_hora_request timestamp with time zone,
                operacao          text,
                sucesso           text,
                respose_msg       text,
                dados_request     jsonb,
                dados_respose     jsonb
            )
    language plpgsql
as
$$
declare
begin
    return query
        with logs as (
            select lrr.codigo                          as cod_request,
                   lrr.cod_empresa                     as cod_empresa_request,
                   lrr.data_hora_request               as data_hora_request,
                   (lrr.request_json ->> 'path')::text as request_path,
                   lrr.request_json                    as request_json,
                   lrr.response_json                   as response_json,
                   lrr.response_status                 as response_status
            from log.log_request_response lrr
            where lrr.cod_empresa = f_cod_empresa
              and lrr.data_hora_request::date >= f_data_inicio_filtro
              and f_if(f_apenas_logs_erro, lrr.response_status != 200, true)
              and (lrr.request_json ->> 'body')::text like '%' || f_cod_cliente_pneu || '%'
        ),
             logs_operacoes as (
                 select (case
                             when (l.request_path like '%prolog/v2/api/pneus/atualiza-status%')
                                 then 'Movimentação'
                             when (l.request_path like '%prolog/v2/api/cadastro/carga-inicial-pneu%')
                                 then 'Carga Inicial'
                             when (l.request_path like '%prolog/v2/api/cadastro/cadastro-pneu%')
                                 then 'Cadastro'
                             when (l.request_path like '%prolog/v2/api/cadastro/edicao-pneu%')
                                 then 'Edição'
                             when (l.request_path like '%prolog/v2/api/cadastro/transferencia-pneu%')
                                 then 'Transferência'
                     end) as request_operacao,
                        l.*
                 from logs l
             )

        select lo.data_hora_request                                           as data_hora_request,
               lo.request_operacao                                            as operacao,
               f_if(lo.response_status = 200, 'sucesso'::text, 'falha'::text) as sucesso,
               (case
                    when (lo.request_path like '%prolog/v2/api/pneus/atualiza-status%')
                        then f_if(lo.response_status != 200,
                                  (((select lo.response_json ->> 'errorBody')::jsonb ->> 'message')::text),
                                  (((select lo.response_json ->> 'body')::jsonb ->> 'msg')::text))
                    when (lo.request_path like '%prolog/v2/api/cadastro/carga-inicial-pneu%')
                        then (select ((temp.body::jsonb ->> 'mensagem')::text)
                              from (select jsonb_array_elements((lo.response_json)) as body) as temp
                              where temp.body ->> 'codigoCliente' = f_cod_cliente_pneu)
                    when (lo.request_path like '%prolog/v2/api/cadastro/cadastro-pneu%')
                        then f_if(lo.response_status != 200,
                                  (((select lo.response_json ->> 'errorBody')::jsonb ->> 'message')::text),
                                  (((select lo.response_json ->> 'body')::jsonb ->> 'msg')::text))
                    when (lo.request_path like '%prolog/v2/api/cadastro/edicao-pneu%')
                        then f_if(lo.response_status != 200,
                                  (((select lo.response_json ->> 'errorBody')::jsonb ->> 'message')::text),
                                  (((select lo.response_json ->> 'body')::jsonb ->> 'msg')::text))
                    when (lo.request_path like '%prolog/v2/api/cadastro/transferencia-pneu%')
                        then f_if(lo.response_status != 200,
                                  (((select lo.response_json ->> 'errorBody')::jsonb ->> 'message')::text),
                                  (((select lo.response_json ->> 'body')::jsonb ->> 'msg')::text))
                   end)                                                       as respose_msg,
               (case
                    when (lo.request_path like '%prolog/v2/api/pneus/atualiza-status%')
                        then (select temp.body::jsonb
                              from (select jsonb_array_elements((lo.request_json ->> 'body')::jsonb) as body) as temp
                              where temp.body ->> 'codigoCliente' = f_cod_cliente_pneu)
                    when (lo.request_path like '%prolog/v2/api/cadastro/carga-inicial-pneu%')
                        then (select temp.body::jsonb
                              from (select jsonb_array_elements((lo.request_json ->> 'body')::jsonb) as body) as temp
                              where temp.body ->> 'codigoCliente' = f_cod_cliente_pneu)
                    when (lo.request_path like '%prolog/v2/api/cadastro/cadastro-pneu%')
                        then ((select lo.request_json ->> 'body')::jsonb)
                    when (lo.request_path like '%prolog/v2/api/cadastro/edicao-pneu%')
                        then ((select lo.request_json ->> 'body')::jsonb)
                    when (lo.request_path like '%prolog/v2/api/cadastro/transferencia-pneu%')
                        then ((select lo.request_json ->> 'body')::jsonb)
                   end)                                                       as dados_request,
               (case
                    when (lo.request_path like '%prolog/v2/api/pneus/atualiza-status%')
                        then f_if(lo.response_status != 200,
                                  (select lo.response_json ->> 'errorBody')::jsonb,
                                  (select lo.response_json ->> 'body')::jsonb)
                    when (lo.request_path like '%prolog/v2/api/cadastro/carga-inicial-pneu%')
                        then (select temp.body::jsonb
                              from (select jsonb_array_elements((lo.response_json)) as body) as temp
                              where temp.body ->> 'codigoCliente' = f_cod_cliente_pneu)
                    when (lo.request_path like '%prolog/v2/api/cadastro/cadastro-pneu%')
                        then f_if(lo.response_status != 200,
                                  (select lo.response_json ->> 'errorBody')::jsonb,
                                  (select lo.response_json ->> 'body')::jsonb)
                    when (lo.request_path like '%prolog/v2/api/cadastro/edicao-pneu%')
                        then f_if(lo.response_status != 200,
                                  (select lo.response_json ->> 'errorBody')::jsonb,
                                  (select lo.response_json ->> 'body')::jsonb)
                    when (lo.request_path like '%prolog/v2/api/cadastro/transferencia-pneu%')
                        then f_if(lo.response_status != 200,
                                  (select lo.response_json ->> 'errorBody')::jsonb,
                                  (select lo.response_json ->> 'body')::jsonb)
                   end)                                                       as dados_response
        from logs_operacoes lo
        order by lo.data_hora_request desc;
end;
$$;