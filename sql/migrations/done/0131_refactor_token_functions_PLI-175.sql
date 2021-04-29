create or replace function integracao.func_garante_token_empresa(f_cod_empresa bigint,
                                                                 f_token_integracao text,
                                                                 f_error_message text default null)
    returns void
    language plpgsql
as
$$
declare
    error_message text :=
        f_if(f_error_message is null,
             format('Token não autorizado para a empresa %s', f_cod_empresa),
             f_error_message);
begin
    if (f_cod_empresa not in (select ti.cod_empresa
                              from integracao.token_integracao ti
                              where ti.token_integracao = f_token_integracao))
    then
        perform throw_generic_error(error_message);
    end if;
end;
$$;



create or replace function integracao.func_geral_busca_unidades_bloqueadas_by_token_integracao(f_token_integracao text,
                                                                                               f_sistema_key text,
                                                                                               f_recurso_integrado text)
    returns table
            (
                cod_unidade_bloqueada bigint
            )
    language sql
as
$$
select euib.cod_unidade_bloqueada
from integracao.empresa_unidades_integracao_bloqueada euib
where euib.cod_empresa in (select cod_empresa
                           from integracao.token_integracao ti
                           where ti.token_integracao = f_token_integracao)
  and euib.chave_sistema = f_sistema_key
  and euib.recuro_integrado = f_recurso_integrado
$$;



CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_VEICULO_INSERE_VEICULO_PROLOG(F_COD_UNIDADE_VEICULO_ALOCADO BIGINT,
                                                  F_PLACA_VEICULO_CADASTRADO TEXT,
                                                  F_KM_ATUAL_VEICULO_CADASTRADO BIGINT,
                                                  F_COD_MODELO_VEICULO_CADASTRADO BIGINT,
                                                  F_COD_TIPO_VEICULO_CADASTRADO BIGINT,
                                                  F_DATA_HORA_VEICULO_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                  F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO       CONSTANT BIGINT  := (SELECT U.COD_EMPRESA
                                                   FROM PUBLIC.UNIDADE U
                                                   WHERE U.CODIGO = F_COD_UNIDADE_VEICULO_ALOCADO);
    DEVE_SOBRESCREVER_VEICULO CONSTANT BOOLEAN := (SELECT *
                                                   FROM INTEGRACAO.FUNC_EMPRESA_GET_CONFIG_SOBRESCREVE_VEICULOS(
                                                           COD_EMPRESA_VEICULO));
    VEICULO_ESTA_NO_PROLOG    CONSTANT BOOLEAN := (SELECT EXISTS(SELECT V.CODIGO
                                                                 FROM PUBLIC.VEICULO_DATA V
                                                                 WHERE V.PLACA::TEXT = F_PLACA_VEICULO_CADASTRADO));
    STATUS_ATIVO_VEICULO      CONSTANT BOOLEAN := TRUE;
    COD_DIAGRAMA_VEICULO               BIGINT;
    COD_VEICULO_PROLOG                 BIGINT;
    F_QTD_ROWS_ALTERADAS               BIGINT;
BEGIN
    -- Validamos se a Unidade pertence a mesma empresa do token.
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(
            COD_EMPRESA_VEICULO,
            F_TOKEN_INTEGRACAO,
            FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a inserir dados da unidade "%s",
                     confira se está usando o token correto', F_TOKEN_INTEGRACAO, F_COD_UNIDADE_VEICULO_ALOCADO));

    -- Validamos se o KM foi inputado corretamente.
    IF (F_KM_ATUAL_VEICULO_CADASTRADO < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE DADOS] A quilometragem do veículo não pode ser um número negativo');
    END IF;

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = COD_EMPRESA_VEICULO
                            AND MV.CODIGO = F_COD_MODELO_VEICULO_CADASTRADO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
                            AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    -- Validamos se a placa já existe no ProLog.
    IF (VEICULO_ESTA_NO_PROLOG AND NOT DEVE_SOBRESCREVER_VEICULO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('[ERRO DE DADOS] A placa "%s" já está cadastrada no Sistema ProLog',
                       F_PLACA_VEICULO_CADASTRADO));
    END IF;

    -- Busca o código do diagrama de acordo com o tipo de veículo
    COD_DIAGRAMA_VEICULO := (SELECT VT.COD_DIAGRAMA
                             FROM PUBLIC.VEICULO_TIPO VT
                             WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
                               AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO);

    IF (VEICULO_ESTA_NO_PROLOG AND DEVE_SOBRESCREVER_VEICULO)
    THEN
        -- Buscamos o código do veículo que será sobrescrito.
        SELECT V.CODIGO
        FROM VEICULO V
        WHERE V.PLACA = F_PLACA_VEICULO_CADASTRADO
          AND V.COD_EMPRESA = COD_EMPRESA_VEICULO
        INTO COD_VEICULO_PROLOG;

        -- Removemos os pneus aplicados na placa, para que ela possa receber novos pneus.
        PERFORM INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO_BY_PLACA(F_PLACA_VEICULO_CADASTRADO);

        -- Sebrescrevemos os dados do veículo.
        PERFORM INTEGRACAO.FUNC_VEICULO_SOBRESCREVE_VEICULO_CADASTRADO(
                F_PLACA_VEICULO_CADASTRADO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_KM_ATUAL_VEICULO_CADASTRADO,
                F_COD_TIPO_VEICULO_CADASTRADO,
                COD_DIAGRAMA_VEICULO,
                F_COD_MODELO_VEICULO_CADASTRADO);

    ELSE
        -- Aqui devemos apenas inserir o veículo no ProLog.
        INSERT INTO PUBLIC.VEICULO(COD_EMPRESA,
                                   COD_UNIDADE,
                                   PLACA,
                                   KM,
                                   STATUS_ATIVO,
                                   COD_TIPO,
                                   COD_DIAGRAMA,
                                   COD_MODELO,
                                   COD_UNIDADE_CADASTRO)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_PLACA_VEICULO_CADASTRADO,
                F_KM_ATUAL_VEICULO_CADASTRADO,
                STATUS_ATIVO_VEICULO,
                F_COD_TIPO_VEICULO_CADASTRADO,
                COD_DIAGRAMA_VEICULO,
                F_COD_MODELO_VEICULO_CADASTRADO,
                F_COD_UNIDADE_VEICULO_ALOCADO)
        RETURNING CODIGO INTO COD_VEICULO_PROLOG;
    END IF;

    IF (DEVE_SOBRESCREVER_VEICULO)
    THEN
        -- Se permite sobrescrita de dados, então tentamos inserir, caso a constraint estourar,
        -- apenas atualizamos os dados. Tentamos inserir antes, pois, em cenários onde o veículo já encontra-se no
        -- ProLog, não temos nenhuma entrada para ele na tabela de mapeamento.
        INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO,
                                                  COD_UNIDADE_CADASTRO,
                                                  COD_VEICULO_CADASTRO_PROLOG,
                                                  PLACA_VEICULO_CADASTRO,
                                                  DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                COD_VEICULO_PROLOG,
                F_PLACA_VEICULO_CADASTRADO,
                F_DATA_HORA_VEICULO_CADASTRO)
        ON CONFLICT ON CONSTRAINT UNIQUE_PLACA_CADASTRADA_EMPRESA_INTEGRACAO
            DO UPDATE SET COD_VEICULO_CADASTRO_PROLOG = COD_VEICULO_PROLOG,
                          COD_UNIDADE_CADASTRO        = F_COD_UNIDADE_VEICULO_ALOCADO,
                          DATA_HORA_ULTIMA_EDICAO     = F_DATA_HORA_VEICULO_CADASTRO;
    ELSE
        -- Se não houve sobrescrita de dados, significa que devemos apenas inserir os dados na tabela de mapeamento.
        INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO,
                                                  COD_UNIDADE_CADASTRO,
                                                  COD_VEICULO_CADASTRO_PROLOG,
                                                  PLACA_VEICULO_CADASTRO,
                                                  DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                COD_VEICULO_PROLOG,
                F_PLACA_VEICULO_CADASTRADO,
                F_DATA_HORA_VEICULO_CADASTRO);
    END IF;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir a placa "%" na tabela de mapeamento', F_PLACA_VEICULO_CADASTRADO;
    END IF;

    RETURN COD_VEICULO_PROLOG;
END;
$$;



CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_VEICULO_ATUALIZA_VEICULO_PROLOG(F_COD_UNIDADE_ORIGINAL_ALOCADO BIGINT,
                                                    F_PLACA_ORIGINAL_VEICULO TEXT,
                                                    F_NOVO_COD_UNIDADE_ALOCADO BIGINT,
                                                    F_NOVA_PLACA_VEICULO TEXT,
                                                    F_NOVO_KM_VEICULO BIGINT,
                                                    F_NOVO_COD_MODELO_VEICULO BIGINT,
                                                    F_NOVO_COD_TIPO_VEICULO BIGINT,
                                                    F_DATA_HORA_EDICAO_VEICULO TIMESTAMP WITH TIME ZONE,
                                                    F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO     BIGINT := (SELECT U.COD_EMPRESA
                                       FROM PUBLIC.UNIDADE U
                                       WHERE U.CODIGO = F_COD_UNIDADE_ORIGINAL_ALOCADO);
    COD_TIPO_VEICULO_ANTIGO BIGINT := (SELECT VD.COD_TIPO
                                       FROM PUBLIC.VEICULO_DATA VD
                                       WHERE VD.PLACA = F_PLACA_ORIGINAL_VEICULO);
    COD_VEICULO_PROLOG      BIGINT;
    COD_DIAGRAMA_VEICULO    BIGINT;
    F_QTD_ROWS_ALTERADAS    BIGINT;
BEGIN
    -- Validamos se o usuário trocou a unidade alocada do veículo.
    IF (F_COD_UNIDADE_ORIGINAL_ALOCADO <> F_NOVO_COD_UNIDADE_ALOCADO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
    END IF;

    -- Validamos se o usuário trocou a placa do veículo.
    IF (F_PLACA_ORIGINAL_VEICULO <> F_NOVA_PLACA_VEICULO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE OPERAÇÃO] O ProLog não permite a edição da placa do veículo');
    END IF;

    -- Validamos se a Unidade do veículo trocou
    IF ((SELECT V.COD_UNIDADE
         FROM PUBLIC.VEICULO_DATA V
         WHERE V.PLACA = F_PLACA_ORIGINAL_VEICULO) <> F_COD_UNIDADE_ORIGINAL_ALOCADO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
    END IF;

    -- Validamos se a Unidade pertence a mesma empresa do token.
    IF ((SELECT U.COD_EMPRESA FROM PUBLIC.UNIDADE U WHERE U.CODIGO = F_NOVO_COD_UNIDADE_ALOCADO)
        NOT IN (SELECT TI.COD_EMPRESA
                FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT(
                        '[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s", verificar vínculos',
                        F_TOKEN_INTEGRACAO,
                        F_NOVO_COD_UNIDADE_ALOCADO));
    END IF;

    -- Validamos se a placa já existe no ProLog.
    IF (SELECT NOT EXISTS(SELECT V.CODIGO FROM PUBLIC.VEICULO_DATA V WHERE V.PLACA::TEXT = F_NOVA_PLACA_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('[ERRO DE DADOS] A placa "%s" não existe no Sistema ProLog', F_NOVA_PLACA_VEICULO));
    END IF;

    -- Validamos se o KM foi inputado corretamente.
    IF (F_NOVO_KM_VEICULO < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE DADOS] A quilometragem do veículo não pode ser um número negativo');
    END IF;

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = COD_EMPRESA_VEICULO
                            AND MV.CODIGO = F_NOVO_COD_MODELO_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vínculos');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_NOVO_COD_TIPO_VEICULO
                            AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vínculos');
    END IF;

    -- Busca o código do diagrama de acordo com o tipo de veículo
    COD_DIAGRAMA_VEICULO := (SELECT VT.COD_DIAGRAMA
                             FROM PUBLIC.VEICULO_TIPO VT
                             WHERE VT.CODIGO = F_NOVO_COD_TIPO_VEICULO
                               AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO);

    -- Validamos se o tipo foi alterado mesmo com o veículo contendo pneus aplicados.
    IF ((COD_TIPO_VEICULO_ANTIGO <> F_NOVO_COD_TIPO_VEICULO)
        AND (SELECT COUNT(VP.*) FROM PUBLIC.VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA_ORIGINAL_VEICULO) > 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE OPERAÇÃO] O tipo do veículo não pode ser alterado se a placa contém pneus aplicados');
    END IF;

    UPDATE PUBLIC.VEICULO_DATA
    SET KM           = F_NOVO_KM_VEICULO,
        COD_MODELO   = F_NOVO_COD_MODELO_VEICULO,
        COD_TIPO     = F_NOVO_COD_TIPO_VEICULO,
        COD_DIAGRAMA = COD_DIAGRAMA_VEICULO
    WHERE PLACA = F_PLACA_ORIGINAL_VEICULO
      AND COD_UNIDADE = F_COD_UNIDADE_ORIGINAL_ALOCADO
    RETURNING CODIGO INTO COD_VEICULO_PROLOG;

    UPDATE INTEGRACAO.VEICULO_CADASTRADO
    SET DATA_HORA_ULTIMA_EDICAO = F_DATA_HORA_EDICAO_VEICULO
    WHERE COD_EMPRESA_CADASTRO = COD_EMPRESA_VEICULO
      AND PLACA_VEICULO_CADASTRO = F_PLACA_ORIGINAL_VEICULO;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- VERIFICAMOS SE O UPDATE NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível atualizar a placa "%" na tabela de mapeamento', F_PLACA_ORIGINAL_VEICULO;
    END IF;

    RETURN COD_VEICULO_PROLOG;
END;
$$;



CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_VEICULO_TRANSFERE_VEICULO(F_COD_UNIDADE_ORIGEM BIGINT,
                                                                     F_COD_UNIDADE_DESTINO BIGINT,
                                                                     F_CPF_COLABORADOR_TRANSFERENCIA BIGINT,
                                                                     F_PLACA TEXT,
                                                                     F_OBSERVACAO TEXT,
                                                                     F_TOKEN_INTEGRACAO TEXT,
                                                                     F_DATA_HORA TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA                        BIGINT := (SELECT U.COD_EMPRESA
                                                    FROM PUBLIC.UNIDADE U
                                                    WHERE U.CODIGO = F_COD_UNIDADE_ORIGEM);
    V_OBSERVACAO_TRANSFERENCIA_PNEU      TEXT   := 'Transferência de pneus aplicados';
    V_COD_VEICULO                        BIGINT;
    V_COD_DIAGRAMA_VEICULO               BIGINT;
    V_COD_TIPO_VEICULO                   BIGINT;
    V_KM_VEICULO                         BIGINT;
    V_COD_COLABORADOR                    BIGINT;
    V_COD_UNIDADE_COLABORADOR            BIGINT;
    V_COD_PROCESSO_TRANSFERENCIA_PNEU    BIGINT;
    V_COD_PROCESSO_TRANSFERENCIA_VEICULO BIGINT;
    V_COD_INFORMACOES_TRANSFERENCIA      BIGINT;
    V_COD_PNEUS_TRANSFERIR               TEXT[];
BEGIN
    -- Validamos se a Unidade pertence a mesma empresa do token.
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(
            V_COD_EMPRESA,
            F_TOKEN_INTEGRACAO,
            FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s",
                    verificar vínculos', F_TOKEN_INTEGRACAO, F_COD_UNIDADE_ORIGEM));

    -- Verificamos se a empresa existe.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(V_COD_EMPRESA,
                                        FORMAT('Token utilizado não está autorizado: %s',
                                               F_TOKEN_INTEGRACAO));

    -- Verificamos se a unidade origem existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_ORIGEM,
                                        FORMAT('A unidade de origem (%s) do veículo não está mapeada',
                                               F_COD_UNIDADE_ORIGEM));

    -- Verificamos se unidade destino existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO,
                                        FORMAT('A unidade de destino (%s) do veículo não está mapeada',
                                               F_COD_UNIDADE_DESTINO));

    -- Verificamos se a unidade de origem pertence a empresa.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(V_COD_EMPRESA,
                                                F_COD_UNIDADE_ORIGEM,
                                                FORMAT('Unidade (%s) não autorizada para o token: %s',
                                                       F_COD_UNIDADE_ORIGEM, F_TOKEN_INTEGRACAO));

    -- Verificamos se a unidade de origem pertence a empresa.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(V_COD_EMPRESA,
                                                F_COD_UNIDADE_DESTINO,
                                                FORMAT('Unidade (%s) não autorizada para o token: %s',
                                                       F_COD_UNIDADE_DESTINO, F_TOKEN_INTEGRACAO));

    -- Pegamos informações necessárias para executar o procedimento de transferência
    SELECT V.CODIGO,
           VT.COD_DIAGRAMA,
           VT.CODIGO,
           V.KM
    FROM VEICULO_DATA V
             JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
        -- Fazemos esse Join para remover a placa do retorno caso ela não estiver mapeada na tabela de integração
             JOIN INTEGRACAO.VEICULO_CADASTRADO VC ON V.CODIGO = VC.COD_VEICULO_CADASTRO_PROLOG
    WHERE V.PLACA = F_PLACA
    INTO V_COD_VEICULO, V_COD_DIAGRAMA_VEICULO, V_COD_TIPO_VEICULO, V_KM_VEICULO;

    -- Verificamos se o veículo pertence a unidade origem.
    IF (V_COD_VEICULO IS NULL)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('A placa (%s) não está cadastrada no Sistema Prolog', F_PLACA));
    END IF;

    -- Pegamos informações necessárias para executar o procedimento de transferência
    SELECT C.CODIGO,
           C.COD_UNIDADE
    FROM COLABORADOR_DATA C
    WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA
    INTO V_COD_COLABORADOR, V_COD_UNIDADE_COLABORADOR;

    -- Validamos se o colaborador existe, ignorando o fato de estar ativado ou desativado.
    IF (V_COD_COLABORADOR IS NULL)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('O colaborador com CPF: %s não está cadastrado no Sistema Prolog',
                                           F_CPF_COLABORADOR_TRANSFERENCIA));
    END IF;

    -- Precisamos desativar as constraints, para verificar apenas no commit da function, assim poderemos transferir o
    -- veículo e seus pneus com segurança.
    SET CONSTRAINTS ALL DEFERRED;

    -- Cria processo de transferência para veículo.
    INSERT INTO VEICULO_TRANSFERENCIA_PROCESSO(COD_UNIDADE_ORIGEM,
                                               COD_UNIDADE_DESTINO,
                                               COD_UNIDADE_COLABORADOR,
                                               COD_COLABORADOR_REALIZACAO,
                                               DATA_HORA_TRANSFERENCIA_PROCESSO,
                                               OBSERVACAO)
    VALUES (F_COD_UNIDADE_ORIGEM,
            F_COD_UNIDADE_DESTINO,
            V_COD_UNIDADE_COLABORADOR,
            V_COD_COLABORADOR,
            F_DATA_HORA,
            F_OBSERVACAO)
    RETURNING CODIGO INTO V_COD_PROCESSO_TRANSFERENCIA_VEICULO;

    -- Verifica se processo foi criado corretamente.
    IF (V_COD_PROCESSO_TRANSFERENCIA_VEICULO IS NULL OR V_COD_PROCESSO_TRANSFERENCIA_VEICULO <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao criar processo de transferência';
    END IF;

    -- Insere valores da transferência do veículo
    INSERT INTO VEICULO_TRANSFERENCIA_INFORMACOES(COD_PROCESSO_TRANSFERENCIA,
                                                  COD_VEICULO,
                                                  COD_DIAGRAMA_VEICULO,
                                                  COD_TIPO_VEICULO,
                                                  KM_VEICULO_MOMENTO_TRANSFERENCIA)
    VALUES (V_COD_PROCESSO_TRANSFERENCIA_VEICULO,
            V_COD_VEICULO,
            V_COD_DIAGRAMA_VEICULO,
            V_COD_TIPO_VEICULO,
            V_KM_VEICULO)
    RETURNING CODIGO INTO V_COD_INFORMACOES_TRANSFERENCIA;

    -- Verifica se os valores da transferência foram adicionados com sucesso.
    IF (V_COD_INFORMACOES_TRANSFERENCIA IS NULL OR V_COD_INFORMACOES_TRANSFERENCIA <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao adicionar valores da transferência';
    END IF;

    -- TODO - verificar através da func, se devemos fechar ou não Itens de O.S (FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO)
    -- Deleta O.S. do veículo transferido.
    PERFORM FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO(V_COD_VEICULO,
                                                               V_COD_INFORMACOES_TRANSFERENCIA,
                                                               F_DATA_HORA);

    -- Transfere veículo.
    UPDATE VEICULO
    SET COD_UNIDADE = F_COD_UNIDADE_DESTINO
    WHERE CODIGO = V_COD_VEICULO;

    -- Transfere veículo na integração.
    UPDATE INTEGRACAO.VEICULO_CADASTRADO
    SET COD_UNIDADE_CADASTRO = F_COD_UNIDADE_DESTINO
    WHERE COD_VEICULO_CADASTRO_PROLOG = V_COD_VEICULO;

    -- Verifica se placa possui pneus aplicados, caso tenha, transferimos esses pneus para a unidade origem.
    IF (EXISTS(SELECT COD_PNEU FROM VEICULO_PNEU WHERE PLACA = F_PLACA))
    THEN
        -- Criamos array com os cod_pneu.
        SELECT ARRAY_AGG(P.CODIGO_CLIENTE)
        FROM PNEU_DATA P
        WHERE P.CODIGO IN (SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA)
        INTO V_COD_PNEUS_TRANSFERIR;

        V_COD_PROCESSO_TRANSFERENCIA_PNEU =
                (INTEGRACAO.FUNC_PNEU_TRANSFERE_PNEU_ENTRE_UNIDADES(F_COD_UNIDADE_ORIGEM,
                                                                    F_COD_UNIDADE_DESTINO,
                                                                    F_CPF_COLABORADOR_TRANSFERENCIA,
                                                                    V_COD_PNEUS_TRANSFERIR,
                                                                    V_OBSERVACAO_TRANSFERENCIA_PNEU,
                                                                    F_TOKEN_INTEGRACAO,
                                                                    F_DATA_HORA,
                                                                    TRUE));
        -- Verifica se a transferência deu certo.
        IF (V_COD_PROCESSO_TRANSFERENCIA_PNEU <= 0)
        THEN
            RAISE EXCEPTION
                'Erro ao transferir os pneus (%s) aplicados ao veículo', ARRAY_TO_STRING(V_COD_PNEUS_TRANSFERIR, ', ');
        END IF;

        --  Modifica unidade dos vínculos.
        UPDATE VEICULO_PNEU
        SET COD_UNIDADE = F_COD_UNIDADE_DESTINO
        WHERE PLACA = F_PLACA;

        -- Adiciona VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU.
        INSERT INTO VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU(COD_VEICULO_TRANSFERENCIA_INFORMACOES,
                                                                COD_PROCESSO_TRANSFERENCIA_PNEU)
        VALUES (V_COD_INFORMACOES_TRANSFERENCIA,
                V_COD_PROCESSO_TRANSFERENCIA_PNEU);
    END IF;
END;
$$;



CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_VEICULO_ATIVA_DESATIVA_VEICULO_PROLOG(F_PLACA_VEICULO TEXT,
                                                          F_ATIVAR_DESATIVAR_VEICULO BOOLEAN,
                                                          F_DATA_HORA_EDICAO_VEICULO TIMESTAMP WITH TIME ZONE,
                                                          F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO  BIGINT := (SELECT V.COD_EMPRESA
                                    FROM PUBLIC.VEICULO V
                                    WHERE V.PLACA = F_PLACA_VEICULO);
    COD_UNIDADE_VEICULO  BIGINT := (SELECT V.COD_UNIDADE
                                    FROM PUBLIC.VEICULO V
                                    WHERE V.PLACA = F_PLACA_VEICULO);
    COD_VEICULO_PROLOG   BIGINT;
    F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
    -- Validamos se a Unidade pertence a mesma empresa do token.
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(
            COD_EMPRESA_VEICULO,
            F_TOKEN_INTEGRACAO,
            FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s",
                   verificar vínculos', F_TOKEN_INTEGRACAO, COD_UNIDADE_VEICULO));

    -- Validamos se a Unidade pertence a mesma empresa do token.
    IF ((SELECT U.COD_EMPRESA
         FROM PUBLIC.VEICULO V
                  JOIN PUBLIC.UNIDADE U ON V.COD_UNIDADE = U.CODIGO
         WHERE V.PLACA = F_PLACA_VEICULO) <> COD_EMPRESA_VEICULO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT(
                        '[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da placa "%s", verificar vínculos',
                        F_TOKEN_INTEGRACAO,
                        F_PLACA_VEICULO));
    END IF;

    -- Validamos se a placa já existe no ProLog.
    IF (SELECT NOT EXISTS(SELECT V.CODIGO FROM PUBLIC.VEICULO_DATA V WHERE V.PLACA::TEXT = F_PLACA_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('[ERRO DE DADOS] A placa "%s" não existe no Sistema ProLog', F_PLACA_VEICULO));
    END IF;

    UPDATE PUBLIC.VEICULO_DATA
    SET STATUS_ATIVO = F_ATIVAR_DESATIVAR_VEICULO
    WHERE PLACA = F_PLACA_VEICULO
      AND COD_UNIDADE = COD_UNIDADE_VEICULO
    RETURNING CODIGO INTO COD_VEICULO_PROLOG;

    UPDATE INTEGRACAO.VEICULO_CADASTRADO
    SET DATA_HORA_ULTIMA_EDICAO = F_DATA_HORA_EDICAO_VEICULO
    WHERE COD_EMPRESA_CADASTRO = COD_EMPRESA_VEICULO
      AND PLACA_VEICULO_CADASTRO = F_PLACA_VEICULO;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- VERIFICAMOS SE O UPDATE NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível ativar/desativar a placa "%" na tabela de mapeamento', F_PLACA_VEICULO;
    END IF;

    RETURN COD_VEICULO_PROLOG;
END;
$$;


DROP FUNCTION IF EXISTS INTEGRACAO.FUNC_CHECKLIST_ALTERNATIVAS_MODELO_CHECKLIST(TEXT, BOOLEAN, BOOLEAN, BOOLEAN);
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_CHECKLIST_ALTERNATIVAS_MODELO_CHECKLIST(F_TOKEN_INTEGRACAO TEXT,
                                                            F_APENAS_MODELOS_CHECKLIST_ATIVOS BOOLEAN,
                                                            F_APENAS_PERGUNTAS_ATIVAS BOOLEAN,
                                                            F_APENAS_ALTERNATIVAS_ATIVAS BOOLEAN)
    RETURNS TABLE
            (
                COD_UNIDADE              BIGINT,
                NOME_UNIDADE             TEXT,
                COD_MODELO_CHECKLIST     BIGINT,
                NOME_MODELO              TEXT,
                STATUS_MODELO_CHECKLIST  BOOLEAN,
                CODIGO_PERGUNTA          BIGINT,
                DESCRICAO_PERGUNTA       TEXT,
                TIPO_DE_RESPOSTA         BOOLEAN,
                CODIGO_ALTERNATIVA       BIGINT,
                DESCRICAO_ALTERNATIVA    TEXT,
                ALTERNATIVA_TIPO_OUTROS  BOOLEAN,
                PRIORIDADE               TEXT,
                DEVE_ABRIR_ORDEM_SERVICO BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT U.CODIGO                     AS COD_UNIDADE,
       U.NOME                       AS NOME_UNIDADE,
       CM.CODIGO                    AS COD_MODELO_CHECKLIST,
       CM.NOME                      AS NOME_MODELO,
       CM.STATUS_ATIVO              AS STATUS_MODELO_CHECKLIST,
       CP.CODIGO                    AS CODIGO_PERGUNTA,
       CP.PERGUNTA                  AS DESCRICAO_PERGUNTA,
       CP.SINGLE_CHOICE             AS TIPO_DE_RESPOSTA,
       CAP.CODIGO                   AS CODIGO_ALTERNATIVA,
       CAP.ALTERNATIVA              AS DESCRICAO_ALTERNATIVA,
       CAP.ALTERNATIVA_TIPO_OUTROS  AS ALTERNATIVA_TIPO_OUTROS,
       CAP.PRIORIDADE               AS PRIORIDADE,
       CAP.DEVE_ABRIR_ORDEM_SERVICO AS DEVE_ABRIR_ORDEM_SERVICO
FROM CHECKLIST_PERGUNTAS CP
         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
              ON CP.COD_UNIDADE = CAP.COD_UNIDADE
                  AND CP.COD_CHECKLIST_MODELO = CAP.COD_CHECKLIST_MODELO
                  AND CP.CODIGO = CAP.COD_PERGUNTA
         JOIN CHECKLIST_MODELO CM
              ON CAP.COD_CHECKLIST_MODELO = CM.CODIGO
         JOIN UNIDADE U
              ON CM.COD_UNIDADE = U.CODIGO
WHERE CM.COD_UNIDADE IN (SELECT U.CODIGO
                         FROM UNIDADE U
                         WHERE U.COD_EMPRESA IN (SELECT TI.COD_EMPRESA
                                                 FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                 WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
  AND F_IF(F_APENAS_MODELOS_CHECKLIST_ATIVOS, CM.STATUS_ATIVO = TRUE, TRUE)
ORDER BY CM.STATUS_ATIVO DESC, U.CODIGO, CM.CODIGO, CP.CODIGO, CAP.CODIGO;
$$;



CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_UNIDADE_LISTA_UNIDADES_EMPRESA(F_TOKEN_INTEGRACAO TEXT,
                                                   F_APENAS_UNIDADES_ATIVAS BOOLEAN DEFAULT FALSE)
    RETURNS TABLE
            (
                COD_EMPRESA  BIGINT,
                CODIGO       BIGINT,
                NOME         TEXT,
                STATUS_ATIVO BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT U.COD_EMPRESA,
               U.CODIGO,
               U.NOME::TEXT,
               U.STATUS_ATIVO
        FROM PUBLIC.UNIDADE U
        WHERE U.COD_EMPRESA IN (SELECT TI.COD_EMPRESA
                                FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
          AND F_IF(F_APENAS_UNIDADES_ATIVAS, U.STATUS_ATIVO = TRUE, TRUE)
        ORDER BY U.CODIGO;
END;
$$;



-- Essa function sofreu várias alterações.
-- Primeiro voltamos ela para a versão que está rodando em Prod. Ela havia sido alterada pelo Natan.
-- Segundo, na versão de Prod, adaptamos para usar tokens repetidos.
drop function integracao.func_pneu_atualiza_status_pneu_prolog(bigint, varchar, bigint, varchar, timestamp, varchar, boolean, bigint, numeric, varchar, integer, varchar);
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_ATUALIZA_STATUS_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
                                                     F_CODIGO_PNEU_CLIENTE CHARACTER VARYING,
                                                     F_COD_UNIDADE_PNEU BIGINT,
                                                     F_CPF_COLABORADOR_ALTERACAO_STATUS CHARACTER VARYING,
                                                     F_DATA_HORA_ALTERACAO_STATUS TIMESTAMP WITH TIME ZONE,
                                                     F_STATUS_PNEU CHARACTER VARYING,
                                                     F_TROCOU_DE_BANDA BOOLEAN,
                                                     F_COD_NOVO_MODELO_BANDA_PNEU BIGINT,
                                                     F_VALOR_NOVA_BANDA_PNEU NUMERIC,
                                                     F_PLACA_VEICULO_PNEU_APLICADO CHARACTER VARYING,
                                                     F_POSICAO_VEICULO_PNEU_APLICADO INTEGER,
                                                     F_TOKEN_INTEGRACAO CHARACTER VARYING) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_PNEU            BIGINT  := (SELECT U.COD_EMPRESA
                                            FROM PUBLIC.UNIDADE U
                                            WHERE U.CODIGO = F_COD_UNIDADE_PNEU);
    COD_VEICULO_PROLOG          BIGINT  := (SELECT V.CODIGO
                                            FROM PUBLIC.VEICULO V
                                            WHERE V.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                                              AND V.COD_UNIDADE IN (SELECT U.CODIGO
                                                                    FROM PUBLIC.UNIDADE U
                                                                    WHERE U.COD_EMPRESA = COD_EMPRESA_PNEU));
    IS_POSICAO_ESTEPE           BOOLEAN := F_IF(F_POSICAO_VEICULO_PNEU_APLICADO >= 900
                                                    AND F_POSICAO_VEICULO_PNEU_APLICADO <= 908, TRUE, FALSE);
    COD_PNEU_PROLOG             BIGINT  := (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                                            FROM INTEGRACAO.PNEU_CADASTRADO PC
                                            WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                                              AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU);
    VIDA_ATUAL_PNEU             INTEGER := (SELECT P.VIDA_ATUAL
                                            FROM PUBLIC.PNEU P
                                            WHERE P.CODIGO = COD_PNEU_PROLOG);
    PROXIMA_VIDA_PNEU           INTEGER := VIDA_ATUAL_PNEU + 1;
    STATUS_APLICADO_VEICULO     TEXT    := 'EM_USO';
    COD_SERVICO_INCREMENTA_VIDA BIGINT;
    F_QTD_ROWS_ALTERADAS        BIGINT;
BEGIN
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(COD_EMPRESA_PNEU, F_TOKEN_INTEGRACAO);

    -- Validamos se a Empresa é válida.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_PNEU,
                                        FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

    -- Validamos se a Unidade repassada existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_PNEU,
                                        FORMAT('A Unidade %s repassada não existe no Sistema ProLog',
                                               F_COD_UNIDADE_PNEU));

    -- Validamos se a Unidade pertence a Empresa do token repassado.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(COD_EMPRESA_PNEU,
                                                F_COD_UNIDADE_PNEU,
                                                FORMAT('A Unidade %s não está configurada para esta empresa',
                                                       F_COD_UNIDADE_PNEU));

    -- Validamos se o código do pneu no sistema integrado está mapeado na tabela interna do ProLog.
    IF (COD_PNEU_PROLOG IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s não está mapeado no Sistema ProLog',
                                                  F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Deletamos o vinculo do pneu com a placa. Caso o pneu não estava vinculado, nada irá acontecer.
    DELETE FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = COD_PNEU_PROLOG;

    -- Atualiza o pneu para o status em que ele deve estar.
    UPDATE PUBLIC.PNEU
    SET STATUS      = F_STATUS_PNEU,
        COD_UNIDADE = F_COD_UNIDADE_PNEU
    WHERE CODIGO = COD_PNEU_PROLOG;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- Validamos se o status do pneu foi atualizado com sucesso
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('Não foi possível atualizar as informações do pneu %s para o status %s',
                       F_CODIGO_PNEU_CLIENTE,
                       F_STATUS_PNEU));
    END IF;

    -- Precisamos vincular o pneu ao veículo apenas se o status for aplicado.
    IF (F_STATUS_PNEU = STATUS_APLICADO_VEICULO)
    THEN
        -- Transferimos o pneu para a unidade do veículo, caso ele já não esteja.
        IF ((SELECT P.COD_UNIDADE FROM PUBLIC.PNEU P WHERE P.CODIGO = COD_PNEU_PROLOG) <> F_COD_UNIDADE_PNEU)
        THEN
            UPDATE PUBLIC.PNEU
            SET COD_UNIDADE = (SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG)
            WHERE CODIGO = COD_PNEU_PROLOG;

            SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG INTO F_COD_UNIDADE_PNEU;
        END IF;

        PERFORM INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(COD_VEICULO_PROLOG,
                                                                F_PLACA_VEICULO_PNEU_APLICADO,
                                                                COD_PNEU_PROLOG,
                                                                F_CODIGO_PNEU_CLIENTE,
                                                                F_COD_UNIDADE_PNEU,
                                                                F_POSICAO_VEICULO_PNEU_APLICADO,
                                                                IS_POSICAO_ESTEPE);
    END IF;

    IF (F_TROCOU_DE_BANDA)
    THEN
        -- Validamos se o código do modelo de banda é válido. Apenas validamos se o pneu possuir banda.
        IF (F_COD_NOVO_MODELO_BANDA_PNEU IS NULL)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR('O código do modelo da banda deve ser informado');
        END IF;

        -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
        IF ((SELECT NOT EXISTS(SELECT MB.CODIGO
                               FROM PUBLIC.MODELO_BANDA MB
                               WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                 AND MB.CODIGO = F_COD_NOVO_MODELO_BANDA_PNEU)))
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda do pneu %s não está mapeado no Sistema ProLog',
                                                      F_COD_NOVO_MODELO_BANDA_PNEU));
        END IF;

        -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
        IF (F_VALOR_NOVA_BANDA_PNEU IS NULL)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu deve ser informado');
        END IF;

        -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
        IF (F_VALOR_NOVA_BANDA_PNEU < 0)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu não pode ser um número negativo');
        END IF;

        -- Busca serviço que incrementa a vida do pneu dentro da empresa em questão.
        SELECT *
        FROM PUBLIC.FUNC_PNEU_GET_SERVICO_INCREMENTA_VIDA_PNEU_EMPRESA(COD_EMPRESA_PNEU)
        INTO COD_SERVICO_INCREMENTA_VIDA;

        IF (COD_SERVICO_INCREMENTA_VIDA IS NULL)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR('Erro ao vincular banda ao pneu');
        END IF;

        -- Incrementa a vida do pneu simulando um processo de movimentação.
        PERFORM INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_MOVIMENTACAO(F_COD_UNIDADE_PNEU,
                                                                          COD_PNEU_PROLOG,
                                                                          F_COD_NOVO_MODELO_BANDA_PNEU,
                                                                          F_VALOR_NOVA_BANDA_PNEU,
                                                                          PROXIMA_VIDA_PNEU,
                                                                          COD_SERVICO_INCREMENTA_VIDA);

        -- Após incrementar a vida e criar o serviço, atualizamos o pneu para ficar com a banda e a vida correta.
        PERFORM PUBLIC.FUNC_PNEUS_INCREMENTA_VIDA_PNEU(COD_PNEU_PROLOG, F_COD_NOVO_MODELO_BANDA_PNEU);
    END IF;

    -- Qualquer alteração de status do pneu deve verificar se o pneu tem serviços aberto e fechá-los.
    PERFORM INTEGRACAO.FUNC_PNEU_FECHA_SERVICO_PNEU_AUTOMATICAMENTE(COD_PNEU_PROLOG,
                                                                    F_DATA_HORA_ALTERACAO_STATUS);

    RETURN COD_PNEU_PROLOG;
END;
$$;



CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO(F_TOKEN_INTEGRACAO TEXT,
                                                           F_COD_SISTEMA_INTEGRADO_PNEUS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_PNEUS_PROLOG CONSTANT BIGINT[] := (SELECT ARRAY_AGG(PC.COD_PNEU_CADASTRO_PROLOG)
                                           FROM INTEGRACAO.PNEU_CADASTRADO PC
                                           WHERE PC.COD_EMPRESA_CADASTRO IN (SELECT TI.COD_EMPRESA
                                                                             FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                                             WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)
                                             AND PC.COD_PNEU_SISTEMA_INTEGRADO = ANY (F_COD_SISTEMA_INTEGRADO_PNEUS));
BEGIN
    DELETE
    FROM PUBLIC.VEICULO_PNEU
    WHERE COD_PNEU = ANY (COD_PNEUS_PROLOG);

    UPDATE PNEU
    SET STATUS = 'ESTOQUE'
    WHERE CODIGO = ANY (COD_PNEUS_PROLOG);
END;
$$;



CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_CARGA_INICIAL_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
                                                   F_CODIGO_PNEU_CLIENTE CHARACTER VARYING,
                                                   F_COD_UNIDADE_PNEU BIGINT,
                                                   F_COD_MODELO_PNEU BIGINT,
                                                   F_COD_DIMENSAO_PNEU BIGINT,
                                                   F_PRESSAO_CORRETA_PNEU DOUBLE PRECISION,
                                                   F_VIDA_ATUAL_PNEU INTEGER,
                                                   F_VIDA_TOTAL_PNEU INTEGER,
                                                   F_DOT_PNEU CHARACTER VARYING,
                                                   F_VALOR_PNEU NUMERIC,
                                                   F_PNEU_NOVO_NUNCA_RODADO BOOLEAN,
                                                   F_COD_MODELO_BANDA_PNEU BIGINT,
                                                   F_VALOR_BANDA_PNEU NUMERIC,
                                                   F_STATUS_PNEU CHARACTER VARYING,
                                                   F_PLACA_VEICULO_PNEU_APLICADO CHARACTER VARYING,
                                                   F_POSICAO_VEICULO_PNEU_APLICADO INTEGER,
                                                   F_DATA_HORA_PNEU_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                   F_TOKEN_INTEGRACAO CHARACTER VARYING) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_PNEU                 BIGINT  := (SELECT U.COD_EMPRESA
                                                 FROM PUBLIC.UNIDADE U
                                                 WHERE U.CODIGO = F_COD_UNIDADE_PNEU);
    COD_VEICULO_PROLOG               BIGINT  := (SELECT V.CODIGO
                                                 FROM PUBLIC.VEICULO V
                                                 WHERE V.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                                                   AND V.COD_UNIDADE IN (SELECT U.CODIGO
                                                                         FROM PUBLIC.UNIDADE U
                                                                         WHERE U.COD_EMPRESA = COD_EMPRESA_PNEU));
    IS_POSICAO_ESTEPE                BOOLEAN := F_IF(F_POSICAO_VEICULO_PNEU_APLICADO >= 900
                                                         AND F_POSICAO_VEICULO_PNEU_APLICADO <= 908, TRUE, FALSE);
    STATUS_APLICADO_VEICULO CONSTANT TEXT    := 'EM_USO';
    DEVE_SOBRESCREVER_PNEU           BOOLEAN := (SELECT *
                                                 FROM INTEGRACAO.FUNC_EMPRESA_GET_CONFIG_SOBRESCREVE_PNEUS(
                                                         COD_EMPRESA_PNEU));
    COD_PNEU_PROLOG                  BIGINT;
    F_QTD_ROWS_ALTERADAS             BIGINT;
BEGIN
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(COD_EMPRESA_PNEU, F_TOKEN_INTEGRACAO);

    -- Inserimos o pneu utilizando a function de inserção padrão. Essa function pode sobrescrever as informações do
    -- pneu caso for necessário.
    SELECT *
    FROM INTEGRACAO.FUNC_PNEU_INSERE_PNEU_PROLOG(
            F_COD_PNEU_SISTEMA_INTEGRADO,
            F_CODIGO_PNEU_CLIENTE,
            F_COD_UNIDADE_PNEU,
            F_COD_MODELO_PNEU,
            F_COD_DIMENSAO_PNEU,
            F_PRESSAO_CORRETA_PNEU,
            F_VIDA_ATUAL_PNEU,
            F_VIDA_TOTAL_PNEU,
            F_DOT_PNEU,
            F_VALOR_PNEU,
            F_PNEU_NOVO_NUNCA_RODADO,
            F_COD_MODELO_BANDA_PNEU,
            F_VALOR_BANDA_PNEU,
            F_DATA_HORA_PNEU_CADASTRO,
            F_TOKEN_INTEGRACAO,
            DEVE_SOBRESCREVER_PNEU)
    INTO COD_PNEU_PROLOG;

    -- Validamos se a inserção do pneu aconteceu com sucesso.
    IF (COD_PNEU_PROLOG IS NULL OR COD_PNEU_PROLOG <= 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível cadastrar o pneu %s no Sistema ProLog',
                                                  F_CODIGO_PNEU_CLIENTE));
    END IF;

    -- Atualiza o pneu para o status em que ele deve estar.
    UPDATE PUBLIC.PNEU
    SET STATUS = F_STATUS_PNEU
    WHERE CODIGO = COD_PNEU_PROLOG;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- Validamos se o status do pneu foi atualizado com sucesso
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível inserir o pneu %s com status %s',
                                                  F_CODIGO_PNEU_CLIENTE,
                                                  F_STATUS_PNEU));
    END IF;

    -- Precisamos vincular o pneu ao veículo apenas se o status for aplicado.
    IF (F_STATUS_PNEU = STATUS_APLICADO_VEICULO)
    THEN
        -- Transferimos o pneu para a unidade do veículo, caso ele já não esteja.
        IF ((SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG) <> F_COD_UNIDADE_PNEU)
        THEN
            UPDATE PUBLIC.PNEU
            SET COD_UNIDADE = (SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG)
            WHERE CODIGO = COD_PNEU_PROLOG;

            SELECT V.COD_UNIDADE
            FROM PUBLIC.VEICULO V
            WHERE V.CODIGO = COD_VEICULO_PROLOG
            INTO F_COD_UNIDADE_PNEU;
        END IF;

        PERFORM
            INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(COD_VEICULO_PROLOG,
                                                            F_PLACA_VEICULO_PNEU_APLICADO,
                                                            COD_PNEU_PROLOG,
                                                            F_CODIGO_PNEU_CLIENTE,
                                                            F_COD_UNIDADE_PNEU,
                                                            F_POSICAO_VEICULO_PNEU_APLICADO,
                                                            IS_POSICAO_ESTEPE);
    END IF;
    RETURN COD_PNEU_PROLOG;
END;
$$;



CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_INSERE_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
                                                                   F_CODIGO_PNEU_CLIENTE CHARACTER VARYING,
                                                                   F_COD_UNIDADE_PNEU BIGINT,
                                                                   F_COD_MODELO_PNEU BIGINT,
                                                                   F_COD_DIMENSAO_PNEU BIGINT,
                                                                   F_PRESSAO_CORRETA_PNEU DOUBLE PRECISION,
                                                                   F_VIDA_ATUAL_PNEU INTEGER,
                                                                   F_VIDA_TOTAL_PNEU INTEGER,
                                                                   F_DOT_PNEU CHARACTER VARYING,
                                                                   F_VALOR_PNEU NUMERIC,
                                                                   F_PNEU_NOVO_NUNCA_RODADO BOOLEAN,
                                                                   F_COD_MODELO_BANDA_PNEU BIGINT,
                                                                   F_VALOR_BANDA_PNEU NUMERIC,
                                                                   F_DATA_HORA_PNEU_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                                   F_TOKEN_INTEGRACAO CHARACTER VARYING,
                                                                   F_DEVE_SOBRESCREVER_PNEU BOOLEAN DEFAULT FALSE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    PNEU_PRIMEIRA_VIDA  CONSTANT BIGINT  := 1;
    PNEU_STATUS_ESTOQUE CONSTANT TEXT    := 'ESTOQUE';
    PNEU_POSSUI_BANDA   CONSTANT BOOLEAN := F_IF(F_VIDA_ATUAL_PNEU > PNEU_PRIMEIRA_VIDA, TRUE, FALSE);
    COD_EMPRESA_PNEU    CONSTANT BIGINT  := (SELECT U.COD_EMPRESA
                                             FROM PUBLIC.UNIDADE U
                                             WHERE U.CODIGO = F_COD_UNIDADE_PNEU);
    PNEU_ESTA_NO_PROLOG CONSTANT BOOLEAN := (SELECT EXISTS(SELECT P.CODIGO
                                                           FROM PUBLIC.PNEU P
                                                           WHERE P.CODIGO_CLIENTE = F_CODIGO_PNEU_CLIENTE
                                                             AND P.COD_EMPRESA = COD_EMPRESA_PNEU));
    COD_PNEU_PROLOG              BIGINT;
    F_QTD_ROWS_AFETADAS          BIGINT;
BEGIN
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(COD_EMPRESA_PNEU, F_TOKEN_INTEGRACAO);

    -- Validamos se a Empresa existe.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_PNEU,
                                        FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

    -- Validamos se a Unidade repassada existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_PNEU,
                                        FORMAT('A Unidade %s repassada não existe no Sistema ProLog',
                                               F_COD_UNIDADE_PNEU));

    -- Validamos se a Unidade pertence a Empresa do token repassado.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(COD_EMPRESA_PNEU,
                                                F_COD_UNIDADE_PNEU,
                                                FORMAT('A Unidade %s não está configurada para esta empresa',
                                                       F_COD_UNIDADE_PNEU));

    -- Validamos se o modelo do pneu está mapeado.
    IF (SELECT NOT EXISTS(SELECT MP.CODIGO
                          FROM PUBLIC.MODELO_PNEU MP
                          WHERE MP.COD_EMPRESA = COD_EMPRESA_PNEU
                            AND MP.CODIGO = F_COD_MODELO_PNEU))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo do pneu %s não está mapeado no Sistema ProLog',
                                                  F_COD_MODELO_PNEU));
    END IF;

    -- Validamos se a dimensão do pneu está mapeada.
    IF (SELECT NOT EXISTS(SELECT DP.CODIGO
                          FROM PUBLIC.DIMENSAO_PNEU DP
                          WHERE DP.CODIGO = F_COD_DIMENSAO_PNEU))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A dimensão de código %s do pneu não está mapeada no Sistema ProLog',
                                                  F_COD_DIMENSAO_PNEU));
    END IF;

    -- Validamos se a pressão recomendada é válida.
    IF (F_PRESSAO_CORRETA_PNEU < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('A pressão recomendada para o pneu não pode ser um número negativo');
    END IF;

    -- Validamos se a vida atual é correta.
    IF (F_VIDA_ATUAL_PNEU < PNEU_PRIMEIRA_VIDA)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('A vida atual do pneu deve ser no mínimo 1 (caso novo)');
    END IF;

    -- Validamos se a vida total é válida.
    IF (F_VIDA_TOTAL_PNEU < F_VIDA_ATUAL_PNEU)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('A vida total do pneu não pode ser menor que a vida atual');
    END IF;

    -- Validamos se o valor do pneu é um valor válido.
    IF (F_VALOR_PNEU < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor do pneu não pode ser um número negativo');
    END IF;

    -- Validamos se o código do modelo de banda é válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND F_COD_MODELO_BANDA_PNEU IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT(
                'O pneu %s não está na primeira vida, deve ser informado um modelo de banda',
                F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND (SELECT NOT EXISTS(SELECT MB.CODIGO
                                                 FROM PUBLIC.MODELO_BANDA MB
                                                 WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                                   AND MB.CODIGO = F_COD_MODELO_BANDA_PNEU)))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda %s do pneu não está mapeado no Sistema ProLog',
                                                  F_COD_MODELO_BANDA_PNEU));
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND F_VALOR_BANDA_PNEU IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                'O pneu não está na primeira vida, deve ser informado o valor da banda aplicada');
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND F_VALOR_BANDA_PNEU < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu não pode ser um número negativo');
    END IF;

    -- Validamos se o código do sistema integrado já está mapeado na tabela, apenas se não estiver devemos sobrescrever.
    -- Pode acontecer o caso onde o pneu está na base do ProLog e é rodado a sobrecarga. Neste cenário o pneu deve
    -- apenas ter as informações sobrescritas e a tabela de vínculo atualizada.
    IF (SELECT EXISTS(SELECT PC.COD_PNEU_CADASTRO_PROLOG
                      FROM INTEGRACAO.PNEU_CADASTRADO PC
                      WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                        AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU) AND NOT F_DEVE_SOBRESCREVER_PNEU)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s já está cadastrado no Sistema ProLog',
                                                  F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Já validamos se o pneu existe no ProLog através código do sistema integrado, então sobrescrevemos as
    -- informações dele ou, caso não deva sobrescrever, inserimos no base. Validamos também se o pneu já está na base
    -- do ProLog, caso ele não esteja, deveremos inserir e não sobrescrever.
    IF (PNEU_ESTA_NO_PROLOG AND F_DEVE_SOBRESCREVER_PNEU)
    THEN
        -- Pegamos o código do pneu que iremos sobrescrever.
        SELECT P.CODIGO
        FROM PUBLIC.PNEU P
        WHERE P.CODIGO_CLIENTE = F_CODIGO_PNEU_CLIENTE
          AND P.COD_EMPRESA = COD_EMPRESA_PNEU
        INTO COD_PNEU_PROLOG;

        -- Sebrescrevemos os dados do pneu.
        PERFORM INTEGRACAO.FUNC_PNEU_SOBRESCREVE_PNEU_CADASTRADO(COD_PNEU_PROLOG,
                                                                 F_COD_UNIDADE_PNEU,
                                                                 F_COD_MODELO_PNEU,
                                                                 F_COD_DIMENSAO_PNEU,
                                                                 F_PRESSAO_CORRETA_PNEU,
                                                                 F_VIDA_ATUAL_PNEU,
                                                                 F_VIDA_TOTAL_PNEU,
                                                                 F_DOT_PNEU,
                                                                 F_VALOR_PNEU,
                                                                 F_PNEU_NOVO_NUNCA_RODADO,
                                                                 F_COD_MODELO_BANDA_PNEU,
                                                                 F_VALOR_BANDA_PNEU,
                                                                 F_DATA_HORA_PNEU_CADASTRO);
    ELSEIF (NOT PNEU_ESTA_NO_PROLOG)
    THEN
        -- Deveremos inserir os dados na base.
        INSERT INTO PUBLIC.PNEU(COD_EMPRESA,
                                COD_UNIDADE_CADASTRO,
                                COD_UNIDADE,
                                CODIGO_CLIENTE,
                                COD_MODELO,
                                COD_DIMENSAO,
                                PRESSAO_RECOMENDADA,
                                PRESSAO_ATUAL,
                                ALTURA_SULCO_INTERNO,
                                ALTURA_SULCO_CENTRAL_INTERNO,
                                ALTURA_SULCO_CENTRAL_EXTERNO,
                                ALTURA_SULCO_EXTERNO,
                                STATUS,
                                VIDA_ATUAL,
                                VIDA_TOTAL,
                                DOT,
                                VALOR,
                                COD_MODELO_BANDA,
                                PNEU_NOVO_NUNCA_RODADO,
                                DATA_HORA_CADASTRO)
        VALUES (COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_COD_MODELO_PNEU,
                F_COD_DIMENSAO_PNEU,
                F_PRESSAO_CORRETA_PNEU,
                0, -- PRESSAO_ATUAL
                NULL, -- ALTURA_SULCO_INTERNO
                NULL, -- ALTURA_SULCO_CENTRAL_INTERNO
                NULL, -- ALTURA_SULCO_CENTRAL_EXTERNO
                NULL, -- ALTURA_SULCO_EXTERNO
                PNEU_STATUS_ESTOQUE,
                F_VIDA_ATUAL_PNEU,
                F_VIDA_TOTAL_PNEU,
                F_DOT_PNEU,
                F_VALOR_PNEU,
                F_IF(PNEU_POSSUI_BANDA, F_COD_MODELO_BANDA_PNEU, NULL),
                   -- Forçamos FALSE caso o pneu já possua uma banda aplicada.
                F_IF(PNEU_POSSUI_BANDA, FALSE, F_PNEU_NOVO_NUNCA_RODADO),
                F_DATA_HORA_PNEU_CADASTRO)
        RETURNING CODIGO INTO COD_PNEU_PROLOG;

        -- Precisamos criar um serviço de incremento de vida para o pneu cadastrado já possuíndo uma banda.
        IF (PNEU_POSSUI_BANDA)
        THEN
            PERFORM FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(F_COD_UNIDADE_PNEU,
                                                               COD_PNEU_PROLOG,
                                                               F_COD_MODELO_BANDA_PNEU,
                                                               F_VALOR_BANDA_PNEU,
                                                               F_VIDA_ATUAL_PNEU);
        END IF;
    ELSE
        -- Pneu está no ProLog e não deve sobrescrever.
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('O pneu %s já está cadastrado no Sistema ProLog', F_CODIGO_PNEU_CLIENTE));
    END IF;

    IF (F_DEVE_SOBRESCREVER_PNEU)
    THEN
        -- Se houve uma sobrescrita de dados, então tentamos inserir, caso a constraint estourar,
        -- apenas atualizamos os dados. Tentamos inserir antes, pois, em cenários onde o pneu já encontra-se no ProLog,
        -- não temos nenhuma entrada para ele na tabela de mapeamento.
        INSERT INTO INTEGRACAO.PNEU_CADASTRADO(COD_PNEU_CADASTRO_PROLOG,
                                               COD_PNEU_SISTEMA_INTEGRADO,
                                               COD_EMPRESA_CADASTRO,
                                               COD_UNIDADE_CADASTRO,
                                               COD_CLIENTE_PNEU_CADASTRO,
                                               TOKEN_AUTENTICACAO_CADASTRO,
                                               DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_PNEU_PROLOG,
                F_COD_PNEU_SISTEMA_INTEGRADO,
                COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_TOKEN_INTEGRACAO,
                F_DATA_HORA_PNEU_CADASTRO)
        ON CONFLICT ON CONSTRAINT UNIQUE_PNEU_CADASTRADO_EMPRESA_INTEGRACAO
            DO UPDATE SET COD_PNEU_SISTEMA_INTEGRADO  = F_COD_PNEU_SISTEMA_INTEGRADO,
                          COD_UNIDADE_CADASTRO        = F_COD_UNIDADE_PNEU,
                          COD_CLIENTE_PNEU_CADASTRO   = F_CODIGO_PNEU_CLIENTE,
                          TOKEN_AUTENTICACAO_CADASTRO = F_TOKEN_INTEGRACAO,
                          DATA_HORA_ULTIMA_EDICAO     = F_DATA_HORA_PNEU_CADASTRO;
    ELSE
        -- Se não houve sobrescrita de dados, significa que devemos apenas inserir os dados na tabela de mapeamento e
        -- deixar um erro estourar caso pneu já exista.
        INSERT INTO INTEGRACAO.PNEU_CADASTRADO(COD_PNEU_CADASTRO_PROLOG,
                                               COD_PNEU_SISTEMA_INTEGRADO,
                                               COD_EMPRESA_CADASTRO,
                                               COD_UNIDADE_CADASTRO,
                                               COD_CLIENTE_PNEU_CADASTRO,
                                               TOKEN_AUTENTICACAO_CADASTRO,
                                               DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_PNEU_PROLOG,
                F_COD_PNEU_SISTEMA_INTEGRADO,
                COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_TOKEN_INTEGRACAO,
                F_DATA_HORA_PNEU_CADASTRO);
    END IF;

    GET DIAGNOSTICS F_QTD_ROWS_AFETADAS = ROW_COUNT;

    -- Verificamos se a inserção na tabela de mapeamento ocorreu com sucesso.
    IF (F_QTD_ROWS_AFETADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir o pneu "%" na tabela de mapeamento', F_CODIGO_PNEU_CLIENTE;
    END IF;

    RETURN COD_PNEU_PROLOG;
END;
$$;



CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_ATUALIZA_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
                                                                     F_NOVO_CODIGO_PNEU_CLIENTE TEXT,
                                                                     F_NOVO_COD_MODELO_PNEU BIGINT,
                                                                     F_NOVO_COD_DIMENSAO_PNEU BIGINT,
                                                                     F_NOVO_DOT_PNEU TEXT,
                                                                     F_NOVO_VALOR_PNEU REAL,
                                                                     F_NOVO_COD_MODELO_BANDA_PNEU BIGINT,
                                                                     F_NOVO_VALOR_BANDA_PNEU REAL,
                                                                     F_DATA_HORA_EDICAO_PNEU TIMESTAMP WITH TIME ZONE,
                                                                     F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Precisamos utilizar o código do modelo de pneu para chegar até o código da empresa.
    COD_EMPRESA_PNEU     BIGINT  := (SELECT MP.COD_EMPRESA
                                     FROM PUBLIC.MODELO_PNEU MP
                                     WHERE MP.CODIGO = F_NOVO_COD_MODELO_PNEU);
    COD_PNEU_PROLOG      BIGINT  := (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                                     FROM INTEGRACAO.PNEU_CADASTRADO PC
                                     WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                                       AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU);
    PNEU_POSSUI_BANDA    BOOLEAN := F_IF(((SELECT P.COD_MODELO_BANDA
                                           FROM PUBLIC.PNEU P
                                           WHERE P.CODIGO = COD_PNEU_PROLOG) IS NULL), FALSE, TRUE);
    TROCOU_BANDA_PNEU    BOOLEAN := F_IF(F_NOVO_COD_MODELO_BANDA_PNEU IS NULL, FALSE, TRUE);
    F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(COD_EMPRESA_PNEU, F_TOKEN_INTEGRACAO);

    -- Validamos se a Empresa é válida.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(
            COD_EMPRESA_PNEU,
            FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

    -- Validamos se o código do pneu no sistema integrado está mapeado na tabela interna do ProLog.
    IF (COD_PNEU_PROLOG IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('O pneu de código interno %s não está mapeado no Sistema ProLog',
                       F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Validamos se o novo_codigo_cliente é um código válido ou já possui um igual na base dados.
    IF (SELECT EXISTS(SELECT PC.COD_PNEU_CADASTRO_PROLOG
                      FROM INTEGRACAO.PNEU_CADASTRADO PC
                      WHERE PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU
                        AND PC.COD_CLIENTE_PNEU_CADASTRO = F_NOVO_CODIGO_PNEU_CLIENTE
                        AND PC.COD_PNEU_SISTEMA_INTEGRADO != F_COD_PNEU_SISTEMA_INTEGRADO))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('Já existe um pneu com o código %s cadastrado no Sistema ProLog',
                                              F_NOVO_CODIGO_PNEU_CLIENTE));
    END IF;

    -- Validamos se o modelo do pneu está mapeado.
    IF (SELECT NOT EXISTS(SELECT MP.CODIGO
                          FROM PUBLIC.MODELO_PNEU MP
                          WHERE MP.COD_EMPRESA = COD_EMPRESA_PNEU
                            AND MP.CODIGO = F_NOVO_COD_MODELO_PNEU))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo do pneu %s não está mapeado', F_NOVO_COD_MODELO_PNEU));
    END IF;

    -- Validamos se a dimensão do pneu está mapeada.
    IF (SELECT NOT EXISTS(SELECT DP.CODIGO
                          FROM PUBLIC.DIMENSAO_PNEU DP
                          WHERE DP.CODIGO = F_NOVO_COD_DIMENSAO_PNEU))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('A dimensão de código %s do pneu não está mapeada',
                                              F_NOVO_COD_DIMENSAO_PNEU));
    END IF;

    -- Validamos se o valor do pneu é um valor válido.
    IF (F_NOVO_VALOR_PNEU < 0)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O valor do pneu não pode ser um número negativo');
    END IF;

    -- Validamos se o pneu possui banda e se ela não foi removida na atualização.
    IF (PNEU_POSSUI_BANDA AND F_NOVO_COD_MODELO_BANDA_PNEU IS NULL)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O modelo da banda do pneu deve ser informado');
    END IF;

    -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND (SELECT NOT EXISTS(SELECT MB.CODIGO
                                                 FROM PUBLIC.MODELO_BANDA MB
                                                 WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                                   AND MB.CODIGO = F_NOVO_COD_MODELO_BANDA_PNEU)))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda do pneu %s não está mapeado',
                                              F_NOVO_COD_MODELO_BANDA_PNEU));
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda e a mesma tiver sido
    -- atualizada.
    IF (PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU AND F_NOVO_VALOR_BANDA_PNEU IS NULL)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR(
                    'Você está trocando a banda, deve ser informado o valor da nova banda aplicada');
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU AND F_NOVO_VALOR_BANDA_PNEU IS NULL AND F_NOVO_VALOR_BANDA_PNEU < 0)
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('O valor da nova banda do pneu não pode ser um número negativo');
    END IF;

    UPDATE PUBLIC.PNEU
    SET CODIGO_CLIENTE   = F_NOVO_CODIGO_PNEU_CLIENTE,
        COD_MODELO       = F_NOVO_COD_MODELO_PNEU,
        COD_DIMENSAO     = F_NOVO_COD_DIMENSAO_PNEU,
        DOT              = F_NOVO_DOT_PNEU,
        VALOR            = F_NOVO_VALOR_PNEU,
        COD_MODELO_BANDA = F_IF(PNEU_POSSUI_BANDA AND TROCOU_BANDA_PNEU, F_NOVO_COD_MODELO_BANDA_PNEU, NULL)
    WHERE CODIGO = COD_PNEU_PROLOG;

    UPDATE INTEGRACAO.PNEU_CADASTRADO
    SET COD_CLIENTE_PNEU_CADASTRO = F_NOVO_CODIGO_PNEU_CLIENTE,
        DATA_HORA_ULTIMA_EDICAO   = F_DATA_HORA_EDICAO_PNEU
    WHERE COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU
      AND COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- Verificamos se a atualização na tabela de mapeamento ocorreu com sucesso.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION 'Não foi possível atualizar o pneu % na tabela de mapeamento', COD_PNEU_PROLOG;
    END IF;

    IF (PNEU_POSSUI_BANDA
        AND NOT (SELECT *
                 FROM PUBLIC.FUNC_PNEUS_UPDATE_BANDA_PNEU(COD_PNEU_PROLOG,
                                                          F_NOVO_COD_MODELO_BANDA_PNEU,
                                                          F_NOVO_VALOR_BANDA_PNEU)))
    THEN
        PERFORM
            PUBLIC.THROW_GENERIC_ERROR('Não foi possível atualizar a banda do pneu');
    END IF;

    RETURN COD_PNEU_PROLOG;
END;
$$;



CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_TRANSFERE_PNEU_ENTRE_UNIDADES(F_COD_UNIDADE_ORIGEM BIGINT,
                                                       F_COD_UNIDADE_DESTINO BIGINT,
                                                       F_CPF_COLABORADOR_TRANSFERENCIA BIGINT,
                                                       F_LISTA_PNEUS TEXT[],
                                                       F_OBSERVACAO TEXT,
                                                       F_TOKEN_INTEGRACAO TEXT,
                                                       F_DATA_HORA TIMESTAMP WITH TIME ZONE,
                                                       F_PNEUS_APLICADOS_TRANSFERENCIA_PLACA BOOLEAN DEFAULT FALSE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA                BIGINT := (SELECT U.COD_EMPRESA
                                            FROM PUBLIC.UNIDADE U
                                            WHERE U.CODIGO = F_COD_UNIDADE_ORIGEM);
    V_COD_COLABORADOR            BIGINT := (SELECT C.CODIGO
                                            FROM COLABORADOR_DATA C
                                            WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA);
    V_COD_UNIDADE_COLABORADOR    BIGINT := (SELECT C.COD_UNIDADE
                                            FROM COLABORADOR_DATA C
                                            WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA);
    V_TIPO_TRANSFERENCIA         TIPO_PROCESSO_TRANSFERENCIA_PNEU
                                        := 'TRANSFERENCIA_APENAS_PNEUS';
    V_PNEUS_NAO_MAPEADOS         TEXT[] := (SELECT ARRAY_AGG(CCNE.COD_CLIENTE)
                                            FROM (SELECT UNNEST(F_LISTA_PNEUS) AS COD_CLIENTE) AS CCNE
                                            WHERE CCNE.COD_CLIENTE
                                                      NOT IN (SELECT PC.COD_CLIENTE_PNEU_CADASTRO
                                                              FROM INTEGRACAO.PNEU_CADASTRADO PC
                                                              WHERE PC.COD_EMPRESA_CADASTRO = V_COD_EMPRESA));
    V_COD_PROCESSO_TRANSFERENCIA BIGINT;
    V_COD_PNEUS_TRANSFERIR       BIGINT[];
    V_QTD_ROWS                   BIGINT;
BEGIN
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(V_COD_EMPRESA, F_TOKEN_INTEGRACAO);

    -- Verificamos se a empresa existe
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(V_COD_EMPRESA,
                                        FORMAT('Token utilizado não está autorizado: %s', F_TOKEN_INTEGRACAO));

    -- Verifica se unidade origem existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_ORIGEM,
                                        FORMAT('A unidade de origem (%s) do pneu não está mapeada',
                                               F_COD_UNIDADE_ORIGEM));

    -- Verifica se unidade destino existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO,
                                        FORMAT('A unidade de destino (%s) do pneu não está mapeada',
                                               F_COD_UNIDADE_DESTINO));

    -- Validamos se a unidade de origem pertence a empresa
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(
            V_COD_EMPRESA,
            F_COD_UNIDADE_ORIGEM,
            FORMAT('Unidade (%s) não autorizada para o token: %s', F_COD_UNIDADE_ORIGEM, F_TOKEN_INTEGRACAO));

    -- Validamos se a unidade de origem pertence a empresa
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(
            V_COD_EMPRESA,
            F_COD_UNIDADE_DESTINO,
            FORMAT('Unidade (%s) não autorizada para o token: %s', F_COD_UNIDADE_DESTINO, F_TOKEN_INTEGRACAO));

    -- Validamos se o colaborador existe, ignorando o fato de estar ativado ou desativado.
    IF NOT EXISTS(SELECT C.CODIGO FROM COLABORADOR_DATA C WHERE C.CPF = F_CPF_COLABORADOR_TRANSFERENCIA)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('O colaborador com CPF: %s não está cadastrado no Sistema Prolog',
                                           F_CPF_COLABORADOR_TRANSFERENCIA));
    END IF;

    -- Verificamos se temos algum pneu não mapeado.
    IF (F_SIZE_ARRAY(V_PNEUS_NAO_MAPEADOS) > 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('Os pneus (%s) não estão cadastrados no Sistema Prolog',
                                           ARRAY_TO_STRING(V_PNEUS_NAO_MAPEADOS, ', ')));
    END IF;

    -- Após validar todos os cenários, pegamos os códigos do pneus que iremos transferir. Transferimos apenas os pneus
    -- que não estão na unidade de destino, ou seja, transferimos somente os que precisam.
    SELECT ARRAY_AGG(P.CODIGO)
    FROM PNEU P
    WHERE P.COD_EMPRESA = V_COD_EMPRESA
      AND P.CODIGO_CLIENTE = ANY (F_LISTA_PNEUS)
      AND P.COD_UNIDADE != F_COD_UNIDADE_DESTINO
    INTO V_COD_PNEUS_TRANSFERIR;

    IF (F_SIZE_ARRAY(V_COD_PNEUS_TRANSFERIR) <= 0)
    THEN
        -- Retornamos 1, simulando um processo de transferência que foi sucesso, porém nada foi transferido.
        RETURN 1;
    END IF;

    -- Verifica se os pneus estão aplicados a uma placa na transferência de veículo.
    IF (F_PNEUS_APLICADOS_TRANSFERENCIA_PLACA IS FALSE)
    THEN
        -- Devemos alterar o status de todos os pneus, colocá-los em estoque caso ainda não estejam.
        DELETE FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = ANY (V_COD_PNEUS_TRANSFERIR);

        UPDATE PUBLIC.PNEU
        SET STATUS = 'ESTOQUE'
        WHERE CODIGO = ANY (V_COD_PNEUS_TRANSFERIR);
    ELSE
        V_TIPO_TRANSFERENCIA = 'TRANSFERENCIA_JUNTO_A_VEICULO';
    END IF;

    -- Cria processo de transferência.
    INSERT INTO PNEU_TRANSFERENCIA_PROCESSO(COD_UNIDADE_ORIGEM,
                                            COD_UNIDADE_DESTINO,
                                            COD_UNIDADE_COLABORADOR,
                                            COD_COLABORADOR,
                                            DATA_HORA_TRANSFERENCIA_PROCESSO,
                                            OBSERVACAO,
                                            TIPO_PROCESSO_TRANSFERENCIA)
    VALUES (F_COD_UNIDADE_ORIGEM,
            F_COD_UNIDADE_DESTINO,
            V_COD_UNIDADE_COLABORADOR,
            V_COD_COLABORADOR,
            F_DATA_HORA,
            F_OBSERVACAO,
            V_TIPO_TRANSFERENCIA)
    RETURNING CODIGO INTO V_COD_PROCESSO_TRANSFERENCIA;

    -- Verifica se processo foi criado corretamente.
    IF (V_COD_PROCESSO_TRANSFERENCIA IS NULL OR V_COD_PROCESSO_TRANSFERENCIA <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao criar processo de transferência';
    END IF;

    -- Insere valores da transferência
    V_QTD_ROWS = (FUNC_PNEU_TRANSFERENCIA_INSERT_INFORMACOES(V_COD_PROCESSO_TRANSFERENCIA,
                                                             V_COD_PNEUS_TRANSFERIR));
    -- Verifica se atualizou.
    IF (V_QTD_ROWS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao realizar a transferência de informações';
    END IF;

    -- Atualiza unidade alocada.
    PERFORM INTEGRACAO.FUNC_PNEU_TRANSFERENCIA_ALTERAR_UNIDADE_ALOCADO(F_COD_UNIDADE_DESTINO,
                                                                       V_COD_PNEUS_TRANSFERIR);

    RETURN V_COD_PROCESSO_TRANSFERENCIA;
END;
$$;