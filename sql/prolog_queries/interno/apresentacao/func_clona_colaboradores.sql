CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_COLABORADORES(F_COD_EMPRESA_BASE BIGINT,
                                                            F_COD_UNIDADE_BASE BIGINT,
                                                            F_COD_EMPRESA_USUARIO BIGINT,
                                                            F_COD_UNIDADE_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_CPF_PREFIXO_PADRAO          TEXT   := '0338328';
    V_CPF_SUFIXO_PADRAO           BIGINT := 0;
    V_CPF_VERIFICACAO             BIGINT;
    V_CPFS_VALIDOS_CADASTRO       BIGINT[];
    V_TENTATIVA_BUSCAR_CPF_VALIDO BIGINT := 0;
BEGIN
    -- VERIFICA SE EXISTEM EQUIPES DE VEÍCULOS PARA COPIAR
    IF NOT EXISTS(SELECT E.CODIGO FROM EQUIPE E WHERE E.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem equipes para serem copiadas da unidade de código: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- VERIFICA SE EXISTEM SETORES PARA COPIAR
    IF NOT EXISTS(SELECT SE.CODIGO FROM SETOR SE WHERE SE.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem setores para serem copiados da unidade de código: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- VERIFICA SE EXISTEM CARGOS PARA COPIAR
    IF NOT EXISTS(SELECT F.CODIGO FROM FUNCAO F WHERE F.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem cargos para serem copiados da empresa de código: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM COLABORADORES PARA COPIAR
    IF NOT EXISTS(SELECT CD.CODIGO FROM COLABORADOR_DATA CD WHERE CD.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem colaboradores para serem copiados da unidade de código: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- COPIA AS EQUIPES
    INSERT INTO EQUIPE (NOME,
                        COD_UNIDADE)
    SELECT E.NOME,
           F_COD_UNIDADE_USUARIO
    FROM EQUIPE E
    WHERE E.COD_UNIDADE = F_COD_UNIDADE_BASE;

    -- COPIA OS SETORES
    INSERT INTO SETOR(NOME,
                      COD_UNIDADE)
    SELECT SE.NOME,
           F_COD_UNIDADE_USUARIO
    FROM SETOR SE
    WHERE SE.COD_UNIDADE = F_COD_UNIDADE_BASE;

    -- COPIA AS FUNÇÕES
    INSERT INTO FUNCAO_DATA (NOME,
                             COD_EMPRESA)
    SELECT F.NOME,
           F_COD_EMPRESA_USUARIO
    FROM FUNCAO F
    WHERE F.COD_EMPRESA = F_COD_EMPRESA_BASE
    ON CONFLICT DO NOTHING;

    --SELECIONA CPFS VÁLIDOS PARA CADASTRO.
    WHILE (((ARRAY_LENGTH(V_CPFS_VALIDOS_CADASTRO, 1)) < (SELECT COUNT(CD.CPF)
                                                          FROM COLABORADOR_DATA CD
                                                          WHERE CD.COD_UNIDADE = F_COD_UNIDADE_BASE)) OR
           ((ARRAY_LENGTH(V_CPFS_VALIDOS_CADASTRO, 1)) IS NULL))
        LOOP
            --EXISTEM 10000 CPFS DISPONÍVEIS PARA CADASTRO (03383280000 ATÉ 03383289999),
            --CASO EXCEDA O NÚMERO DE TENTATIVAS - UM ERRO É MOSTRADO.
            IF (V_TENTATIVA_BUSCAR_CPF_VALIDO = 10000)
            THEN
                RAISE EXCEPTION
                    'Não existem cpfs disponíveis para serem cadastrados';
            END IF;
            V_CPF_VERIFICACAO := (CONCAT(V_CPF_PREFIXO_PADRAO, LPAD(V_CPF_SUFIXO_PADRAO::TEXT, 4, '0')))::BIGINT;
            IF NOT EXISTS(SELECT CD.CPF FROM COLABORADOR_DATA CD WHERE CD.CPF = V_CPF_VERIFICACAO)
            THEN
                -- CPFS VÁLIDOS PARA CADASTRO
                V_CPFS_VALIDOS_CADASTRO := ARRAY_APPEND(V_CPFS_VALIDOS_CADASTRO, V_CPF_VERIFICACAO);
            END IF;
            V_CPF_SUFIXO_PADRAO := V_CPF_SUFIXO_PADRAO + 1;
            V_TENTATIVA_BUSCAR_CPF_VALIDO := V_TENTATIVA_BUSCAR_CPF_VALIDO + 1;
        END LOOP;

    WITH CPFS_VALIDOS_CADASTRO AS (
        SELECT ROW_NUMBER() OVER () AS CODIGO,
               CDN                  AS CPF_NOVO_CADASTRO
        FROM UNNEST(V_CPFS_VALIDOS_CADASTRO) CDN),
         COLABORADORES_BASE AS (
             SELECT ROW_NUMBER() OVER () AS CODIGO,
                    CO.CPF               AS CPF_BASE,
                    CO.NOME              AS NOME_BASE,
                    CO.DATA_NASCIMENTO   AS DATA_NASCIMENTO_BASE,
                    CO.DATA_ADMISSAO     AS DATA_ADMISSAO_BASE,
                    CO.COD_EQUIPE        AS COD_EQUIPE_BASE,
                    CO.COD_SETOR         AS COD_SETOR_BASE,
                    CO.COD_FUNCAO        AS COD_FUNCAO_BASE,

                    CO.COD_PERMISSAO     AS COD_PERMISSAO_BASE
             FROM COLABORADOR CO
             WHERE COD_UNIDADE = F_COD_UNIDADE_BASE
         ),
         DADOS_DE_PARA AS (
             SELECT CVC.CPF_NOVO_CADASTRO      AS CPF_CADASTRO,
                    CB.CPF_BASE           AS CPF_BASE,
                    CB.NOME_BASE          AS NOME_BASE,
                    CB.DATA_ADMISSAO_BASE AS DATA_NASCIMENTO_BASE,
                    CB.DATA_ADMISSAO_BASE AS DATA_ADMISSAO_BASE,
                    CB.COD_PERMISSAO_BASE AS COD_PERMISSAO_BASE,
                    EB.CODIGO             AS COD_EQUIPE_BASE,
                    EN.CODIGO             AS COD_EQUIPE_NOVA,
                    SB.CODIGO             AS COD_SETOR_BASE,
                    SN.CODIGO             AS COD_SETOR_NOVO,
                    FB.CODIGO             AS COD_FUNCAO_BASE,
                    FN.CODIGO             AS COD_FUNCAO_NOVO
             FROM COLABORADORES_BASE CB
                      JOIN EQUIPE EB ON EB.CODIGO = CB.COD_EQUIPE_BASE
                      JOIN EQUIPE EN ON EB.NOME = EN.NOME
                      JOIN SETOR SB ON CB.COD_SETOR_BASE = SB.CODIGO
                      JOIN SETOR SN ON SB.NOME = SN.NOME
                      JOIN FUNCAO FB ON CB.COD_FUNCAO_BASE = FB.CODIGO
                      JOIN FUNCAO FN ON FB.NOME = FN.NOME
                      JOIN CPFS_VALIDOS_CADASTRO CVC ON CVC.CODIGO = CB.CODIGO
             WHERE EB.COD_UNIDADE = F_COD_UNIDADE_BASE
               AND EN.COD_UNIDADE = F_COD_UNIDADE_USUARIO
               AND SB.COD_UNIDADE = F_COD_UNIDADE_BASE
               AND SN.COD_UNIDADE = F_COD_UNIDADE_USUARIO
               AND FB.COD_EMPRESA = F_COD_EMPRESA_BASE
               AND FN.COD_EMPRESA = F_COD_EMPRESA_USUARIO)

         -- INSERE AS PLACAS DE->PARA.
    INSERT
    INTO COLABORADOR_DATA(CPF,
                          DATA_NASCIMENTO,
                          DATA_ADMISSAO,
                          STATUS_ATIVO,
                          NOME,
                          COD_EQUIPE,
                          COD_FUNCAO,
                          COD_UNIDADE,
                          COD_PERMISSAO,
                          COD_EMPRESA,
                          COD_SETOR,
                          COD_UNIDADE_CADASTRO,
                          DELETADO)
    SELECT DDP.CPF_CADASTRO,
           DDP.DATA_NASCIMENTO_BASE,
           DDP.DATA_ADMISSAO_BASE,
           TRUE,
           DDP.NOME_BASE,
           DDP.COD_EQUIPE_NOVA,
           DDP.COD_FUNCAO_NOVO,
           F_COD_UNIDADE_USUARIO,
           DDP.COD_PERMISSAO_BASE,
           F_COD_EMPRESA_USUARIO,
           DDP.COD_SETOR_NOVO,
           F_COD_UNIDADE_USUARIO,
           FALSE
    FROM DADOS_DE_PARA DDP;
END;
$$;