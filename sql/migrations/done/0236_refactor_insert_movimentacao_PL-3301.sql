drop function if exists func_movimentacao_insert_movimentacao_veiculo_origem(f_cod_pneu bigint,
    f_cod_unidade bigint,
    f_tipo_origem varchar(255),
    f_cod_movimentacao bigint,
    f_placa_veiculo varchar(7),
    f_km_atual bigint,
    f_posicao_prolog integer);
create or replace function func_movimentacao_insert_movimentacao_veiculo_origem(f_cod_pneu bigint,
                                                                                f_cod_unidade bigint,
                                                                                f_tipo_origem text,
                                                                                f_cod_movimentacao bigint,
                                                                                f_cod_veiculo bigint,
                                                                                f_km_atual bigint,
                                                                                f_posicao_prolog integer)
    returns void
    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo           bigint;
    v_cod_diagrama_veiculo       bigint;
    v_cod_movimentacao_realizada bigint;
    v_tipo_origem_atual constant text := (select p.status
                                          from pneu p
                                          where p.codigo = f_cod_pneu
                                            and p.cod_unidade = f_cod_unidade
                                            and f_tipo_origem in (select p.status
                                                                  from pneu p
                                                                  where p.codigo = f_cod_pneu
                                                                    and p.cod_unidade = f_cod_unidade));
begin
    select v.cod_tipo,
           v.cod_diagrama
    from veiculo v
    where v.codigo = f_cod_veiculo
    into strict
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo;

    -- Realiza inserção da movimentação origem.
    insert into movimentacao_origem(cod_movimentacao,
                                    tipo_origem,
                                    km_veiculo,
                                    posicao_pneu_origem,
                                    cod_diagrama,
                                    cod_veiculo)
    values (f_cod_movimentacao,
            v_tipo_origem_atual,
            f_km_atual,
            f_posicao_prolog,
            v_cod_diagrama_veiculo,
            f_cod_veiculo)
    returning cod_movimentacao into v_cod_movimentacao_realizada;

    if (v_cod_movimentacao_realizada <= 0)
    then
        perform throw_server_side_error('Erro ao inserir a origem veiculo da movimentação');
    end if;
end
$$;


drop function if exists func_movimentacao_insert_movimentacao_veiculo_destino(f_cod_movimentacao bigint,
    f_tipo_destino varchar(255),
    f_placa_veiculo varchar(255),
    f_km_atual bigint,
    f_posicao_prolog bigint);
create or replace function func_movimentacao_insert_movimentacao_veiculo_destino(f_cod_movimentacao bigint,
                                                                                 f_tipo_destino text,
                                                                                 f_cod_veiculo bigint,
                                                                                 f_km_atual bigint,
                                                                                 f_posicao_prolog bigint)
    returns void
    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo           bigint;
    v_cod_diagrama_veiculo       bigint;
    v_cod_movimentacao_realizada bigint;
begin
    select v.cod_tipo,
           v.cod_diagrama
    from veiculo v
    where v.codigo = f_cod_veiculo
    into strict
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo;

    -- Realiza inserção da movimentação destino.
    insert into movimentacao_destino(cod_movimentacao,
                                     tipo_destino,
                                     km_veiculo,
                                     posicao_pneu_destino,
                                     cod_motivo_descarte,
                                     url_imagem_descarte_1,
                                     url_imagem_descarte_2,
                                     url_imagem_descarte_3,
                                     cod_recapadora_destino,
                                     cod_coleta,
                                     cod_diagrama,
                                     cod_veiculo)
    values (f_cod_movimentacao,
            f_tipo_destino,
            f_km_atual,
            f_posicao_prolog,
            null,
            null,
            null,
            null,
            null,
            null,
            v_cod_diagrama_veiculo,
            f_cod_veiculo)
    returning cod_movimentacao into v_cod_movimentacao_realizada;

    if (v_cod_movimentacao_realizada <= 0)
    then
        perform throw_server_side_error('Erro ao inserir o destino veiculo da movimentação');
    end if;
end
$$;


create or replace function func_garante_veiculo_existe_by_codigo(f_cod_unidade_veiculo bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_considerar_deletados boolean default true,
                                                                 f_error_message text default null)
    returns void
    language plpgsql
as
$$
declare
    v_error_message text :=
        f_if(f_error_message is null,
             format('Não foi possível encontrar o veículo com estes parâmetros: Unidade %s, Código %s',
                    f_cod_unidade_veiculo, f_cod_veiculo),
             f_error_message);
begin
    if not exists(select vd.codigo
                  from veiculo_data vd
                  where vd.codigo = f_cod_veiculo
                    and vd.cod_unidade = f_cod_unidade_veiculo
                    and f_if(f_considerar_deletados, true, vd.deletado = false))
    then
        perform throw_generic_error(v_error_message);
    end if;
end;
$$;


drop function if exists suporte.func_pneu_remove_vinculo_pneu(f_cpf_solicitante bigint,
    f_cod_unidade bigint,
    f_placa_veiculo text,
    f_lista_cod_pneus bigint[]);
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_REMOVE_VINCULO_PNEU(F_CPF_SOLICITANTE BIGINT,
                                                                 F_COD_UNIDADE BIGINT,
                                                                 F_COD_VEICULO BIGINT,
                                                                 F_LISTA_COD_PNEUS BIGINT[],
                                                                 OUT AVISO_PNEUS_DESVINCULADOS TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    STATUS_PNEU_ESTOQUE              TEXT                     := 'ESTOQUE';
    STATUS_PNEU_EM_USO               TEXT                     := 'EM_USO';
    DATA_HORA_ATUAL                  TIMESTAMP WITH TIME ZONE := NOW();
    COD_PNEU_DA_VEZ                  BIGINT;
    COD_MOVIMENTACAO_CRIADA          BIGINT;
    COD_PROCESSO_MOVIMENTACAO_CRIADO BIGINT;
    VIDA_ATUAL_PNEU                  BIGINT;
    POSICAO_PNEU                     INTEGER;
    KM_ATUAL_VEICULO                 BIGINT                   := (SELECT V.KM
                                                                  FROM VEICULO V
                                                                  WHERE V.COD_UNIDADE = F_COD_UNIDADE
                                                                    AND V.CODIGO = F_COD_VEICULO);
    NOME_COLABORADOR                 TEXT                     := (SELECT C.NOME
                                                                  FROM COLABORADOR C
                                                                  WHERE C.CPF = F_CPF_SOLICITANTE);
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Verifica se colaborador possui integridade com unidade;
    PERFORM FUNC_GARANTE_INTEGRIDADE_UNIDADE_COLABORADOR(F_COD_UNIDADE, F_CPF_SOLICITANTE);

    -- Verifica se unidade existe;
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- Verifica se veículo existe;
    perform func_garante_veiculo_existe_by_codigo(f_cod_unidade, f_cod_veiculo);

    -- Verifica quantiade de pneus recebida;
    IF (ARRAY_LENGTH(F_LISTA_COD_PNEUS, 1) > 0)
    THEN
        -- Cria processo para movimentação
        INSERT INTO MOVIMENTACAO_PROCESSO(COD_UNIDADE, DATA_HORA, CPF_RESPONSAVEL, OBSERVACAO)
        VALUES (F_COD_UNIDADE,
                DATA_HORA_ATUAL,
                F_CPF_SOLICITANTE,
                'Processo para desvincular o pneu de uma placa')
        RETURNING CODIGO INTO COD_PROCESSO_MOVIMENTACAO_CRIADO;

        FOREACH COD_PNEU_DA_VEZ IN ARRAY F_LISTA_COD_PNEUS
            LOOP
                -- Verifica se pneu não está vinculado a placa informada;
                IF NOT EXISTS(SELECT VP.PLACA
                              FROM VEICULO_PNEU VP
                              WHERE VP.COD_VEICULO = F_COD_VEICULO
                                AND VP.COD_PNEU = COD_PNEU_DA_VEZ)
                THEN
                    RAISE EXCEPTION 'Erro! O pneu com código: % não está vinculado ao veículo de código %',
                        COD_PNEU_DA_VEZ, F_COD_VEICULO;
                END IF;

                -- Busca vida atual e posicao do pneu;
                SELECT P.VIDA_ATUAL, VP.POSICAO
                FROM PNEU P
                         JOIN VEICULO_PNEU VP ON P.CODIGO = VP.COD_PNEU
                WHERE P.CODIGO = COD_PNEU_DA_VEZ
                INTO VIDA_ATUAL_PNEU, POSICAO_PNEU;

                IF (COD_PROCESSO_MOVIMENTACAO_CRIADO > 0)
                THEN
                    -- Insere movimentação retornando o código da mesma;
                    INSERT INTO MOVIMENTACAO(COD_MOVIMENTACAO_PROCESSO,
                                             COD_UNIDADE,
                                             COD_PNEU,
                                             SULCO_INTERNO,
                                             SULCO_CENTRAL_INTERNO,
                                             SULCO_EXTERNO,
                                             VIDA,
                                             OBSERVACAO,
                                             SULCO_CENTRAL_EXTERNO)
                    SELECT COD_PROCESSO_MOVIMENTACAO_CRIADO,
                           F_COD_UNIDADE,
                           COD_PNEU_DA_VEZ,
                           P.ALTURA_SULCO_INTERNO,
                           P.ALTURA_SULCO_CENTRAL_INTERNO,
                           P.ALTURA_SULCO_EXTERNO,
                           VIDA_ATUAL_PNEU,
                           NULL,
                           P.ALTURA_SULCO_CENTRAL_EXTERNO
                    FROM PNEU P
                    WHERE P.CODIGO = COD_PNEU_DA_VEZ
                    RETURNING CODIGO INTO COD_MOVIMENTACAO_CRIADA;

                    -- Insere destino da movimentação;
                    INSERT INTO MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO, TIPO_DESTINO)
                    VALUES (COD_MOVIMENTACAO_CRIADA, STATUS_PNEU_ESTOQUE);

                    -- Insere origem da movimentação;
                    PERFORM FUNC_MOVIMENTACAO_INSERT_MOVIMENTACAO_VEICULO_ORIGEM(COD_PNEU_DA_VEZ,
                                                                                 F_COD_UNIDADE,
                                                                                 STATUS_PNEU_EM_USO,
                                                                                 COD_MOVIMENTACAO_CRIADA,
                                                                                 F_COD_VEICULO,
                                                                                 KM_ATUAL_VEICULO,
                                                                                 POSICAO_PNEU);

                    -- Remove pneu do vinculo;
                    DELETE FROM VEICULO_PNEU WHERE COD_PNEU = COD_PNEU_DA_VEZ AND COD_VEICULO = F_COD_VEICULO;

                    -- Atualiza status do pneu
                    UPDATE PNEU
                    SET STATUS = STATUS_PNEU_ESTOQUE
                    WHERE CODIGO = COD_PNEU_DA_VEZ
                      AND COD_UNIDADE = F_COD_UNIDADE;

                    -- Verifica se o pneu possui serviços em aberto;
                    IF EXISTS(SELECT AM.COD_PNEU
                              FROM AFERICAO_MANUTENCAO AM
                              WHERE AM.COD_UNIDADE = F_COD_UNIDADE
                                AND AM.COD_PNEU = COD_PNEU_DA_VEZ
                                AND AM.DATA_HORA_RESOLUCAO IS NULL
                                AND AM.CPF_MECANICO IS NULL
                                AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                                AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE)
                    THEN
                        -- Remove serviços em aberto;
                        UPDATE AFERICAO_MANUTENCAO
                        SET FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = TRUE,
                            COD_PROCESSO_MOVIMENTACAO            = COD_PROCESSO_MOVIMENTACAO_CRIADO,
                            DATA_HORA_RESOLUCAO                  = DATA_HORA_ATUAL
                        WHERE COD_UNIDADE = F_COD_UNIDADE
                          AND COD_PNEU = COD_PNEU_DA_VEZ
                          AND DATA_HORA_RESOLUCAO IS NULL
                          AND CPF_MECANICO IS NULL
                          AND FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                          AND FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE;
                    END IF;
                ELSE
                    RAISE EXCEPTION 'Erro! Não foi possível realizar o processo de movimentação para o pneu código: %',
                        COD_PNEU_DA_VEZ;
                END IF;
            END LOOP;
    ELSE
        RAISE EXCEPTION 'Erro! Precisa-se de pelo menos um (1) pneu para realizar a operação!';
    END IF;

    -- Mensagem de sucesso;
    SELECT 'Movimentação realizada com sucesso!! Autorizada por ' || NOME_COLABORADOR ||
           ' com CPF: ' || F_CPF_SOLICITANTE || '. Os pneus que estavam na placa de código ' || F_COD_VEICULO ||
           ' foram movidos para estoque.'
    INTO AVISO_PNEUS_DESVINCULADOS;
END
$$;