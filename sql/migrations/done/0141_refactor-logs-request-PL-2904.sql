-- Caso a migration estiver apresetando erro no 'cod_empresa', descomentar o código abaixo e rodar.
-- alter table integracao.log_request_response
--     rename column cod_emresa to cod_empresa;

alter table integracao.log_request_response
    add column log_type text;

-- noinspection SqlWithoutWhere
update integracao.log_request_response
set log_type = 'FROM_API';

alter table integracao.log_request_response
    alter column log_type set not null;

drop function integracao.func_geral_salva_log_integracao(f_token_integracao character varying,
    f_response_status integer,
    f_request_json jsonb,
    f_response_json jsonb,
    f_data_hora_request timestamp with time zone);
create or replace function integracao.func_geral_salva_log_integracao(f_log_type text,
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
    insert into integracao.log_request_response(cod_empresa,
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