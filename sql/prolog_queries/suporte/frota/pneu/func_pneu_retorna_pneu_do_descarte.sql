-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Se o pneu veio do ESTOQUE:
-- Pode deletar a movimentação e mover pneu para ESTOQUE.
--
-- Se o pneu veio da ANÁLISE:
-- Pode deletar a movimentação e mover pneu para ANÁLISE.
-- Não podemos mover diretamente para ESTOQUE pois uma mov de ANÁLISE -> ESTOQUE implica informações de incremento de
-- vida ou de outros serviços simples, que nós não temos.
--
-- Se o pneu veio do VEÍCULO:
-- Não podemos deletar a movimentação, pois não podemos voltar o pneu para o veículo, já que pode ter outro pneu lá.
-- Teremos que alterar essa movimentação para ser VEÍCULO -> ESTOQUE ao invés de VEÍCULO -> DESCARTE. As informações
-- específicas de uma mov VEÍCULO -> DESCARTE (fotos e motivo) devem ser removidas, mas não a mov como um todo.
--
-- Lembrando sempre que nos casos de deleção de movs, temos que deletar o processo, se necessário.
--
-- Précondições:
-- 1) Para a function funcionar, a última movimentação do pneu deve ser para descarte.
-- 2) Assumimos que não exista mais de uma movimentação para descarte, o que seria uma inconsistência e não é nem
-- verificado e nem tratado por essa function.
--
-- Histórico:
-- 2019-07-09 -> Function criada (thaisksf - PL-2090).
-- 2019-09-18 -> Adiciona ao schema suporte (natanrotta - PL-2242).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_RETORNA_PNEU_DO_DESCARTE(F_COD_EMPRESA BIGINT,
                                                                      F_COD_UNIDADE BIGINT,
                                                                      F_COD_PNEU BIGINT,
                                                                      F_NUMERO_FOGO_PNEU VARCHAR,
                                                                      OUT AVISO_PNEU_RETORNADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_STATUS_PNEU_ESTOQUE       VARCHAR := 'ESTOQUE';
    F_STATUS_PNEU_DESCARTE      VARCHAR := 'DESCARTE';
    F_STATUS_PNEU_EM_USO        VARCHAR := 'EM_USO';
    F_STATUS_ORIGEM_PNEU        VARCHAR;
    F_STATUS_DESTINO_PNEU       VARCHAR;
    F_COD_MOVIMENTACAO_PROCESSO BIGINT;
    F_COD_MOVIMENTACAO          BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- VERIFICA SE UNIDADE EXISTE, SE EMPRESA EXISTE E SE UNIDADE PERTENCE A EMPRESA.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

    -- VERIFICA SE PNEU EXISTE NA UNIDADE.
    PERFORM FUNC_GARANTE_PNEU_EXISTE(F_COD_EMPRESA, F_COD_UNIDADE, F_COD_PNEU, F_NUMERO_FOGO_PNEU);

    -- VERIFICA SE O STATUS DO PNEU ESTÁ COMO 'DESCARTE'.
    IF (SELECT P.STATUS
        FROM PNEU P
        WHERE P.CODIGO_CLIENTE = F_NUMERO_FOGO_PNEU
          AND P.CODIGO = F_COD_PNEU
          AND P.COD_UNIDADE = F_COD_UNIDADE) != F_STATUS_PNEU_DESCARTE
    THEN
        RAISE EXCEPTION 'Pneu de número de fogo %, com código % da unidade %, não está com status = %!',
            F_NUMERO_FOGO_PNEU, F_COD_PNEU, F_COD_UNIDADE, F_STATUS_PNEU_DESCARTE;
    END IF;

    -- VAI PEGAR AS INFORMAÇÕES DA ÚLTIMA MOVIMENTAÇÃO DO PNEU ASSUMINDO QUE FOI PARA DESCARTE.
    -- NA SEQUÊNCIA ISSO SERÁ VALIDADO.
    SELECT M.CODIGO                    AS COD_MOVIMENTACAO,
           M.COD_MOVIMENTACAO_PROCESSO AS COD_MOVIMENTACAO_PROCESSO,
           MO.TIPO_ORIGEM              AS TIPO_ORIGEM,
           MD.TIPO_DESTINO             AS TIPO_DESTINO
    FROM MOVIMENTACAO M
             JOIN MOVIMENTACAO_DESTINO MD ON M.CODIGO = MD.COD_MOVIMENTACAO
             JOIN MOVIMENTACAO_ORIGEM MO ON M.CODIGO = MO.COD_MOVIMENTACAO
    WHERE M.COD_PNEU = F_COD_PNEU
      AND M.COD_UNIDADE = F_COD_UNIDADE
    ORDER BY M.CODIGO DESC
    LIMIT 1
    INTO F_COD_MOVIMENTACAO, F_COD_MOVIMENTACAO_PROCESSO, F_STATUS_ORIGEM_PNEU, F_STATUS_DESTINO_PNEU;

    -- GARANTE QUE A ÚLTIMA MOVIMENTAÇÃO DO PNEU TENHA SIDO PARA DESCARTE.
    IF F_STATUS_DESTINO_PNEU != F_STATUS_PNEU_DESCARTE
    THEN
        RAISE EXCEPTION '[INCONSISTÊNCIA] A ultima movimentação do pneu de número de fogo %, com código % da unidade %,
      não foi para %!',
            F_NUMERO_FOGO_PNEU, F_COD_PNEU, F_COD_UNIDADE, F_STATUS_PNEU_DESCARTE;
    END IF;

    -- DELETA A MOVIMENTAÇÃO QUE MOVEU O PNEU PARA O DESCARTE SE A ORIGEM NÃO FOR 'EM_USO', CASO SEJA, MODIFICA
    -- DESTINO PARA 'ESTOQUE'.
    -- ISSO É FEITO PORQUE UM PNEU QUE ESTAVA APLICADO, NÃO PODE VOLTAR AO VEÍCULO, JÁ QUE PODERÍAMOS ESBARRAR NO CASO
    -- ONDE JÁ EXISTE OUTRO PNEU NA POSIÇÃO QUE ELE ESTAVA ANTES.
    IF F_STATUS_ORIGEM_PNEU != F_STATUS_PNEU_EM_USO
    THEN
        DELETE
        FROM MOVIMENTACAO M
        WHERE M.CODIGO = F_COD_MOVIMENTACAO
          AND M.COD_MOVIMENTACAO_PROCESSO = F_COD_MOVIMENTACAO_PROCESSO
          AND M.COD_PNEU = F_COD_PNEU
          AND M.COD_UNIDADE = F_COD_UNIDADE;
        -- VERIFICA SE MOVIMENTACAO ERA A UNICA EXISTENTE NO PROCESSO.
        IF NOT EXISTS(
                SELECT M.CODIGO FROM MOVIMENTACAO M WHERE M.COD_MOVIMENTACAO_PROCESSO = F_COD_MOVIMENTACAO_PROCESSO)
        THEN
            -- DELETA PROCESSO DE MOVIMENTACAO.
            DELETE FROM MOVIMENTACAO_PROCESSO WHERE CODIGO = F_COD_MOVIMENTACAO_PROCESSO;
        END IF;
    ELSE
        UPDATE MOVIMENTACAO_DESTINO
        SET TIPO_DESTINO          = F_STATUS_PNEU_ESTOQUE,
            COD_MOTIVO_DESCARTE   = NULL,
            URL_IMAGEM_DESCARTE_1 = NULL,
            URL_IMAGEM_DESCARTE_2 = NULL,
            URL_IMAGEM_DESCARTE_3 = NULL
        WHERE COD_MOVIMENTACAO = F_COD_MOVIMENTACAO;
    END IF;

    -- ALTERA STATUS DO PNEU.
    IF F_STATUS_ORIGEM_PNEU != F_STATUS_PNEU_EM_USO
    THEN
        UPDATE PNEU
        SET STATUS = F_STATUS_ORIGEM_PNEU
        WHERE CODIGO = F_COD_PNEU
          AND CODIGO_CLIENTE = F_NUMERO_FOGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND COD_EMPRESA = F_COD_EMPRESA;
    ELSE
        UPDATE PNEU
        SET STATUS = F_STATUS_PNEU_ESTOQUE
        WHERE CODIGO = F_COD_PNEU
          AND CODIGO_CLIENTE = F_NUMERO_FOGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND COD_EMPRESA = F_COD_EMPRESA;
    END IF;

    SELECT CONCAT('Pneu retornado para ', (SELECT P.STATUS FROM PNEU P WHERE P.CODIGO = F_COD_PNEU),
                  ', Código: ', F_COD_PNEU,
                  ', Número de fogo: ', F_NUMERO_FOGO_PNEU,
                  ', Unidade: ', F_COD_UNIDADE, ' - ', (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE),
                  ', Empresa: ', F_COD_EMPRESA, ' - ', (SELECT E.NOME FROM EMPRESA E WHERE E.CODIGO = F_COD_EMPRESA))
    INTO AVISO_PNEU_RETORNADO;
END;
$$;