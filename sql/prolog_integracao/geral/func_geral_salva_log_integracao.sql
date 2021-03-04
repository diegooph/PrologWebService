-- Sobre:
--
-- Function utilizada nas para salvar os logs das requisições e respostas em integrações. A coluna 'response_status'
-- foi inserida na tabela para facilitar a analise dos dados, podendo facilmente filtrar pelas respostas que geraram
-- erro.
-- Para cenários de token repetido, escolhemos qualquer código de empresa. Perdemos um pouco da assertividade dos logs
-- porém é o que temos pra esse momento ¯\_(ツ)_/¯.
--
-- Histórico:
-- 2019-09-18 -> Function criada (diogenesvanzella - PL-2306).
-- 2020-04-13 -> Corrige function para buscar apenas um token (diogenesvanzella - PLI-2306).
-- 2020-08-17 -> Corrige nome de parâmetro (diogenesvanzella - PL-2924).
-- 2020-08-24 -> Adiciona log type na function (diogenesvanzella - PL-2904).
-- 2020-10-22 -> Modifica schema da function e da tabela de log, altera nome da function (PL-2939).
create or replace function log.func_geral_salva_log(f_log_type text,
                                                               f_token_integracao character varying,
                                                               f_response_status integer,
                                                               f_request_json jsonb,
                                                               f_response_json jsonb,
                                                               f_data_hora_request timestamp with time zone)
    returns void
    language plpgsql
as
$$
begin
    insert into log.log_request_response(cod_empresa,
                                                token_integracao,
                                                response_status,
                                                request_json,
                                                response_json,
                                                data_hora_request,
                                                log_type)
    values ((select ti.cod_empresa
             from integracao.token_integracao ti
             where ti.token_integracao = f_token_integracao
             limit 1),
            f_token_integracao,
            f_response_status,
            f_request_json,
            f_response_json,
            f_data_hora_request,
            f_log_type);

    if not found
    then
        perform public.throw_generic_error('Não foi possível inserir o Log de request e response');
    end if;
end;
$$;