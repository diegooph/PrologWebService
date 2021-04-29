begin transaction;
-- #####################################################################################################################
insert into integracao.empresa_integracao_sistema(cod_empresa, chave_sistema, recurso_integrado, ativo)
values (6, 'RODOPAR_HORIZONTE', 'AFERICAO', true);

insert into integracao.empresa_integracao_metodos(cod_integracao_sistema,
                                                  metodo_integrado,
                                                  url_completa,
                                                  api_token_client,
                                                  api_short_code)
values ((select codigo from integracao.empresa_integracao_sistema where cod_empresa = 6),
        'GET_AUTENTICACAO',
        'http://177.154.133.194:8260/token',
        null,
        null);

insert into integracao.empresa_integracao_metodos(cod_integracao_sistema,
                                                  metodo_integrado,
                                                  url_completa,
                                                  api_token_client,
                                                  api_short_code)
values ((select codigo from integracao.empresa_integracao_sistema where cod_empresa = 6),
        'INSERT_AFERICAO_AVULSA',
        'http://177.154.133.194:8260/api/AfericaoAvulsaRealizada',
        null,
        null);

insert into integracao.empresa_integracao_metodos(cod_integracao_sistema,
                                                  metodo_integrado,
                                                  url_completa,
                                                  api_token_client,
                                                  api_short_code)
values ((select codigo from integracao.empresa_integracao_sistema where cod_empresa = 6),
        'INSERT_AFERICAO_PLACA',
        'http://177.154.133.194:8260/api/AfericaoRealizada',
        null,
        null);
end transaction;