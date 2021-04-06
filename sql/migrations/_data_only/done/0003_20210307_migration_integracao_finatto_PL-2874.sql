begin transaction;
insert into integracao.token_integracao (cod_empresa, token_integracao, ativo)
values (87, 'lgvmm2umklisqcl12ivs23mkpe0eu7lvf6lgchslt1dk9didi86', true);

insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (87, 'WEB_FINATTO', 'AFERICAO');

insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (87, 'WEB_FINATTO', 'EMPRESA');

insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (87, 'WEB_FINATTO', 'MOVIMENTACAO');

insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (87, 'WEB_FINATTO', 'PNEUS');

insert into integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado)
values (87, 'WEB_FINATTO', 'VEICULOS');

insert into integracao.empresa_integracao_metodos (cod_integracao_sistema,
                                                   metodo_integrado,
                                                   url_completa,
                                                   api_token_client,
                                                   api_short_code)
values ((select codigo
         from integracao.empresa_integracao_sistema
         where cod_empresa = 87 and recurso_integrado = 'AFERICAO'),
        'GET_VEICULOS_CRONOGRAMA_AFERICAO',
        'https://apiws.webfinatto.com.br/cronogramaAfericao/',
        null,
        null);

insert into integracao.empresa_integracao_metodos (cod_integracao_sistema,
                                                   metodo_integrado,
                                                   url_completa,
                                                   api_token_client,
                                                   api_short_code)
values ((select codigo
         from integracao.empresa_integracao_sistema
         where cod_empresa = 87 and recurso_integrado = 'AFERICAO'),
        'GET_VEICULO_NOVA_AFERICAO_PLACA',
        'https://apiws.webfinatto.com.br/novaAfericao/',
        null,
        null);

insert into integracao.empresa_integracao_metodos (cod_integracao_sistema,
                                                   metodo_integrado,
                                                   url_completa,
                                                   api_token_client,
                                                   api_short_code)
values ((select codigo
         from integracao.empresa_integracao_sistema
         where cod_empresa = 87 and recurso_integrado = 'AFERICAO'),
        'INSERT_AFERICAO_PLACA',
        'https://apiws.webfinatto.com.br/afericaoPlaca/',
        null,
        null);

insert into integracao.empresa_integracao_metodos (cod_integracao_sistema,
                                                   metodo_integrado,
                                                   url_completa,
                                                   api_token_client,
                                                   api_short_code)
values ((select codigo
         from integracao.empresa_integracao_sistema
         where cod_empresa = 87 and recurso_integrado = 'AFERICAO'),
        'GET_PNEUS_AFERICAO_AVULSA',
        'https://apiws.webfinatto.com.br/listarPneus/',
        null,
        null);

insert into integracao.empresa_integracao_metodos (cod_integracao_sistema,
                                                   metodo_integrado,
                                                   url_completa,
                                                   api_token_client,
                                                   api_short_code)
values ((select codigo
         from integracao.empresa_integracao_sistema
         where cod_empresa = 87 and recurso_integrado = 'AFERICAO'),
        'GET_PNEU_NOVA_AFERICAO_AVULSA',
        'https://apiws.webfinatto.com.br/listarPneuIndividual/',
        null,
        null);

insert into integracao.empresa_integracao_metodos (cod_integracao_sistema,
                                                   metodo_integrado,
                                                   url_completa,
                                                   api_token_client,
                                                   api_short_code)
values ((select codigo
         from integracao.empresa_integracao_sistema
         where cod_empresa = 87 and recurso_integrado = 'AFERICAO'),
        'INSERT_AFERICAO_AVULSA',
        'https://apiws.webfinatto.com.br/afericaoPneuIndividual/',
        null,
        null);

insert into integracao.empresa_integracao_metodos (cod_integracao_sistema,
                                                   metodo_integrado,
                                                   url_completa,
                                                   api_token_client,
                                                   api_short_code)
values ((select codigo
         from integracao.empresa_integracao_sistema
         where cod_empresa = 87 and recurso_integrado = 'AFERICAO'),
        'GET_LOCAIS_DE_MOVIMENTO',
        'https://apiws.webfinatto.com.br/clientes/',
        null,
        null);

insert into integracao.empresa_integracao_metodos (cod_integracao_sistema,
                                                   metodo_integrado,
                                                   url_completa,
                                                   api_token_client,
                                                   api_short_code)
values ((select codigo
         from integracao.empresa_integracao_sistema
         where cod_empresa = 87 and recurso_integrado = 'AFERICAO'),
        'INSERT_MOVIMENTACAO',
        'https://apiws.webfinatto.com.br/movimentacao/',
        null,
        null);
end transaction;