-- Essa migração deve ser executada quando o WS versão 46 for publicado.
BEGIN TRANSACTION;
  -- Criação das tabelas utilizadas na parte das dashboards.
  -- ########################################################################################################
   CREATE TABLE IF NOT EXISTS PUBLIC.DASHBOARD_COMPONENTE_TIPO (
     CODIGO SMALLINT NOT NULL,
     IDENTIFICADOR_TIPO VARCHAR(255) UNIQUE NOT NULL,
     NOME VARCHAR(255) NOT NULL,
     DESCRICAO TEXT NOT NULL,
     MAXIMO_BLOCOS_HORIZONTAIS SMALLINT NOT NULL,
     MAXIMO_BLOCOS_VERTICAIS SMALLINT NOT NULL,
     MINIMO_BLOCOS_HORIZONTAIS SMALLINT NOT NULL,
     MINIMO_BLOCOS_VERTICAIS SMALLINT NOT NULL,
     DATA_HORA_CRIACAO TIMESTAMP NOT NULL,
     DATA_HORA_ULTIMA_ALTERACAO TIMESTAMP NOT NULL,
     CONSTRAINT PK_DASHBOARD_COMPONENTE_TIPO PRIMARY KEY (CODIGO)
  );

  CREATE TABLE IF NOT EXISTS PUBLIC.DASHBOARD_COMPONENTE (
    CODIGO SMALLINT NOT NULL,
    TITULO VARCHAR(255) NOT NULL,
    SUBTITULO VARCHAR(255),
    DESCRICAO TEXT NOT NULL,
    QTD_BLOCOS_HORIZONTAIS SMALLINT NOT NULL,
    QTD_BLOCOS_VERTICAIS SMALLINT NOT NULL,
    DATA_HORA_CRIACAO TIMESTAMP NOT NULL,
    DATA_HORA_ULTIMA_ALTERACAO TIMESTAMP NOT NULL,
    COD_PILAR_PROLOG_COMPONENTE SMALLINT NOT NULL,
    COD_TIPO_COMPONENTE SMALLINT NOT NULL,
    URL_ENDPOINT_DADOS TEXT NOT NULL,
    COR_BACKGROUND_HEX VARCHAR(9),
    URL_ICONE TEXT,
    LABEL_EIXO_X VARCHAR(255),
    LABEL_EIXO_Y VARCHAR(255),
    ATIVO BOOLEAN DEFAULT TRUE,
    CONSTRAINT PK_DASHBOARD_COMPONENTE PRIMARY KEY (CODIGO),
    CONSTRAINT FK_DASHBOARD_COMPONENTE_PILAR_PROLOG FOREIGN KEY (COD_PILAR_PROLOG_COMPONENTE)
    REFERENCES PUBLIC.PILAR_PROLOG(CODIGO),
    CONSTRAINT FK_DASHBOARD_COMPONENTE_DASHBOARD_COMPONENTE_TIPO FOREIGN KEY (COD_TIPO_COMPONENTE)
    REFERENCES PUBLIC.DASHBOARD_COMPONENTE_TIPO(CODIGO)
  );

  CREATE TABLE IF NOT EXISTS PUBLIC.DASHBOARD_COMPONENTE_FUNCAO_PROLOG (
    COD_COMPONENTE SMALLINT NOT NULL,
    COD_FUNCAO_PROLOG BIGINT NOT NULL,
    COD_PILAR_PROLOG BIGINT NOT NULL,
    CONSTRAINT PK_DASHBOARD_COMPONENTE_FUNCAO_PROLOG PRIMARY KEY (COD_COMPONENTE, COD_FUNCAO_PROLOG),
    CONSTRAINT FK_DASHBOARD_COMPONENTE_FUNCAO_PROLOG_DASHBOARD_COMPONENTE FOREIGN KEY (COD_COMPONENTE)
      REFERENCES PUBLIC.DASHBOARD_COMPONENTE(CODIGO),
    CONSTRAINT FK_DASHBOARD_COMPONENTE_FUNCAO_PROLOG_FUNCAO_PROLOG FOREIGN KEY (COD_FUNCAO_PROLOG, COD_PILAR_PROLOG)
      REFERENCES PUBLIC.FUNCAO_PROLOG_V11(CODIGO, COD_PILAR)
  );

  CREATE TABLE IF NOT EXISTS PUBLIC.DASHBOARD_COMPONENTE_PERSONALIZACAO (
    CPF_COLABORADOR BIGINT NOT NULL,
    COD_COMPONENTE SMALLINT NOT NULL,
    QTD_BLOCOS_HORIZONTAIS SMALLINT NOT NULL,
    QTD_BLOCOS_VERTICAIS SMALLINT NOT NULL,
    ORDEM_EXIBICAO_COMPONENTE SMALLINT NOT NULL,
    OCULTAR_COMPONENTE BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT PK_DASHBOARD_COMPONENTE_PERSONALIZACAO PRIMARY KEY (CPF_COLABORADOR, COD_COMPONENTE),
    CONSTRAINT FK_DASHBOARD_COMPONENTE_PERSONALIZACAO_COLABORADOR FOREIGN KEY (CPF_COLABORADOR)
      REFERENCES PUBLIC.COLABORADOR(CPF),
    CONSTRAINT FK_DASHBOARD_COMPONENTE_PERSONALIZACAO_DASHBOARD_COMPONENTE FOREIGN KEY (COD_COMPONENTE)
      REFERENCES PUBLIC.DASHBOARD_COMPONENTE(CODIGO)
  );
  -- ########################################################################################################

  -- Cria permissão para visualizar os relatórios dos veículos
  -- ########################################################################################################
  INSERT INTO public.funcao_prolog_v11 (codigo, funcao, cod_pilar) VALUES (122, 'Visualizar relatórios sobre os veículos', 1);
  -- ########################################################################################################

  -- Cria os primeiros componentes utilizados
  -- ########################################################################################################
  -- TIPOS
  INSERT INTO public.dashboard_componente_tipo (codigo, nome, descricao, maximo_blocos_horizontais, maximo_blocos_verticais, minimo_blocos_horizontais, minimo_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, identificador_tipo) VALUES (2, 'Quantidade de Itens', 'Todos os componentes desse tipo serão representados como um componente simples que mostra uma quantidade', 1, 1, 1, 1, '2018-01-25 11:43:28.673000', '2018-01-25 11:43:28.673000', 'QUANTIDADE_ITEM');
  INSERT INTO public.dashboard_componente_tipo (codigo, nome, descricao, maximo_blocos_horizontais, maximo_blocos_verticais, minimo_blocos_horizontais, minimo_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, identificador_tipo) VALUES (3, 'Gráfico em Barras Verticais Agrupadas', 'Todos os componentes desse tipo serão representados como um gráfico em barras verticais agrupadas ', 3, 2, 1, 1, '2018-01-25 15:13:42.496000', '2018-01-25 15:13:42.496000', 'GRAFICO_BARRAS_VERTICAIS_AGRUPADAS');
  INSERT INTO public.dashboard_componente_tipo (codigo, nome, descricao, maximo_blocos_horizontais, maximo_blocos_verticais, minimo_blocos_horizontais, minimo_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, identificador_tipo) VALUES (1, 'Gráfico de Setores', 'Todos os componentes desse tipo serão representados como um gráfico de setores', 2, 2, 1, 1, '2018-01-24 15:26:29.576000', '2018-01-24 15:26:29.576000', 'GRAFICO_SETORES');
  INSERT INTO public.dashboard_componente_tipo (codigo, nome, descricao, maximo_blocos_horizontais, maximo_blocos_verticais, minimo_blocos_horizontais, minimo_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, identificador_tipo) VALUES (5, 'Tabela', 'Todos os componentes desse tipo serão representados como uma tabela', 3, 2, 1, 1, '2018-01-26 10:22:58.234000', '2018-01-26 10:22:58.234000', 'TABELA');
  INSERT INTO public.dashboard_componente_tipo (codigo, nome, descricao, maximo_blocos_horizontais, maximo_blocos_verticais, minimo_blocos_horizontais, minimo_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, identificador_tipo) VALUES (4, 'Gráfico em Barras Verticais', 'Todos os componentes desse tipo serão representados como um gráfico em barras verticais', 3, 2, 1, 1, '2018-01-25 17:32:06.826000', '2018-01-25 17:32:06.826000', 'GRAFICO_BARRAS_VERTICAIS');
  INSERT INTO public.dashboard_componente_tipo (codigo, nome, descricao, maximo_blocos_horizontais, maximo_blocos_verticais, minimo_blocos_horizontais, minimo_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, identificador_tipo) VALUES (6, 'Gráfico de Densidade', 'Todos os componentes desse tipo serão representados como um gráfico de dispersão', 3, 2, 1, 1, '2018-01-26 14:13:56.065000', '2018-01-26 14:13:56.065000', 'GRAFICO_DENSIDADE');

  -- COMPONENTES
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (1, 'Quantidade de pneus por status', null, 'Mostra quantos pneus existem em cada estado', 1, 1, '2018-01-24 17:04:45.605000', '2018-01-24 17:04:45.605000', 1, 1, '/dashboards/pneus/pneus-por-status', null, null, null, null, true);
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (2, 'Veículos ativos', 'Apenas veículos que tenham pneus aplicados', 'Mostra a quantidade de veículos ativos que possuam ao menos algum pneu aplicado', 1, 1, '2018-01-25 11:44:19.598000', '2018-01-25 11:44:19.598000', 1, 2, '/dashboards/veiculos/quantidade-veiculos-ativos-com-pneus-aplicados', '#1976D2', 'https://s3-sa-east-1.amazonaws.com/prolog-dashboards/icones-quantidade-itens/ic_caminhao.png', null, null, true);
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (3, 'Pneus com pressão incorreta', null, 'Mostra a quantidade de pneus com pressão incorreta', 1, 1, '2018-01-25 13:34:32.573000', '2018-01-25 13:34:32.573000', 1, 2, '/dashboards/pneus/quantidade-pneus-pressao-incorreta', '#E74C3C', 'https://s3-sa-east-1.amazonaws.com/prolog-dashboards/icones-quantidade-itens/ic_pneu_furado.png', null, null, true);
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (4, 'Quantidade de aferições na semana atual', 'Separado por dia', 'Mostra a quantidade de aferições na semana atual agrupadas pelo tipo da aferição', 2, 1, '2018-01-25 15:15:52.595000', '2018-01-25 15:15:52.595000', 1, 3, '/dashboards/pneus/quantidade-afericoes-semana-atual', null, null, 'Dias', 'Quantidade de aferições', true);
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (5, 'Quantidade de serviços em aberto por tipo', null, 'Mostra a quantidade de serviços que estão em aberto', 1, 1, '2018-01-25 17:33:20.405000', '2018-01-25 17:33:20.405000', 1, 4, '/dashboards/pneus/quantidade-servicos-abertos-por-tipo', null, null, 'Tipos de serviço', 'Quantidade de serviços em aberto', true);
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (6, 'Resumo das aferições', 'Quantidade de placas que estão com aferição vencida e no prazo', 'Mostra a quantidade de placas que estão com a aferição vencida e no prazo', 1, 1, '2018-01-26 10:10:02.094000', '2018-01-26 10:10:02.094000', 1, 1, '/dashboards/pneus/quantidade-placas-afericoes-vencidas-e-no-prazo', null, null, null, null, true);
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (7, 'Placas com pneu(s) abaixo do limite de milimetragem', null, 'Mostra as placas que possuem um ou mais pneus com sulco abaixo do limite permitido de milimetragem', 1, 1, '2018-01-26 10:24:04.882000', '2018-01-26 10:24:04.882000', 1, 5, '/dashboards/pneus/placas-com-pneus-abaixo-limite-milimetragem', null, null, null, null, true);
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (8, 'Quantidade de KM rodado com pneu(s) com problema', null, 'Mostra a quantidade de quilômetros que cada veículo percorreu tendo pneu(s) com problema', 1, 1, '2018-01-26 13:55:18.273000', '2018-01-26 13:55:18.273000', 1, 5, '/dashboards/pneus/quantidade-km-rodado-com-servico-aberto', null, null, null, null, true);
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (9, 'Menor sulco e pressão de cada pneu', null, 'Mostra a altura do menor sulco e pressão de cada pneu', 2, 1, '2018-01-26 14:16:44.349000', '2018-01-26 14:16:44.349000', 1, 6, '/dashboards/pneus/menor-sulco-e-pressao-pneus', null, null, 'Pressão', 'Sulco', true);
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (10, 'Média de tempo de conserto dos serviços', 'Separado por tipo de serviço e em horas', 'Mostra a média de tempo de conserto, em horas, para cada tipo de serviço dos pneus', 1, 1, '2018-01-26 17:56:19.953000', '2018-01-26 17:56:19.953000', 1, 4, '/dashboards/pneus/media-tempo-conserto-servicos-por-tipo', null, null, 'Tipos de serviço', 'Tempo de conserto em horas', true);
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (12, 'Pneus cadastrados', null, 'Mostra a quantidade de pneus cadastrados no sistema que não estão sucateados', 1, 1, '2018-01-25 13:34:32.573000', '2018-01-25 13:34:32.573000', 1, 2, '/dashboards/pneus/quantidade-pneus-cadastrados', '#249b57', 'https://s3-sa-east-1.amazonaws.com/prolog-dashboards/icones-quantidade-itens/ic_listagem_pneus.png', null, null, true);
  INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (11, 'Relatos realizados hoje', '', 'Mostra a quantidade de relatos que foram realizados no dia de hoje', 1, 1, '2018-02-08 15:18:26.755000', '2018-02-08 15:18:26.755000', 2, 2, '/dashboards/relatos/quantidade-relatos-realizados-hoje', '#1976D2', 'https://s3-sa-east-1.amazonaws.com/prolog-dashboards/icones-quantidade-itens/ic_rosto_capacete.png', null, null, false);

  -- PERMISSÕES
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (1, 110, 1);
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (2, 122, 1);
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (3, 110, 1);
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (4, 110, 1);
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (5, 110, 1);
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (6, 110, 1);
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (7, 110, 1);
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (8, 110, 1);
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (9, 110, 1);
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (10, 110, 1);
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (11, 26, 2);
  INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (12, 110, 1);
  -- ########################################################################################################
END TRANSACTION;