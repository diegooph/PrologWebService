CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PREVISAO_TROCA(F_DATA_INICIAL DATE,
                                                         F_DATA_FINAL DATE,
                                                         F_COD_UNIDADE TEXT[],
                                                         F_STATUS_PNEU CHARACTER VARYING)
    RETURNS TABLE
            (
                "UNIDADE ALOCADO"         TEXT,
                "COD PNEU"                TEXT,
                "STATUS"                  TEXT,
                "VIDA ATUAL"              INTEGER,
                "MARCA"                   TEXT,
                "MODELO"                  TEXT,
                "MEDIDAS"                 TEXT,
                "PLACA APLICADO"          TEXT,
                "POSIÇÃO APLICADO"        TEXT,
                "QTD DE AFERIÇÕES"        BIGINT,
                "DATA 1ª AFERIÇÃO"        TEXT,
                "DATA ÚLTIMA AFERIÇÃO"    TEXT,
                "DIAS ATIVO"              INTEGER,
                "MÉDIA KM POR DIA"        NUMERIC,
                "MAIOR MEDIÇÃO VIDA"      NUMERIC,
                "MENOR SULCO ATUAL"       NUMERIC,
                "MILÍMETROS GASTOS"       NUMERIC,
                "KMS POR MILÍMETRO"       NUMERIC,
                "VALOR VIDA"              REAL,
                "VALOR ACUMULADO"         REAL,
                "VALOR POR KM VIDA ATUAL" NUMERIC,
                "VALOR POR KM ACUMULADO"  NUMERIC,
                "KMS A PERCORRER"         NUMERIC,
                "DIAS RESTANTES"          DOUBLE PRECISION,
                "PREVISÃO DE TROCA"       TEXT,
                "DESTINO"                 TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT VAP."UNIDADE ALOCADO",
       VAP."COD PNEU CLIENTE",
       VAP."STATUS PNEU",
       VAP."VIDA ATUAL",
       VAP."MARCA",
       VAP."MODELO",
       VAP."MEDIDAS",
       VP.PLACA                                 AS PLACA_APLICADO,
       COALESCE(PPNE.NOMENCLATURA, '-') :: TEXT AS POSICAO_APLICADO,
       VAP."QTD DE AFERIÇÕES",
       VAP."DTA 1a AFERIÇÃO",
       VAP."DTA ÚLTIMA AFERIÇÃO",
       VAP."DIAS ATIVO",
       VAP."MÉDIA KM POR DIA",
       VAP."MAIOR MEDIÇÃO VIDA",
       VAP."MENOR SULCO ATUAL",
       VAP."MILIMETROS GASTOS",
       VAP."KMS POR MILIMETRO",
       VAP.VALOR_VIDA_ATUAL,
       VAP.VALOR_ACUMULADO,
       VAP."VALOR POR KM",
       VAP."VALOR POR KM ACUMULADO",
       VAP."KMS A PERCORRER",
       VAP."DIAS RESTANTES",
       TO_CHAR(VAP."PREVISÃO DE TROCA", 'DD/MM/YYYY'),
       VAP."DESTINO"
FROM VIEW_PNEU_ANALISE_VIDA_ATUAL AS VAP
         JOIN VEICULO_PNEU VP
              ON VAP."COD PNEU" = VP.COD_PNEU
         JOIN VEICULO V
              ON VP.PLACA = V.PLACA
         LEFT JOIN VEICULO_TIPO VT
                   ON V.COD_TIPO = VT.CODIGO
         JOIN EMPRESA E ON VT.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
    AND PPNE.COD_DIAGRAMA = VD.CODIGO
    AND VP.POSICAO = PPNE.POSICAO_PROLOG
WHERE VAP.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
  AND VAP."PREVISÃO DE TROCA" <= F_DATA_FINAL
  AND VAP."STATUS PNEU" LIKE F_STATUS_PNEU
ORDER BY VAP."UNIDADE ALOCADO";
$$;