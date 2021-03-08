begin transaction;
-- #####################################################################################################################
-- #####################################################################################################################
-- Loga requisições da empresa Grupo Horizonte.
insert into integracao.token_integracao(cod_empresa, token_integracao, ativo)
values (5, '8jc9j0hskt3ng8omg788cbcadgrldidiug87ebdmeohj02gfm5t', true);

insert into integracao.empresa_integracao_metodos(cod_integracao_sistema,
                                                  metodo_integrado,
                                                  url_completa,
                                                  api_token_client,
                                                  api_short_code)
values((select codigo from integracao.empresa_integracao_sistema where cod_empresa = 5),
       'GET_AUTENTICACAO',
       'http://187.103.73.245:8082/token',
       null,
       null);

insert into integracao.empresa_integracao_metodos(cod_integracao_sistema,
                                                  metodo_integrado,
                                                  url_completa,
                                                  api_token_client,
                                                  api_short_code)
values((select codigo from integracao.empresa_integracao_sistema where cod_empresa = 5),
       'INSERT_AFERICAO_AVULSA',
       'http://187.103.73.245:8082/api/AfericaoAvulsaRealizada',
       null,
       null);

insert into integracao.empresa_integracao_metodos(cod_integracao_sistema,
                                                  metodo_integrado,
                                                  url_completa,
                                                  api_token_client,
                                                  api_short_code)
values((select codigo from integracao.empresa_integracao_sistema where cod_empresa = 5),
       'INSERT_AFERICAO_PLACA',
       'http://187.103.73.245:8082/api/AfericaoRealizada',
       null,
       null);
end transaction;