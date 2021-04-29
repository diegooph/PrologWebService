-- PROCESSO DE CONFIGURAÇÃO DE CAMPOS PERSONALIZADOS
-- 1 - Cadastrar os campos personalizados da empresa
-- LIP
insert into campo_personalizado_empresa (cod_empresa,
                                         cod_tipo_campo,
                                         cod_funcao_prolog_agrupamento,
                                         nome,
                                         descricao,
                                         texto_auxilio_preenchimento,
                                         permite_selecao_multipla,
                                         opcoes_selecao,
                                         data_hora_ultima_atualizacao,
                                         cod_colaborador_ultima_atualizacao)
values (15,
        1,
        1, -- Aferição
        'LIP',
        'Laudo de inspeção de pneu',
        'Selecione...',
        false,
        '{}', -- Vazio pois as informações virão do Protheus
        now(),
        4034);

-- ORIGEM
insert into campo_personalizado_empresa (cod_empresa,
                                         cod_tipo_campo,
                                         cod_funcao_prolog_agrupamento,
                                         nome,
                                         descricao,
                                         texto_auxilio_preenchimento,
                                         permite_selecao_multipla,
                                         opcoes_selecao,
                                         data_hora_ultima_atualizacao,
                                         cod_colaborador_ultima_atualizacao)
values (15,
        1,
        1, -- Aferição
        'Origem',
        'Unidade de Origem',
        'Selecione...',
        false,
        '{}', -- Vazio pois as informações virão do Protheus
        now(),
        4034);

-- DESTINO
insert into campo_personalizado_empresa (cod_empresa,
                                         cod_tipo_campo,
                                         cod_funcao_prolog_agrupamento,
                                         nome,
                                         descricao,
                                         texto_auxilio_preenchimento,
                                         permite_selecao_multipla,
                                         opcoes_selecao,
                                         data_hora_ultima_atualizacao,
                                         cod_colaborador_ultima_atualizacao)
values (15,
        1,
        1, -- Aferição
        'Destino',
        'Destino do Pneu',
        'Selecione...',
        false,
        '{D1-Conserto,D2-Estoque Usados,D3-Sucata,D4-Garantia Fabricante,D5-Garantia Reformador,D6-Reforma}',
        now(),
        4034);

-- CODIGO SUCATA
insert into campo_personalizado_empresa (cod_empresa,
                                         cod_tipo_campo,
                                         cod_funcao_prolog_agrupamento,
                                         nome,
                                         descricao,
                                         texto_auxilio_preenchimento,
                                         permite_selecao_multipla,
                                         opcoes_selecao,
                                         data_hora_ultima_atualizacao,
                                         cod_colaborador_ultima_atualizacao)
values (15,
        1,
        1, -- Aferição
        'Cod Sucata',
        'Código de sucata. Apenas se destino = D3 - Sucata',
        'Selecione...',
        false,
        '{}', -- Vazio pois as informações virão do Protheus
        now(),
        4034);

-- OBSERVACAO
insert into campo_personalizado_empresa (cod_empresa,
                                         cod_tipo_campo,
                                         cod_funcao_prolog_agrupamento,
                                         nome,
                                         descricao,
                                         texto_auxilio_preenchimento,
                                         permite_selecao_multipla,
                                         opcoes_selecao,
                                         data_hora_ultima_atualizacao,
                                         cod_colaborador_ultima_atualizacao)
values (15,
        2,
        1, -- Aferição
        'Observação',
        'Observação do processo',
        'Digite...',
        null,
        null,
        now(),
        4034);

-- 2 - Vincula os campos personalizados para as unidades
-- LIP
insert into afericao_campo_personalizado_unidade (cod_campo,
                                                  cod_unidade,
                                                  cod_funcao_prolog_agrupamento,
                                                  preenchimento_obrigatorio,
                                                  mensagem_caso_campo_nao_preenchido,
                                                  ordem_exibicao,
                                                  tipo_processo_coleta_afericao)
select (select codigo from campo_personalizado_empresa where nome = 'LIP'),
       unnest((select array_agg(codigo) as cod
               from unidade
               where cod_empresa = 15)),
       1, -- Aferição
       true,
       'Seleção obrigatória',
       1,
       'PNEU_AVULSO';

-- ORIGEM
insert into afericao_campo_personalizado_unidade (cod_campo,
                                                  cod_unidade,
                                                  cod_funcao_prolog_agrupamento,
                                                  preenchimento_obrigatorio,
                                                  mensagem_caso_campo_nao_preenchido,
                                                  ordem_exibicao,
                                                  tipo_processo_coleta_afericao)
select (select codigo from campo_personalizado_empresa where nome = 'Origem'),
       unnest((select array_agg(codigo) as cod
               from unidade
               where cod_empresa = 15)),
       1, -- Aferição
       true,
       'Seleção obrigatória',
       2,
       'PNEU_AVULSO';

-- DESTINO
insert into afericao_campo_personalizado_unidade (cod_campo,
                                                  cod_unidade,
                                                  cod_funcao_prolog_agrupamento,
                                                  preenchimento_obrigatorio,
                                                  mensagem_caso_campo_nao_preenchido,
                                                  ordem_exibicao,
                                                  tipo_processo_coleta_afericao)
select (select codigo from campo_personalizado_empresa where nome = 'Destino'),
       unnest((select array_agg(codigo) as cod
               from unidade
               where cod_empresa = 15)),
       1, -- Aferição
       true,
       'Seleção obrigatória',
       3,
       'PNEU_AVULSO';

-- CODIGO SUCATA
insert into afericao_campo_personalizado_unidade (cod_campo,
                                                  cod_unidade,
                                                  cod_funcao_prolog_agrupamento,
                                                  preenchimento_obrigatorio,
                                                  mensagem_caso_campo_nao_preenchido,
                                                  ordem_exibicao,
                                                  tipo_processo_coleta_afericao)
select (select codigo from campo_personalizado_empresa where nome = 'Cod Sucata'),
       unnest((select array_agg(codigo) as cod
               from unidade
               where cod_empresa = 15)),
       1, -- Aferição
       false,
       null,
       4,
       'PNEU_AVULSO';

-- OBSERVACAO
insert into afericao_campo_personalizado_unidade (cod_campo,
                                                  cod_unidade,
                                                  cod_funcao_prolog_agrupamento,
                                                  preenchimento_obrigatorio,
                                                  mensagem_caso_campo_nao_preenchido,
                                                  ordem_exibicao,
                                                  tipo_processo_coleta_afericao)
select (select codigo from campo_personalizado_empresa where nome = 'Observação'),
       unnest((select array_agg(codigo) as cod
               from unidade
               where cod_empresa = 15)),
       1, -- Aferição
       false,
       null,
       5,
       'PNEU_AVULSO';

-- 3 - Configurar o arquivo 'integracao_parametros_nepomuceno.yaml' no servidor
-- Devemos inserir os códigos dos campos pernsonalizados neste arquivo para a integração funcionar.

-- 4 - Configurar URLs das aferições avulsas
-- O serviço de homologação para a integração da inspeção de removido da Expresso Nepomuceno deve estar no ar também.