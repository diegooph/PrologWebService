begin transaction;
-- PLI - 70
-- Iremos inserir uma coluna para conter a quantidade de tentativas de sincronia. Default será 0, pois o momento
-- que é salvo o código nesta tabela não representa uma tentativa.
alter table piccolotur.checklist_pendente_para_sincronizar
    add column qtd_tentativas integer not null default 0;

comment on column piccolotur.checklist_pendente_para_sincronizar.qtd_tentativas
    is 'Coluna que contém a quantidade de tentativas de sincronia dos itens do checklist. Default será 0, pois o momento
    que é salvo o código nesta tabela não representa uma tentativa.';

-- Insere a data e hora que o checklist foi realizado, para sabermos quando ele entrou nessa tabela.
alter table piccolotur.checklist_pendente_para_sincronizar
    add column data_hora_realizado timestamp with time zone;

comment on column piccolotur.checklist_pendente_para_sincronizar.data_hora_realizado
    is 'Coluna que salva a data e hora que o checklist foi inserido na tabela para ser sincronizado.';

-- Migramos possíveis checklist pendentes para sincronizar, que já estão na base.
update piccolotur.checklist_pendente_para_sincronizar cpps
set data_hora_realizado = (select c.data_hora from checklist_data c where c.codigo = cpps.cod_checklist_para_sincronizar);

alter table piccolotur.checklist_pendente_para_sincronizar
    alter column data_hora_realizado set not null;

-- Insere a data e hora que aconteceu a última atualziação no código do checklist.
alter table piccolotur.checklist_pendente_para_sincronizar
    add column data_hora_ultima_atualizacao timestamp with time zone;

comment on column piccolotur.checklist_pendente_para_sincronizar.data_hora_ultima_atualizacao
    is 'Coluna que salva a data e hora que aconteceu a última atualziação no código do checklist.
    São consideradas atualizações:
    1 - marcar como sincronizado
    2 - marcar como não precisa sincronizar
    3 - marcar como erro ao sincronizar';
-- #####################################################################################################################

-- Criamos uma estrutura para salvar a stacktrace das exceptions que ocorrerem ao sincronizar checklists.
create table if not exists piccolotur.checklist_erros_sincronia
(
    codigo                         bigserial not null,
    cod_checklist_para_sincronizar bigint    not null,
    nova_qtd_tentativas            integer   not null,
    error_stacktrace               text      not null,
    data_hora_erro                 timestamp with time zone,
    constraint pk_checklist_erros_sincronia primary key (codigo),
    constraint check_nova_qtd_tentativas check (nova_qtd_tentativas > 0)
);


-- #####################################################################################################################

-- Cria function para inserir checklsit pendente de sincronia no schema piccolotur.
create or replace function piccolotur.func_check_os_insere_checklist_pendente_sincronia(f_cod_checklist bigint)
    returns void
    language plpgsql
as
$$
begin
    insert into piccolotur.checklist_pendente_para_sincronizar (cod_checklist_para_sincronizar, data_hora_realizado)
    values (f_cod_checklist,
            (select data_hora from checklist where codigo = f_cod_checklist));

    if not found
    then
        -- Não queremos que esse erro seja mapeado para o usuário ou para a integração.
        raise exception '%', (format('Não foi possível inserir o checklist (%s) na tabela de pendentes para envio',
                                     f_cod_checklist));
    end if;
end;
$$;
-- #####################################################################################################################

-- #####################################################################################################################
-- Cria function para marcar um item como não precisa sincronizar no schema piccolotur.
create or replace function
    piccolotur.func_check_os_marca_checklist_nao_precisa_sincronizar(f_cod_checklist bigint,
                                                                     f_data_hora_atualizacao timestamp with time zone)
    returns void
    language plpgsql
as
$$
begin
    update piccolotur.checklist_pendente_para_sincronizar
    set precisa_ser_sincronizado     = false,
        data_hora_ultima_atualizacao = f_data_hora_atualizacao
    where cod_checklist_para_sincronizar = f_cod_checklist;

    if not found
    then
        -- Não queremos que esse erro seja mapeado para o usuário ou para a integração.
        raise exception '%', (format('Não foi possível marcar o checklist (%s) para não ser sincronizado',
                                     f_cod_checklist));
    end if;
end;
$$;
-- #####################################################################################################################

-- #####################################################################################################################
-- Cria function para marcar um item como não precisa sincronizar no schema piccolotur.
create or replace function
    piccolotur.func_check_os_marca_checklist_como_sincronizado(f_cod_checklist bigint,
                                                               f_data_hora_atualizacao timestamp with time zone)
    returns void
    language plpgsql
as
$$
begin
    update piccolotur.checklist_pendente_para_sincronizar
    set sincronizado                 = true,
        data_hora_ultima_atualizacao = f_data_hora_atualizacao
    where cod_checklist_para_sincronizar = f_cod_checklist;

    if not found
    then
        -- Não queremos que esse erro seja mapeado para o usuário ou para a integração.
        raise exception '%', (format('Não foi possível marcar o checklist (%s) como sincronizado', f_cod_checklist));
    end if;
end;
$$;
-- #####################################################################################################################

-- #####################################################################################################################
-- Cria function para inserir o erro que ocorreu ao sincronizar o checklist e incrementar a quantidade de tentativas
-- de sincronia.
create or replace function
    piccolotur.func_check_os_insere_erro_sincronia_checklist(f_cod_checklist bigint,
                                                             f_error_message text,
                                                             f_stacktrace text,
                                                             f_data_hora_atualizacao timestamp with time zone)
    returns void
    language plpgsql
as
$$
declare
    nova_quantidade_tentativas integer;
begin
    update piccolotur.checklist_pendente_para_sincronizar
    set mensagem_erro_ao_sincronizar = f_error_message,
        data_hora_ultima_atualizacao = f_data_hora_atualizacao,
        qtd_tentativas               = qtd_tentativas + 1
    where cod_checklist_para_sincronizar = f_cod_checklist
    returning qtd_tentativas into nova_quantidade_tentativas;

    insert into piccolotur.checklist_erros_sincronia(cod_checklist_para_sincronizar,
                                                     nova_qtd_tentativas,
                                                     error_stacktrace,
                                                     data_hora_erro)
    values (f_cod_checklist, nova_quantidade_tentativas, f_stacktrace, f_data_hora_atualizacao);

    if not found
    then
        -- Não queremos que esse erro seja mapeado para o usuário ou para a integração.
        raise exception '%', (format('Não foi possível salvar a error_message ao tentar sincronizar o checklist (%s)',
                                     f_cod_checklist));
    end if;
end;
$$;
-- #####################################################################################################################

-- #####################################################################################################################
-- Dropamos a function para criar com o nome adequado.
drop function piccolotur.func_check_busca_checklist_itens_nok(f_cod_checklist_prolog bigint);

CREATE OR REPLACE FUNCTION PICCOLOTUR.FUNC_CHECK_OS_BUSCA_CHECKLIST_ITENS_NOK(F_COD_CHECKLIST_PROLOG BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE_CHECKLIST        BIGINT,
                COD_MODELO_CHECKLIST         BIGINT,
                COD_VERSAO_MODELO_CHECKLIST  BIGINT,
                CPF_COLABORADOR_REALIZACAO   TEXT,
                PLACA_VEICULO_CHECKLIST      TEXT,
                KM_COLETADO_CHECKLIST        BIGINT,
                TIPO_CHECKLIST               TEXT,
                DATA_HORA_REALIZACAO         TIMESTAMP WITHOUT TIME ZONE,
                TOTAL_ALTERNATIVAS_NOK       INTEGER,
                COD_CONTEXTO_PERGUNTA_NOK    BIGINT,
                DESCRICAO_PERGUNTA_NOK       TEXT,
                COD_ALTERNATIVA_NOK          BIGINT,
                COD_CONTEXTO_ALTERNATIVA_NOK BIGINT,
                DESCRICAO_ALTERNATIVA_NOK    TEXT,
                PRIORIDADE_ALTERNATIVA_NOK   TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT C.COD_UNIDADE                                                           AS COD_UNIDADE_CHECKLIST,
       C.COD_CHECKLIST_MODELO                                                  AS COD_MODELO_CHECKLIST,
       C.COD_VERSAO_CHECKLIST_MODELO                                           AS COD_VERSAO_MODELO_CHECKLIST,
       LPAD(C.CPF_COLABORADOR::TEXT, 11, '0')                                  AS CPF_COLABORADOR_REALIZACAO,
       C.PLACA_VEICULO::TEXT                                                   AS PLACA_VEICULO_CHECKLIST,
       C.KM_VEICULO                                                            AS KM_COLETADO_CHECKLIST,
       F_IF(C.TIPO::TEXT = 'S'::TEXT, 'SAIDA'::TEXT, 'RETORNO'::TEXT)          AS TIPO_CHECKLIST,
       C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)                      AS DATA_HORA_REALIZACAO,
       C.TOTAL_ALTERNATIVAS_NOK::INTEGER                                       AS TOTAL_ALTERNATIVAS_NOK,
       CP.CODIGO_CONTEXTO                                                      AS COD_CONTEXTO_PERGUNTA_NOK,
       CP.PERGUNTA                                                             AS DESCRICAO_PERGUNTA_NOK,
       CAP.CODIGO                                                              AS COD_ALTERNATIVA_NOK,
       CAP.CODIGO_CONTEXTO                                                     AS COD_CONTEXTO_ALTERNATIVA_NOK,
       F_IF(CAP.ALTERNATIVA_TIPO_OUTROS, CRN.RESPOSTA_OUTROS, CAP.ALTERNATIVA) AS DESCRICAO_ALTERNATIVA_NOK,
       CAP.PRIORIDADE                                                          AS PRIORIDADE_ALTERNATIVA_NOK
FROM CHECKLIST C
         -- Usamos LEFT JOIN para os cenários onde o check não possuir nenhum item NOK, devemos retornar as infos do
         -- checklist mesmo assim.
         LEFT JOIN CHECKLIST_RESPOSTAS_NOK CRN ON C.CODIGO = CRN.COD_CHECKLIST
         LEFT JOIN CHECKLIST_PERGUNTAS CP ON CRN.COD_PERGUNTA = CP.CODIGO
    AND C.COD_VERSAO_CHECKLIST_MODELO = CP.COD_VERSAO_CHECKLIST_MODELO
         LEFT JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON CRN.COD_ALTERNATIVA = CAP.CODIGO
    AND C.COD_VERSAO_CHECKLIST_MODELO = CAP.COD_VERSAO_CHECKLIST_MODELO
WHERE C.CODIGO = F_COD_CHECKLIST_PROLOG;
$$;
-- #####################################################################################################################

-- #####################################################################################################################
-- Dropamos a function para criar com o nome adequado.
drop function piccolotur.func_check_get_next_cod_checklist_para_sincronizar();

CREATE OR REPLACE FUNCTION PICCOLOTUR.FUNC_CHECK_OS_GET_NEXT_COD_CHECKLIST_PARA_SINCRONIZAR()
    RETURNS TABLE
            (
                COD_CHECKLIST BIGINT,
                IS_LAST_COD   BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_CHECKLIST BIGINT;
    IS_LAST_COD   BOOLEAN;
BEGIN
    --   1° - verifica se existe um checklist para sincronizar, se não, seta o de menor código como apto a
    --   sincronização.
    IF ((SELECT COD_CHECKLIST_PARA_SINCRONIZAR
         FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
         WHERE NEXT_TO_SYNC IS TRUE
           AND SINCRONIZADO IS FALSE
           AND PRECISA_SER_SINCRONIZADO IS TRUE
         LIMIT 1) IS NULL)
    THEN
        UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        SET NEXT_TO_SYNC = TRUE
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = (SELECT CPPS.COD_CHECKLIST_PARA_SINCRONIZAR
                                                FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR CPPS
                                                WHERE CPPS.SINCRONIZADO IS FALSE
                                                  AND CPPS.PRECISA_SER_SINCRONIZADO IS TRUE
                                                ORDER BY CPPS.COD_CHECKLIST_PARA_SINCRONIZAR
                                                LIMIT 1);
    END IF;

    --   2° - Verifica se o código marcado para sincronizar é o último código a ser sincronizado
    SELECT CPPS.NEXT_TO_SYNC
    FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR CPPS
    WHERE CPPS.PRECISA_SER_SINCRONIZADO
      AND CPPS.SINCRONIZADO IS FALSE
    ORDER BY CPPS.COD_CHECKLIST_PARA_SINCRONIZAR DESC
    LIMIT 1
    INTO IS_LAST_COD;

    --   3° - Pega o código que está marcado para tentar sincronizar. Utilizamos limit 1 para evitar que mais de um
    --   código seja setado.
    SELECT COD_CHECKLIST_PARA_SINCRONIZAR
    FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
    WHERE NEXT_TO_SYNC = TRUE
    ORDER BY COD_CHECKLIST_PARA_SINCRONIZAR DESC
    LIMIT 1
    INTO COD_CHECKLIST;

    --   4° - Remove a marcação do checklist que estava marcado par sincronizar
    UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
    SET NEXT_TO_SYNC = FALSE
    WHERE COD_CHECKLIST_PARA_SINCRONIZAR = COD_CHECKLIST;

    --   5° - Marca o próximo código que precisa ser sincronizado, se for o último código, então seta o
    -- primeiro como o próximo a ser sincronizado
    IF IS_LAST_COD
    THEN
        UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        SET NEXT_TO_SYNC = TRUE
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = (SELECT CPPS.COD_CHECKLIST_PARA_SINCRONIZAR
                                                FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR CPPS
                                                WHERE CPPS.SINCRONIZADO IS FALSE
                                                  AND CPPS.PRECISA_SER_SINCRONIZADO IS TRUE
                                                ORDER BY CPPS.COD_CHECKLIST_PARA_SINCRONIZAR
                                                LIMIT 1);
    ELSE
        UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        SET NEXT_TO_SYNC = TRUE
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = (SELECT COD_CHECKLIST_PARA_SINCRONIZAR
                                                FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
                                                WHERE SINCRONIZADO IS FALSE
                                                  AND PRECISA_SER_SINCRONIZADO IS TRUE
                                                  AND NEXT_TO_SYNC IS FALSE
                                                  AND COD_CHECKLIST_PARA_SINCRONIZAR > COD_CHECKLIST
                                                ORDER BY COD_CHECKLIST_PARA_SINCRONIZAR
                                                LIMIT 1);
    END IF;

    --   6° - Retorna o código que será sincronizado
    RETURN QUERY
        SELECT COD_CHECKLIST, IS_LAST_COD;
END;
$$;
end transaction;