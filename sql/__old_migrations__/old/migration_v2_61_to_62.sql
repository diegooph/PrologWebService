BEGIN TRANSACTION ;
-- ########################################################################################################
-- ########################################################################################################
-- ######################## FUNÇÃO PARA BUSCAR A LISTAGEM DE MODELOS DE CHECKLIST #########################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION func_checklist_get_listagem_modelos_checklist(f_cod_unidade bigint, f_cargos text)
  RETURNS TABLE("MODELO" text, "COD_MODELO" bigint, "COD_UNIDADE" bigint, "NOME_CARGO" text, "TIPO_VEICULO" text, "TOTAL_PERGUNTAS" bigint)
LANGUAGE SQL
AS $$
SELECT CM.NOME AS MODELO,
       CM.CODIGO AS COD_MODELO,
       CM.COD_UNIDADE AS COD_UNIDADE,
       F.NOME AS NOME_CARGO,
       VT.NOME AS TIPO_VEICULO,
       COUNT(CP.CODIGO) AS TOTAL_PERGUNTAS
FROM CHECKLIST_MODELO CM
  JOIN CHECKLIST_PERGUNTAS CP ON CM.COD_UNIDADE = CP.COD_UNIDADE
                                 AND CM.CODIGO = CP.COD_CHECKLIST_MODELO
                                 AND CP.STATUS_ATIVO = TRUE
  LEFT JOIN CHECKLIST_MODELO_FUNCAO CMF ON CM.COD_UNIDADE = CMF.COD_UNIDADE
                                      AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO
  LEFT JOIN UNIDADE_FUNCAO UF ON CMF.COD_UNIDADE = UF.COD_UNIDADE
                            AND CMF.COD_FUNCAO = UF.COD_FUNCAO
  LEFT JOIN FUNCAO F ON UF.COD_FUNCAO = F.CODIGO
  LEFT JOIN CHECKLIST_MODELO_VEICULO_TIPO CMVT ON CM.COD_UNIDADE = CMVT.COD_UNIDADE
                                             AND CM.CODIGO = CMVT.COD_MODELO
  LEFT JOIN  VEICULO_TIPO VT ON CMVT.COD_UNIDADE = VT.COD_UNIDADE
                          AND CMVT.COD_TIPO_VEICULO = VT.CODIGO
WHERE CM.COD_UNIDADE = f_cod_unidade
      AND CMF.COD_FUNCAO::TEXT LIKE f_cargos
      AND CM.STATUS_ATIVO = TRUE
GROUP BY CM.NOME, CM.CODIGO, CM.COD_UNIDADE, F.NOME, VT.NOME
ORDER BY CM.CODIGO;
$$;
-- ########################################################################################################
-- ########################################################################################################
-- Function para executar os updates no modelo de checklist
CREATE OR REPLACE FUNCTION func_checklist_update_modelo_checklist(
  f_nome_modelo text, f_cod_unidade bigint, f_cod_modelo bigint, f_cod_cargos bigint[], f_cod_tipos_veiculos bigint[])
  RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
DECLARE
  tipos_veiculo_excluir bigint;
  tipos_veiculo_deletados bigint;
  cargos_excluir bigint;
  cargos_deletados bigint;
BEGIN
  UPDATE CHECKLIST_MODELO SET NOME = f_nome_modelo WHERE COD_UNIDADE = f_cod_unidade AND CODIGO = f_cod_modelo;
  SELECT COUNT(*) FROM CHECKLIST_MODELO_VEICULO_TIPO WHERE COD_UNIDADE = f_cod_unidade AND COD_MODELO = f_cod_modelo INTO tipos_veiculo_excluir;
  SELECT COUNT(*) FROM CHECKLIST_MODELO_FUNCAO WHERE COD_UNIDADE = f_cod_unidade AND COD_CHECKLIST_MODELO = f_cod_modelo INTO cargos_excluir;
  WITH T AS (
      DELETE FROM CHECKLIST_MODELO_VEICULO_TIPO WHERE COD_UNIDADE = f_cod_unidade AND COD_MODELO = f_cod_modelo RETURNING *
  ) SELECT COUNT(*) FROM T INTO tipos_veiculo_deletados;
  WITH T AS (
      DELETE FROM CHECKLIST_MODELO_FUNCAO WHERE COD_UNIDADE = f_cod_unidade AND COD_CHECKLIST_MODELO = f_cod_modelo RETURNING *
  ) SELECT COUNT(*) FROM T INTO cargos_deletados;
  IF tipos_veiculo_excluir = tipos_veiculo_deletados
  THEN
    INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO(COD_UNIDADE, COD_MODELO, COD_TIPO_VEICULO)
    VALUES (f_cod_unidade, f_cod_modelo, unnest(f_cod_tipos_veiculos));
  ELSE
    RAISE EXCEPTION 'Não foi possível limpar as entradas da tabela CHECKLIST_MODELO_VEICULO_TIPO';
  END IF;
  IF cargos_excluir = cargos_deletados
  THEN
    INSERT INTO CHECKLIST_MODELO_FUNCAO(COD_UNIDADE, COD_CHECKLIST_MODELO, COD_FUNCAO)
    VALUES (f_cod_unidade, f_cod_modelo, unnest(f_cod_cargos));
  ELSE
    RAISE EXCEPTION 'Não foi possível limpar as entradas da tabela CHECKLIST_MODELO_FUNCAO';
  END IF;
  RETURN TRUE;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ########## ADICIONA QTD DE SULCOS E PRESSÃO IDEAL NO RELATÓRIO #########################################
-- ########################################################################################################
-- ########################################################################################################
DROP FUNCTION func_relatorio_pneu_resumo_geral_pneus(f_cod_unidade bigint, f_status_pneu text, f_time_zone_unidade text);

create function func_relatorio_pneu_resumo_geral_pneus(f_cod_unidade bigint, f_status_pneu text, f_time_zone_unidade text)
  returns TABLE(
    "PNEU" text,
    "STATUS" text,
    "VALOR DE AQUISIÇÃO" TEXT,
    "MARCA" text,
    "MODELO" text,
    "BANDA APLICADA" text,
    "MEDIDAS" text,
    "PLACA" text,
    "TIPO" text,
    "POSIÇÃO" text,
    "QUANTIDADE DE SULCOS" TEXT,
    "SULCO INTERNO" text,
    "SULCO CENTRAL INTERNO" text,
    "SULCO CENTRAL EXTERNO" text,
    "SULCO EXTERNO" text,
    "PRESSÃO ATUAL (PSI)" text,
    "PRESSÃO IDEAL (PSI)" text,
    "VIDA ATUAL" text,
    "DOT" text,
    "ÚLTIMA AFERIÇÃO" text)
language sql
as $$
SELECT
  P.codigo_cliente                                         AS COD_PNEU,
  P.STATUS AS STATUS,
  CASE WHEN P.VALOR IS NULL THEN '-' ELSE P.VALOR::TEXT END AS VALOR,
  map.nome                                         AS NOME_MARCA_PNEU,
  mp.nome                                          AS NOME_MODELO_PNEU,
  CASE WHEN MARB.CODIGO IS NULL
    THEN 'Nunca Recapado'
    ELSE MARB.NOME || ' - ' || MODB.NOME
  END AS BANDA_APLICADA,
  ((((dp.largura || '/' :: TEXT) || dp.altura) || ' R' :: TEXT) ||
   dp.aro)                                         AS MEDIDAS,
  coalesce(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU,
           '-')                                    AS PLACA,
  coalesce(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-') AS TIPO_VEICULO,
  coalesce(POSICAO_PNEU_VEICULO.POSICAO_PNEU,
           '-')                                    AS POSICAO_PNEU,
  COALESCE(MODB.qt_sulcos, MP.qt_sulcos) :: TEXT   AS QTD_SULCOS,
  coalesce(trunc(P.altura_sulco_interno :: NUMERIC, 2) :: TEXT,
           '-')                                    AS SULCO_INTERNO,
  coalesce(trunc(P.altura_sulco_central_interno :: NUMERIC, 2) :: TEXT,
           '-')                                    AS SULCO_CENTRAL_INTERNO,
  coalesce(trunc(P.altura_sulco_central_externo :: NUMERIC, 2) :: TEXT,
           '-')                                    AS SULCO_CENTRAL_EXTERNO,
  coalesce(trunc(P.altura_sulco_externo :: NUMERIC, 2) :: TEXT,
           '-')                                    AS SULCO_EXTERNO,
  coalesce(trunc(P.pressao_atual) :: TEXT,
           '-')                                    AS PRESSAO_ATUAL,
  P.pressao_recomendada :: TEXT                    AS PRESSAO_RECOMENDADA,
  PVN.nome :: TEXT                             AS VIDA_ATUAL,
  COALESCE(P.DOT, '-')                             AS DOT,
  coalesce(to_char(DATA_ULTIMA_AFERICAO.ULTIMA_AFERICAO AT TIME ZONE F_TIME_ZONE_UNIDADE, 'DD/MM/YYYY HH24:MI'),
           'Nunca Aferido')                          AS ULTIMA_AFERICAO
FROM PNEU P
  JOIN dimensao_pneu dp ON dp.codigo = p.cod_dimensao
  JOIN unidade u ON u.codigo = p.cod_unidade
  JOIN modelo_pneu mp ON mp.codigo = p.cod_modelo AND mp.cod_empresa = u.cod_empresa
  JOIN marca_pneu map ON map.codigo = mp.cod_marca
  JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = p.vida_atual
  LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.cod_modelo_banda
  LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.cod_marca
  LEFT JOIN
  (SELECT
     PON.nomenclatura AS POSICAO_PNEU,
     VP.cod_pneu      AS CODIGO_PNEU,
     VP.placa         AS PLACA_VEICULO_PNEU,
     VP.cod_unidade   AS COD_UNIDADE_PNEU,
     VT.nome          AS VEICULO_TIPO
   FROM veiculo V
     JOIN veiculo_pneu VP ON VP.placa = V.placa AND VP.cod_unidade = V.cod_unidade
     JOIN veiculo_tipo vt ON v.cod_unidade = vt.cod_unidade AND v.cod_tipo = vt.codigo
     -- LEFT JOIN porque unidade pode não ter
     LEFT JOIN pneu_ordem_nomenclatura_unidade pon ON pon.cod_unidade = v.cod_unidade AND pon.cod_tipo_veiculo = v.cod_tipo
                                                 AND vp.posicao = pon.posicao_prolog
   WHERE V.cod_unidade = F_COD_UNIDADE
   ORDER BY VP.cod_pneu) AS POSICAO_PNEU_VEICULO
    ON P.codigo = POSICAO_PNEU_VEICULO.CODIGO_PNEU AND P.cod_unidade = POSICAO_PNEU_VEICULO.COD_UNIDADE_PNEU
  LEFT JOIN
  (SELECT
     AV.cod_pneu,
     A.cod_unidade                  AS COD_UNIDADE_DATA,
     MAX(A.data_hora AT TIME ZONE F_TIME_ZONE_UNIDADE) AS ULTIMA_AFERICAO
   FROM AFERICAO A
     JOIN afericao_valores AV ON A.codigo = AV.cod_afericao
   GROUP BY 1, 2) AS DATA_ULTIMA_AFERICAO
    ON DATA_ULTIMA_AFERICAO.COD_UNIDADE_DATA = P.cod_unidade AND DATA_ULTIMA_AFERICAO.cod_pneu = P.codigo
WHERE P.COD_UNIDADE = F_COD_UNIDADE
      AND
      CASE
      WHEN F_STATUS_PNEU IS NULL
        THEN TRUE
      ELSE P.STATUS = F_STATUS_PNEU
      END
ORDER BY P.CODIGO_CLIENTE;
$$;
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ########## GARANTE QUE PNEUS ESTEJAM SEMPRE MAPEADOS À VEÍCULOS DA MESMA UNIDADE #######################
-- ########################################################################################################
-- ########################################################################################################
ALTER TABLE PNEU ADD CONSTRAINT UNIQUE_PNEU_UNIDADE UNIQUE (CODIGO, COD_UNIDADE);
ALTER TABLE VEICULO ADD CONSTRAINT UNIQUE_VEICULO_UNIDADE UNIQUE (PLACA, COD_UNIDADE);
ALTER TABLE VEICULO_PNEU DROP CONSTRAINT FK_VEICULO_PNEU_PNEU;
ALTER TABLE VEICULO_PNEU DROP CONSTRAINT FK_VEICULO_PNEU_VEICULO;
ALTER TABLE VEICULO_PNEU ADD CONSTRAINT FK_VEICULO_PNEU_PNEU FOREIGN KEY (COD_PNEU, COD_UNIDADE) REFERENCES PNEU(CODIGO, COD_UNIDADE);
ALTER TABLE VEICULO_PNEU ADD CONSTRAINT FK_VEICULO_PNEU_VEICULO FOREIGN KEY (PLACA, COD_UNIDADE) REFERENCES VEICULO(PLACA, COD_UNIDADE);

-- Remove trigger não mais necessária.
DROP TRIGGER tg_veiculo_pneu_mesma_unidade ON veiculo_pneu;
DROP FUNCTION tg_func_veiculo_pneu_mesma_unidade();
-- ########################################################################################################
-- ########################################################################################################

END TRANSACTION ;