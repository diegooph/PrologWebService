-- Cria function base de busca de km rodado por vida do pneu para ser usado pelo relatório de linhas e pelo de colunas.
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_KM_RODADO_POR_VIDA_BASE(F_COD_UNIDADES BIGINT[])
    RETURNS TABLE
            (
                UNIDADE_ALOCADO       TEXT,
                COD_PNEU              BIGINT,
                COD_CLIENTE_PNEU      TEXT,
                MARCA                 TEXT,
                MODELO                TEXT,
                DIMENSAO              TEXT,
                VIDA_PNEU             INTEGER,
                VALOR_VIDA            NUMERIC,
                KM_RODADO_VIDA        NUMERIC,
                VALOR_POR_KM_VIDA     TEXT,
                KM_RODADO_TODAS_VIDAS NUMERIC
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                           AS UNIDADE_ALOCADO,
       P.CODIGO                                                                         AS COD_PNEU,
       P.CODIGO_CLIENTE                                                                 AS COD_CLIENTE_PNEU,
       F_IF(P.VIDA_ATUAL = 1, MARCA_PNEU.NOME, MARCA_BANDA.NOME)                        AS MARCA,
       F_IF(P.VIDA_ATUAL = 1, MODELO_PNEU.NOME, MODELO_BANDA.NOME)                      AS MODELO,
       FUNC_PNEU_FORMAT_DIMENSAO(DP.LARGURA, DP.LARGURA, DP.ARO)                        AS DIMENSAO,
       -- A pvv só tem acima da primeira vida.
       -- Caso o pneu esteja na primeira vida o valor será pego da própria tabela pneu.
       COALESCE(PVV.VIDA, P.VIDA_ATUAL)                                                 AS VIDA_PNEU,
       F_IF(P.VIDA_ATUAL = 1, ROUND(P.VALOR::NUMERIC, 2), ROUND(PVV.VALOR::NUMERIC, 2)) AS VALOR_VIDA,
       COALESCE(VP.KM_RODADO_VIDA, 0)                                                   AS KM_RODADO_VIDA,
       -- O nullif() nesse case serve para impedir erro de divisão por zero.
       COALESCE(
               ROUND((CASE
                          WHEN VP.VIDA_PNEU = 1
                              THEN P.VALOR / NULLIF(VP.KM_RODADO_VIDA, 0)
                          ELSE
                              COALESCE(PVV.VALOR, 0) / NULLIF(VP.KM_RODADO_VIDA, 0)
                   END)::NUMERIC, 3)::TEXT, '-')                                        AS VALOR_POR_KM_VIDA,
       COALESCE(VP.TOTAL_KM_RODADO_TODAS_VIDAS, 0)                                      AS KM_RODADO_TODAS_VIDAS
FROM PNEU P
         JOIN MODELO_PNEU
              ON MODELO_PNEU.CODIGO = P.COD_MODELO
         JOIN MARCA_PNEU
              ON MARCA_PNEU.CODIGO = MODELO_PNEU.COD_MARCA
         JOIN DIMENSAO_PNEU DP
              ON DP.CODIGO = P.COD_DIMENSAO
         JOIN UNIDADE U
              ON U.CODIGO = P.COD_UNIDADE
         LEFT JOIN PNEU_VALOR_VIDA PVV
                   ON PVV.COD_PNEU = P.CODIGO
         LEFT JOIN MODELO_BANDA
                   ON MODELO_BANDA.CODIGO = PVV.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA
                   ON MARCA_BANDA.CODIGO = MODELO_BANDA.COD_MARCA
         LEFT JOIN VIEW_PNEU_KM_RODADO_TOTAL VP
                   ON VP.COD_PNEU = P.CODIGO AND VP.VIDA_PNEU = PVV.VIDA
WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
ORDER BY U.CODIGO, P.CODIGO_CLIENTE, P.VIDA_ATUAL;
$$;

drop function func_pneu_relatorio_km_rodado_por_vida(f_cod_unidades bigint[]);
-- O nome foi alterado e a busca agora é feita de outra function.
-- Além disso, foram adicionadas novas colunas ao relatório, sendo:
-- MARCA, MODELO, DIMENSÃO, VALOR VIDA e CPK VIDA.
create or replace function func_pneu_relatorio_km_rodado_por_vida_linhas(f_cod_unidades bigint[])
    returns table
            (
                "UNIDADE ALOCADO"          text,
                "PNEU"                     text,
                "MARCA"                    text,
                "MODELO"                   text,
                "DIMENSÃO"                 text,
                "VIDA"                     integer,
                "VALOR VIDA"               numeric,
                "KM RODADO VIDA"           numeric,
                "CPK VIDA"                 text,
                "KM RODADO TODAS AS VIDAS" numeric
            )
    language sql
as
$$
select f.unidade_alocado       as unidade_alocado,
       f.cod_cliente_pneu      as cod_cliente_pneu,
       f.marca                 as marca,
       f.modelo                as modelo,
       f.dimensao              as dimensao,
       f.vida_pneu             as vida,
       f.valor_vida            as valor_vida,
       f.km_rodado_vida        as km_rodado_vida,
       f.valor_por_km_vida     as valor_por_km_vida,
       f.km_rodado_todas_vidas as km_rodado_todas_vidas
from func_pneu_relatorio_km_rodado_por_vida_base(f_cod_unidades) f;
$$;