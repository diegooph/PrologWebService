CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_INTERNAL_VINCULA_PNEU_POSICAO_PLACA(F_PLACA TEXT,
                                                                                    F_COD_PNEU BIGINT,
                                                                                    F_POSICAO INTEGER)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_VEICULO  BIGINT;
    V_COD_UNIDADE  BIGINT;
    V_COD_DIAGRAMA BIGINT := (SELECT COD_DIAGRAMA
                              FROM VEICULO_TIPO
                              WHERE CODIGO = (SELECT COD_TIPO FROM VEICULO_DATA WHERE PLACA = F_PLACA));
BEGIN
    SELECT V.CODIGO, V.COD_UNIDADE
    FROM VEICULO_DATA V
    WHERE V.PLACA = F_PLACA
    INTO V_COD_VEICULO, V_COD_UNIDADE;

    -- Valida se posição existe no diagrama.
    IF NOT EXISTS(SELECT VDPP.POSICAO_PROLOG
                  FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
                  WHERE VDPP.COD_DIAGRAMA = (SELECT V.COD_DIAGRAMA FROM VEICULO_DATA V WHERE V.PLACA = F_PLACA))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A posição %s não existe no diagrama do veículo de placa %s',
                                                  F_POSICAO,
                                                  F_PLACA));
    END IF;

    -- Verifica se tem pneu aplicado nessa posição, caso tenha é prq não passou pelo método
    -- do Java de removePneusAplicados;
    IF EXISTS(SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.POSICAO = F_POSICAO AND VP.COD_VEICULO = V_COD_VEICULO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Erro! O veículo %s já possui pneu aplicado na posição %s',
                                                  F_PLACA,
                                                  F_POSICAO));
    END IF;

    -- Deleta a posição.
    DELETE FROM VEICULO_PNEU WHERE POSICAO = F_POSICAO AND COD_VEICULO = V_COD_VEICULO;

    -- Não tem pneu aplicado a posição, então eu adiciono.
    INSERT INTO VEICULO_PNEU(COD_PNEU, COD_UNIDADE, POSICAO, COD_DIAGRAMA, COD_VEICULO)
    VALUES (F_COD_PNEU, V_COD_UNIDADE, F_POSICAO, V_COD_DIAGRAMA, V_COD_VEICULO);
END;
$$;

create or replace function integracao.func_pneu_vincula_pneu_posicao_placa(f_cod_veiculo_prolog bigint,
                                                                           f_placa_veiculo_pneu_aplicado text,
                                                                           f_cod_pneu_prolog bigint,
                                                                           f_codigo_pneu_cliente text,
                                                                           f_cod_unidade_pneu bigint,
                                                                           f_posicao_veiculo_pneu_aplicado integer,
                                                                           f_is_posicao_estepe boolean)
    returns boolean
    language plpgsql
as
$$
declare
    f_qtd_rows_alteradas bigint;
begin
    -- Validamos se a placa existe no ProLog.
    if (f_cod_veiculo_prolog is null or f_cod_veiculo_prolog <= 0)
    then
        perform public.throw_generic_error(format('A placa informada %s não está presente no Sistema ProLog',
                                                  f_placa_veiculo_pneu_aplicado));
    end if;

    -- Validamos se o placa e o pneu pertencem a mesma unidade.
    if ((select v.cod_unidade from public.veiculo v where v.codigo = f_cod_veiculo_prolog) <> f_cod_unidade_pneu)
    then
        perform public.throw_generic_error(
                format('A placa informada %s está em uma Unidade diferente do pneu informado %s,
               unidade da placa %s, unidade do pneu %s',
                       f_placa_veiculo_pneu_aplicado,
                       f_codigo_pneu_cliente,
                       (select v.cod_unidade from public.veiculo v where v.codigo = f_cod_veiculo_prolog),
                       f_cod_unidade_pneu));
    end if;

    -- Validamos se a posição repassada é uma posição válida no ProLog.
    if (not is_placa_posicao_pneu_valida(f_cod_veiculo_prolog, f_posicao_veiculo_pneu_aplicado, f_is_posicao_estepe))
    then
        perform public.throw_generic_error(
                format('A posição informada %s para o pneu, não é uma posição válida para a placa %s',
                       f_posicao_veiculo_pneu_aplicado,
                       f_placa_veiculo_pneu_aplicado));
    end if;

    -- Validamos se a placa possui algum outro pneu aplicado na posição.
    if (select exists(select *
                      from public.veiculo_pneu vp
                      where vp.cod_veiculo = f_cod_veiculo_prolog
                        and vp.cod_unidade = f_cod_unidade_pneu
                        and vp.posicao = f_posicao_veiculo_pneu_aplicado))
    then
        perform public.throw_generic_error(format('Já existe um pneu na placa %s, posição %s',
                                                  f_placa_veiculo_pneu_aplicado,
                                                  f_posicao_veiculo_pneu_aplicado));
    end if;

    -- Vincula pneu a placa.
    insert into public.veiculo_pneu(cod_pneu,
                                    cod_unidade,
                                    posicao,
                                    cod_diagrama,
                                    cod_veiculo)
    values (f_cod_pneu_prolog,
            f_cod_unidade_pneu,
            f_posicao_veiculo_pneu_aplicado,
            (select vt.cod_diagrama
             from veiculo_tipo vt
             where vt.codigo = (select v.cod_tipo from veiculo v where v.codigo = f_cod_veiculo_prolog)),
            f_cod_veiculo_prolog);

    get diagnostics f_qtd_rows_alteradas = row_count;

    -- Verificamos se o update ocorreu como deveria
    if (f_qtd_rows_alteradas <= 0)
    then
        perform public.throw_generic_error(format('Não foi possível aplicar o pneu %s na placa %s',
                                                  f_codigo_pneu_cliente,
                                                  f_placa_veiculo_pneu_aplicado));
    end if;

    -- Retornamos sucesso se o pneu estiver aplicado na placa e posição que deveria estar.
    if (select exists(select vp.posicao
                      from public.veiculo_pneu vp
                      where vp.cod_veiculo = f_cod_veiculo_prolog
                        and vp.cod_pneu = f_cod_pneu_prolog
                        and vp.posicao = f_posicao_veiculo_pneu_aplicado
                        and vp.cod_unidade = f_cod_unidade_pneu))
    then
        return true;
    else
        perform public.throw_generic_error(format('Não foi possível aplicar o pneu %s na placa %s',
                                                  f_codigo_pneu_cliente,
                                                  f_placa_veiculo_pneu_aplicado));
    end if;
end ;
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
    IF (EXISTS(SELECT COD_PNEU FROM VEICULO_PNEU WHERE COD_VEICULO = V_COD_VEICULO))
    THEN
        -- Criamos array com os cod_pneu.
        SELECT ARRAY_AGG(P.CODIGO_CLIENTE)
        FROM PNEU_DATA P
        WHERE P.CODIGO IN (SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.COD_VEICULO = V_COD_VEICULO)
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
        WHERE COD_VEICULO = V_COD_VEICULO;

        -- Adiciona VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU.
        INSERT INTO VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU(COD_VEICULO_TRANSFERENCIA_INFORMACOES,
                                                                COD_PROCESSO_TRANSFERENCIA_PNEU)
        VALUES (V_COD_INFORMACOES_TRANSFERENCIA,
                V_COD_PROCESSO_TRANSFERENCIA_PNEU);
    END IF;
END;
$$;