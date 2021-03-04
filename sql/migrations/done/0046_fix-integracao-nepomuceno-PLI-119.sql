create or replace function integracao.func_pneu_afericao_get_infos_configuracao_afericao(f_cod_unidades bigint[])
    returns table
            (
                cod_auxiliar              text,
                cod_unidade               bigint,
                cod_tipo_veiculo          bigint,
                pode_aferir_sulco         boolean,
                pode_aferir_pressao       boolean,
                pode_aferir_sulco_pressao boolean,
                pode_aferir_estepe        boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa bigint := (select u.cod_empresa
                             from public.unidade u
                             where u.codigo = any (f_cod_unidades)
                             limit 1);
begin
    return query
        with cod_auxiliares as (
            select vt.codigo                                   as cod_tipo_veiculo,
                   regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
            from veiculo_tipo vt
            where vt.cod_empresa = v_cod_empresa
        )

        select ca.cod_auxiliar                 as cod_auxiliar,
               actav.cod_unidade               as cod_unidade,
               actav.cod_tipo_veiculo          as cod_tipo_veiculo,
               actav.pode_aferir_sulco         as pode_aferir_sulco,
               actav.pode_aferir_pressao       as pode_aferir_pressao,
               actav.pode_aferir_sulco_pressao as pode_aferir_sulco_pressao,
               actav.pode_aferir_estepe        as pode_aferir_estepe
        from afericao_configuracao_tipo_afericao_veiculo actav
                 join cod_auxiliares ca on actav.cod_tipo_veiculo = ca.cod_tipo_veiculo
        where actav.cod_unidade = any (f_cod_unidades)
          and ca.cod_auxiliar is not null;
end;
$$;


DROP FUNCTION FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
    F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                PLACA                     TEXT,
                COD_UNIDADE_PLACA         BIGINT,
                NOME_MODELO               TEXT,
                INTERVALO_PRESSAO         INTEGER,
                INTERVALO_SULCO           INTEGER,
                PERIODO_AFERICAO_SULCO    INTEGER,
                PERIODO_AFERICAO_PRESSAO  INTEGER,
                PNEUS_APLICADOS           INTEGER,
                STATUS_ATIVO_TIPO_VEICULO BOOLEAN,
                PODE_AFERIR_SULCO         BOOLEAN,
                PODE_AFERIR_PRESSAO       BOOLEAN,
                PODE_AFERIR_SULCO_PRESSAO BOOLEAN,
                PODE_AFERIR_ESTEPE        BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT V.PLACA :: TEXT                                                     AS PLACA,
               V.COD_UNIDADE :: BIGINT                                             AS COD_UNIDADE_PLACA,
               MV.NOME :: TEXT                                                     AS NOME_MODELO,
               COALESCE(INTERVALO_PRESSAO.INTERVALO, -1) :: INTEGER                AS INTERVALO_PRESSAO,
               COALESCE(INTERVALO_SULCO.INTERVALO, -1) :: INTEGER                  AS INTERVALO_SULCO,
               PRU.PERIODO_AFERICAO_SULCO                                          AS PERIODO_AFERICAO_SULCO,
               PRU.PERIODO_AFERICAO_PRESSAO                                        AS PERIODO_AFERICAO_PRESSAO,
               COALESCE(NUMERO_PNEUS.TOTAL, 0) :: INTEGER                          AS PNEUS_APLICADOS,
               VT.STATUS_ATIVO                                                     AS STATUS_ATIVO_TIPO_VEICULO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_SULCO)         AS PODE_AFERIR_SULCO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_PRESSAO)       AS PODE_AFERIR_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_SULCO_PRESSAO) AS PODE_AFERIR_SULCO_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_ESTEPE)        AS PODE_AFERIR_ESTEPE
        FROM VEICULO V
                 JOIN PNEU_RESTRICAO_UNIDADE PRU ON PRU.COD_UNIDADE = V.COD_UNIDADE
                 JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
                 JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO
                 LEFT JOIN AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO CONFIG
                           ON CONFIG.COD_TIPO_VEICULO = VT.CODIGO
                               AND CONFIG.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN (SELECT A.PLACA_VEICULO                                                            AS PLACA_INTERVALO,
                                   EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL) -
                                                     MAX(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))) AS INTERVALO
                            FROM AFERICAO A
                            WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                               OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                            GROUP BY A.PLACA_VEICULO) AS INTERVALO_PRESSAO
                           ON INTERVALO_PRESSAO.PLACA_INTERVALO = V.PLACA
                 LEFT JOIN (SELECT A.PLACA_VEICULO                                                            AS PLACA_INTERVALO,
                                   EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL) -
                                                     MAX(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))) AS INTERVALO
                            FROM AFERICAO A
                            WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                               OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                            GROUP BY A.PLACA_VEICULO) AS INTERVALO_SULCO
                           ON INTERVALO_SULCO.PLACA_INTERVALO = V.PLACA
                 LEFT JOIN (SELECT VP.PLACA           AS PLACA_PNEUS,
                                   COUNT(VP.COD_PNEU) AS TOTAL
                            FROM VEICULO_PNEU VP
                            WHERE VP.COD_UNIDADE = ANY (F_COD_UNIDADES)
                            GROUP BY VP.PLACA) AS NUMERO_PNEUS ON PLACA_PNEUS = V.PLACA
        WHERE V.STATUS_ATIVO = TRUE
          AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
        ORDER BY MV.NOME, INTERVALO_PRESSAO DESC, INTERVALO_SULCO DESC, PNEUS_APLICADOS DESC;
END;
$$;