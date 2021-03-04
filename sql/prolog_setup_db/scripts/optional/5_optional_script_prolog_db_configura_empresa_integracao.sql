insert into integracao.token_integracao (cod_empresa, token_integracao)
values (3, 'didi');

insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (3, 'API_PROLOG', 'PNEUS');
insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (3, 'API_PROLOG', 'PNEU_TRANSFERENCIA');
insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (3, 'API_PROLOG', 'AFERICAO');
insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (3, 'API_PROLOG', 'AFERICAO_SERVICO');
insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (3, 'API_PROLOG', 'MOVIMENTACAO');
insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (3, 'API_PROLOG', 'VEICULOS');
insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (3, 'API_PROLOG', 'VEICULO_TRANSFERENCIA');
insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (3, 'GLOBUS_PICCOLOTUR', 'CHECKLIST');
insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (3, 'GLOBUS_PICCOLOTUR', 'CHECKLIST_MODELO');
insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (3, 'GLOBUS_PICCOLOTUR', 'CHECKLIST_ORDEM_SERVICO');

insert into integracao.empresa_integracao_metodos (cod_integracao_sistema,
                                                   metodo_integrado,
                                                   url_completa,
                                                   api_token_client,
                                                   api_short_code)
values ((select codigo from integracao.empresa_integracao_sistema where cod_empresa = 3 order by codigo limit 1),
        'GET_AUTENTICACAO',
        'http://localhost',
        'abcd',
        1234);
insert into integracao.empresa_integracao_metodos (cod_integracao_sistema,
                                                   metodo_integrado,
                                                   url_completa,
                                                   api_token_client,
                                                   api_short_code)
values ((select codigo from integracao.empresa_integracao_sistema where cod_empresa = 3 order by codigo limit 1),
        'INSERT_MOVIMENTACAO',
        'http://localhost',
        null,
        null);

insert into integracao.empresa_config_carga_inicial (cod_empresa, sobrescreve_pneus, sobrescreve_veiculos)
values (3, true, true);