BEGIN TRANSACTION ;
-- ########################################################################################################
-- ########################################################################################################
-- ######################## ADICIONA VALOR DE COMPRA DO PNEU AO RELATÓRIO GERAL ###########################
-- ########################################################################################################
-- ########################################################################################################
DROP FUNCTION func_relatorio_pneu_resumo_geral_pneus(f_cod_unidade bigint, f_status_pneu text, f_time_zone_unidade text);

create function func_relatorio_pneu_resumo_geral_pneus(f_cod_unidade bigint, f_status_pneu text, f_time_zone_unidade text)
  returns TABLE("PNEU" text, "STATUS" text, "VALOR DE AQUISIÇÃO" TEXT, "MARCA" text, "MODELO" text, "BANDA APLICADA" text, "MEDIDAS" text, "PLACA" text, "TIPO" text, "POSIÇÃO" text, "SULCO INTERNO" text, "SULCO CENTRAL INTERNO" text, "SULCO CENTRAL EXTERNO" text, "SULCO EXTERNO" text, "PRESSÃO (PSI)" text, "VIDA ATUAL" text, "DOT" text, "ÚLTIMA AFERIÇÃO" text)
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
WHERE P.cod_unidade = F_COD_UNIDADE AND CASE WHEN F_STATUS_PNEU IS NULL THEN TRUE ELSE P.STATUS = F_STATUS_PNEU END
ORDER BY P.CODIGO_CLIENTE;
$$;
-- ########################################################################################################
-- ########################################################################################################
END TRANSACTION ;