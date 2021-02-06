DO
$body$
    DECLARE
        codigos bigint[];
        CODIGO_LOOP bigint;
    BEGIN
        with pneus_cte as (
            SELECT P.CODIGO,
                   P.CODIGO_CLIENTE,
                   P.DOT,
                   P.VALOR,
                   U.CODIGO                         AS COD_UNIDADE_ALOCADO,
                   R.CODIGO                         AS COD_REGIONAL_ALOCADO,
                   P.PNEU_NOVO_NUNCA_RODADO,
                   MP.CODIGO                        AS COD_MARCA_PNEU,
                   MP.NOME                          AS NOME_MARCA_PNEU,
                   MOP.CODIGO                       AS COD_MODELO_PNEU,
                   MOP.NOME                         AS NOME_MODELO_PNEU,
                   MOP.QT_SULCOS                    AS QT_SULCOS_MODELO_PNEU,
                   MAB.CODIGO                       AS COD_MARCA_BANDA,
                   MAB.NOME                         AS NOME_MARCA_BANDA,
                   MOP.ALTURA_SULCOS                AS ALTURA_SULCOS_MODELO_PNEU,
                   MOB.CODIGO                       AS COD_MODELO_BANDA,
                   MOB.NOME                         AS NOME_MODELO_BANDA,
                   MOB.QT_SULCOS                    AS QT_SULCOS_MODELO_BANDA,
                   MOB.ALTURA_SULCOS                AS ALTURA_SULCOS_MODELO_BANDA,
                   PVV.VALOR                        AS VALOR_BANDA,
                   PD.ALTURA,
                   PD.LARGURA,
                   PD.ARO,
                   PD.CODIGO                        AS COD_DIMENSAO,
                   P.ALTURA_SULCO_CENTRAL_INTERNO,
                   P.ALTURA_SULCO_CENTRAL_EXTERNO,
                   P.ALTURA_SULCO_INTERNO,
                   P.ALTURA_SULCO_EXTERNO,
                   P.PRESSAO_RECOMENDADA,
                   P.PRESSAO_ATUAL,
                   P.STATUS,
                   P.VIDA_ATUAL,
                   P.VIDA_TOTAL,
                   VP.POSICAO                       AS POSICAO_PNEU,
                   COALESCE(PPNE.NOMENCLATURA, '-') AS POSICAO_APLICADO,
                   VEI.CODIGO                       AS COD_VEICULO,
                   VEI.PLACA                        AS PLACA
            FROM PNEU P
                     JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
                     JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
                     JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
                     JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
                     JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
                     JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
                     LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE
                     LEFT JOIN VEICULO VEI ON VEI.PLACA = VP.PLACA
                     LEFT JOIN VEICULO_TIPO VT ON VT.CODIGO = VEI.COD_TIPO AND VT.COD_EMPRESA = E.CODIGO
                     LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                     LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
                     LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
                     LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
                     LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
                AND PPNE.COD_DIAGRAMA = VD.CODIGO
                AND PPNE.POSICAO_PROLOG = VP.POSICAO
            WHERE P.COD_UNIDADE in (select u.codigo from unidade u where u.cod_empresa = 45)
              AND P.COD_MODELO_BANDA IS NOT NULL
              AND PVV.VALOR IS NULL
            ORDER BY P.CODIGO_CLIENTE ASC
        )

        select ARRAY_AGG(PC.CODIGO)
        from pneus_cte pc
        into codigos;

        FOREACH CODIGO_LOOP IN ARRAY CODIGOS
            LOOP
                PERFORM INTEGRACAO.func_pneu_realiza_incremento_vida_cadastro(
                                f_cod_modelo_banda_pneu := (select pc.cod_modelo_banda from pneu_data pc where pc.codigo = CODIGO_LOOP),
                                f_cod_pneu_prolog := CODIGO_LOOP,
                                f_cod_unidade_pneu := (select pc.cod_unidade from pneu_data pc where pc.codigo = CODIGO_LOOP),
                                f_valor_banda_pneu := 0,
                                f_vida_nova_pneu := (select pc.vida_atual from pneu_data pc where pc.codigo = CODIGO_LOOP));
            END LOOP;
    END;

$body$
LANGUAGE 'plpgsql';