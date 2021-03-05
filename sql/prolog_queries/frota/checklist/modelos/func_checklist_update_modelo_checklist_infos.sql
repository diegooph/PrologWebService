CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_UPDATE_MODELO_CHECKLIST_INFOS(F_COD_UNIDADE BIGINT,
                                                                        F_COD_MODELO BIGINT,
                                                                        F_NOME_MODELO TEXT,
                                                                        F_COD_CARGOS BIGINT[],
                                                                        F_COD_TIPOS_VEICULOS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    QTD_TIPOS_VEICULO_PARA_DELETAR BIGINT := (SELECT COUNT(*)
                                              FROM CHECKLIST_MODELO_VEICULO_TIPO
                                              WHERE COD_UNIDADE = F_COD_UNIDADE
                                                AND COD_MODELO = F_COD_MODELO);
    QTD_CARGOS_PARA_DELETAR        BIGINT := (SELECT COUNT(*)
                                              FROM CHECKLIST_MODELO_FUNCAO
                                              WHERE COD_UNIDADE = F_COD_UNIDADE
                                                AND COD_CHECKLIST_MODELO = F_COD_MODELO);
    QTD_LINHAS_IMPACTADAS          BIGINT;
BEGIN
    -- 1 -> Atualiza o modelo.
    UPDATE CHECKLIST_MODELO
    SET NOME = F_NOME_MODELO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO = F_COD_MODELO;

    GET DIAGNOSTICS QTD_LINHAS_IMPACTADAS = ROW_COUNT;

    IF QTD_LINHAS_IMPACTADAS IS NULL OR QTD_LINHAS_IMPACTADAS <> 1
    THEN
        RAISE EXCEPTION 'Erro ao atualizar o nome do modelo de checklist';
    END IF;
    --

    -- 2 -> Atualiza os tipos de veículos.
    DELETE FROM CHECKLIST_MODELO_VEICULO_TIPO WHERE COD_UNIDADE = F_COD_UNIDADE AND COD_MODELO = F_COD_MODELO;

    GET DIAGNOSTICS QTD_LINHAS_IMPACTADAS = ROW_COUNT;

    IF QTD_TIPOS_VEICULO_PARA_DELETAR = QTD_LINHAS_IMPACTADAS
    THEN
        INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO(COD_UNIDADE, COD_MODELO, COD_TIPO_VEICULO)
        VALUES (F_COD_UNIDADE, F_COD_MODELO, UNNEST(F_COD_TIPOS_VEICULOS));

        GET DIAGNOSTICS QTD_LINHAS_IMPACTADAS = ROW_COUNT;

        IF QTD_LINHAS_IMPACTADAS IS NULL OR QTD_LINHAS_IMPACTADAS <> ARRAY_LENGTH(F_COD_TIPOS_VEICULOS, 1)
        THEN
            RAISE EXCEPTION 'Erro ao inserir tipos de veículo liberados no modelo de checklist %', F_COD_MODELO;
        END IF;
    ELSE
        RAISE EXCEPTION 'Não foi possível limpar as entradas da tabela CHECKLIST_MODELO_VEICULO_TIPO';
    END IF;
    --

    -- 3 -> Atualiza os cargos.
    DELETE FROM CHECKLIST_MODELO_FUNCAO WHERE COD_UNIDADE = F_COD_UNIDADE AND COD_CHECKLIST_MODELO = F_COD_MODELO;

    GET DIAGNOSTICS QTD_LINHAS_IMPACTADAS = ROW_COUNT;

    IF QTD_CARGOS_PARA_DELETAR = QTD_LINHAS_IMPACTADAS
    THEN
        INSERT INTO CHECKLIST_MODELO_FUNCAO(COD_UNIDADE, COD_CHECKLIST_MODELO, COD_FUNCAO)
        VALUES (F_COD_UNIDADE, F_COD_MODELO, UNNEST(F_COD_CARGOS));

        GET DIAGNOSTICS QTD_LINHAS_IMPACTADAS = ROW_COUNT;

        IF QTD_LINHAS_IMPACTADAS IS NULL OR QTD_LINHAS_IMPACTADAS <> ARRAY_LENGTH(F_COD_CARGOS, 1)
        THEN
            RAISE EXCEPTION 'Erro ao inserir cargos liberados no modelo de checklist %', F_COD_MODELO;
        END IF;
    ELSE
        RAISE EXCEPTION 'Não foi possível limpar as entradas da tabela CHECKLIST_MODELO_FUNCAO';
    END IF;
    --
END;
$$;