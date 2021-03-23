-- Essa migração deve ser executada quando o WS versão 39 for publicado.

BEGIN TRANSACTION;
  -- ########################################################################################################
  -- Adiciona DOT na tabela de PNEU.
  ALTER TABLE PNEU ADD COLUMN DOT VARCHAR(20);
  COMMENT ON COLUMN PNEU.DOT IS 'O código DOT gravado na lateral do pneu indica sua conformidade com os padrões de segurança e fornece dados sobre a fabricação do pneu.';
  -- ########################################################################################################

  -- ########################################################################################################
  -- Adiciona data de fechamento na tabela de TREINAMENTO.
  ALTER TABLE TREINAMENTO ADD COLUMN DATA_FECHAMENTO DATE;
  COMMENT ON COLUMN TREINAMENTO.DATA_FECHAMENTO IS 'A data a partir da qual esse treinamento não estará mais disponível para visualização.';
  -- ########################################################################################################

  -- ########################################################################################################
  -- Adiciona valor na tabela de PNEU.
  ALTER TABLE PNEU ADD COLUMN VALOR REAL NULL;
  COMMENT ON COLUMN PNEU.VALOR IS 'O valor pago pelo pneu como um todo (estrutura + banda) em sua primeira vida.';
  UPDATE PNEU SET VALOR = 0 WHERE VALOR IS NULL;
  ALTER TABLE PNEU ALTER COLUMN VALOR SET NOT NULL;
  -- ########################################################################################################
END TRANSACTION;

BEGIN TRANSACTION ;
  ALTER TABLE UNIDADE_FUNCAO_PRODUTIVIDADE ADD COLUMN DIA_INICIO_PRODUTIVIDADE INT;
  COMMENT ON COLUMN UNIDADE_FUNCAO_PRODUTIVIDADE.dia_inicio_produtividade IS 'Dia em que inicia um novo período de remuneração variável';
  ALTER TABLE UNIDADE_FUNCAO_PRODUTIVIDADE ADD CONSTRAINT UNIDADE_FUNCAO_PRODUTIVIDADE_CHECK_DIA CHECK(DIA_INICIO_PRODUTIVIDADE BETWEEN 1 AND 28);
  -- Função que calcula o primeiro dia de produtividade com base nas informações passadas pelos parâmetros
  CREATE OR REPLACE FUNCTION func_get_data_inicio_produtividade(f_ano INT, f_mes INT, f_cpf BIGINT, f_cod_unidade BIGINT) RETURNS date
  LANGUAGE plpgsql
  AS $$
  DECLARE
    dia INT;
    error_msg TEXT;
    cod_unidade_busca BIGINT;
  BEGIN
    CASE WHEN f_cpf IS NULL THEN
      cod_unidade_busca := f_cod_unidade;
      ELSE
      cod_unidade_busca := (SELECT COD_UNIDADE FROM COLABORADOR WHERE CPF = f_cpf);
    END CASE;
    SELECT (SELECT dia_inicio_produtividade FROM unidade_funcao_produtividade WHERE cod_unidade = cod_unidade_busca) INTO dia;
    CASE
      WHEN dia IS NULL THEN
      error_msg := 'Unidade ' || cod_unidade_busca || ' não possui dia de inicio da produtividade';
      RAISE EXCEPTION '%', error_msg;
      ELSE
      RETURN CONCAT(f_ano, '-', f_mes-1, '-', dia);
    END CASE;
  END;
  $$;
  -- Função que calcula o último dia de produtividade com base nas informações passadas pelos parâmetros
  CREATE OR REPLACE FUNCTION func_get_data_fim_produtividade(f_ano INT, f_mes INT, f_cpf BIGINT, f_cod_unidade BIGINT) RETURNS date
  LANGUAGE plpgsql
  AS $$
  DECLARE
  BEGIN
    RETURN (func_get_data_inicio_produtividade(f_ano, f_mes, f_cpf, f_cod_unidade) + '1 month'::INTERVAL ) + '-1 day'::INTERVAL;
  END;
  $$;
  -- Function que busca a produtividade de um colaborador de um determinado período
  CREATE OR REPLACE FUNCTION func_get_produtividade_colaborador(f_mes INT, f_ano INT, f_cpf BIGINT)
    RETURNS TABLE(
  "cod_unidade" INT,
  "matricula_ambev" INT,
  "data" DATE,
  "cpf" BIGINT,
  "nome_colaborador" VARCHAR(255),
  "data_nascimento" DATE,
  "funcao" VARCHAR(40),
  "cod_funcao" BIGINT,
  "nome_equipe" VARCHAR(255),
  "fator" REAL,
  "cargaatual" VARCHAR(20),
  "entrega" VARCHAR(20),
  "mapa" INT,
  "placa" VARCHAR(7),
  "cxcarreg" REAL,
  "cxentreg" REAL,
  "qthlcarregados" REAL,
  "qthlentregues" REAL,
  "qtnfcarregadas" INT,
  "qtnfentregues" INT,
  "entregascompletas" INT,
  "entregasnaorealizadas" INT,
  "entregasparciais" INT,
  "kmprevistoroad" REAL,
  "kmsai" INT,
  "kmentr" INT,
  "tempoprevistoroad" INT,
  "hrsai" TIMESTAMP,
  "hrentr" TIMESTAMP,
  "tempo_rota" INT,
  "tempointerno" INT,
  "hrmatinal" TIME,
  "apontamentos_ok" BIGINT,
  "total_tracking" BIGINT,
  "tempo_largada" INT,
  "meta_tracking" REAL,
  "meta_tempo_rota_mapas" REAL,
  "meta_caixa_viagem" REAL,
  "meta_dev_hl" REAL,
  "meta_dev_nf" REAL,
  "meta_dev_pdv" REAL,
  "meta_dispersao_km" REAL,
  "meta_dispersao_tempo" REAL,
  "meta_jornada_liquida_mapas" REAL,
  "meta_raio_tracking" REAL,
  "meta_tempo_interno_mapas" REAL,
  "meta_tempo_largada_mapas" REAL,
  "meta_tempo_rota_horas" INT,
  "meta_tempo_interno_horas" INT,
  "meta_tempo_largada_horas" INT,
  "meta_jornada_liquida_horas" INT,
  "valor_rota" REAL,
  "valor_recarga" REAL,
  "valor_diferenca_eld" DOUBLE PRECISION,
  "valor_as" REAL,
  "valor" DOUBLE PRECISION) AS
  $func$
  SELECT * FROM VIEW_PRODUTIVIDADE_EXTRATO
  WHERE DATA BETWEEN func_get_data_inicio_produtividade(f_ano, f_mes, f_cpf, null) AND
        func_get_data_fim_produtividade(f_ano, f_mes, f_cpf, null) AND cpf = f_cpf  ORDER BY DATA ASC
  $func$ LANGUAGE SQL;
  -- Function que calcula o total da produtividade dos colaboradores e ordena do mais alto para o mais baixo
  CREATE OR REPLACE FUNCTION func_get_produtividade_consolidado_colaboradores(f_data_inicial DATE, f_data_final DATE, f_cod_unidade BIGINT,
                                                                f_equipe TEXT, f_funcao TEXT)
    RETURNS TABLE(
  "cpf" BIGINT,
      "matricula_ambev" INT,
      "nome" TEXT,
      "data_nascimento" DATE,
      "funcao" TEXT,
      "mapas" BIGINT,
      "caixas" REAL,
      "valor" DOUBLE PRECISION) AS
  $func$
  SELECT cpf, matricula_ambev, nome_colaborador AS nome, data_nascimento, funcao, count(mapa) as mapas, sum(cxentreg) as caixas,
  sum(valor) as valor
  FROM VIEW_PRODUTIVIDADE_EXTRATO
  WHERE data between f_data_inicial and f_data_final and cod_unidade = f_cod_unidade and nome_equipe like f_equipe and cod_funcao::text like f_funcao
  GROUP BY 1,2,3,4,5
  order by funcao, valor desc, nome
  $func$ LANGUAGE SQL;
END TRANSACTION;

BEGIN TRANSACTION;
  CREATE OR REPLACE FUNCTION func_relatorio_extrato_relatos(f_data_inicial DATE, f_data_final DATE, f_cod_unidade BIGINT, f_equipe TEXT)
    RETURNS TABLE(
      "CÓDIGO" BIGINT,
      "DATA DO ENVIO" TEXT,
      "INVÁLIDO" CHAR,
      "ENVIADO" CHAR,
      "CLASSIFICADO" CHAR,
      "FECHADO" CHAR,
      "ALTERNATIVA" TEXT,
      "DESCRIÇÃO" TEXT,
      "COD_PDV" INT,
      "COLABORADOR" TEXT,
      "DATA CLASSIFICAÇÃO" TEXT,
      "TEMPO PARA CLASSIFICAÇÃO (DIAS)" INT,
      "CLASSIFICADO POR" TEXT,
      "DATA FECHAMENTO" TEXT,
      "TEMPO PARA FECHAMENTO (DIAS)" INT,
      "FECHADO POR" TEXT,
      "OBS FECHAMENTO" TEXT,
      "LATITUDE" TEXT,
      "LONGITUDE" TEXT,
      "LINK MAPS" TEXT,
      "FOTO 1" TEXT,
      "FOTO 2" TEXT,
      "FOTO 3" TEXT
  ) AS
  $func$
  SELECT r.codigo as cod_relato,
    to_char(r.data_hora_database, 'DD/MM/YYYY HH24:MI') AS data_envio,
    case when r.status = 'INVALIDO' THEN 'X' ELSE '' END AS invalido,
    'X'::CHAR AS enviado,
    case when r.status = 'PENDENTE_FECHAMENTO' OR r.status = 'FECHADO' THEN 'X' ELSE '' END AS classificado,
    case when r.status = 'FECHADO' THEN 'X' ELSE '' END AS fechado,
    ra.alternativa as alternativa_selecionada,
    r.resposta_outros as descricao,
    r.cod_pdv as cod_pdv,
    relator.nome as colaborador_envio,
    to_char(r.data_hora_classificacao, 'DD/MM/YYYY HH24:MI') AS data_classificacao,
    extract(day from r.data_hora_classificacao - r.data_hora_database)::INT as dias_para_classificacao,
    classificador.nome AS colaborador_classificacao,
    to_char(r.data_hora_fechamento, 'DD/MM/YYYY HH24:MI') AS data_fechamento,
    extract(DAY FROM r.data_hora_fechamento - r.data_hora_database)::INT AS dias_para_fechamento,
    fechamento.nome AS colaborador_fechamento,
    r.feedback_fechamento,
    r.latitude,
    r.longitude,
    'http://maps.google.com/?q=' || r.latitude || ',' || r.longitude as link_maps,
    r.url_foto_1,
    r.url_foto_2,
    r.url_foto_3
  FROM relato r JOIN colaborador relator ON relator.cpf = r.cpf_colaborador
  LEFT JOIN colaborador classificador ON classificador.cpf = r.cpf_classificacao
  LEFT JOIN colaborador fechamento ON fechamento.cpf = r.cpf_fechamento
  LEFT JOIN relato_alternativa ra ON ra.cod_unidade = r.cod_unidade AND r.cod_alternativa = ra.codigo
  JOIN unidade u ON u.codigo = relator.cod_unidade
  JOIN funcao f ON f.codigo = relator.cod_funcao AND f.cod_empresa = u.cod_empresa
  JOIN equipe e ON e.codigo = relator.cod_equipe AND e.cod_unidade = relator.cod_unidade
  WHERE r.cod_unidade = f_cod_unidade and (r.data_hora_database BETWEEN f_data_inicial AND f_data_final)
  AND e.nome like f_equipe
  $func$ LANGUAGE SQL;
END TRANSACTION;