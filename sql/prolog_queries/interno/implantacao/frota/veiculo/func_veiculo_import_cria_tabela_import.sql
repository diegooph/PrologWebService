-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Cria uma tabela de "pré-import" e aplica uma trigger para verificar os dados inseridos e importar o que estiver de
-- acordo com as verificações.
--
-- Pré-requisitos:
-- Func remove_all_apaces criada.
--
-- Histórico:
-- 2019-10-31 -> Function criada (thaisksf - PL-2318).
-- 2020-01-17 -> Corrige SQL de grant de permissão (luizfp).
-- 2020-05-19 -> Adiciona a coluna identificador_frota na tabela de import (thaisksf - PL-2712).
-- 2020-05-25 -> Remove FK cod_unidade (thaisksf - PL-2711).
-- 2020-05-26 -> Adiciona cod_empresa na criação da tabela (thaisksf - PL-2711).
-- 2020-07-21 -> Altera grant de permissões na tabela criada para conceder ao usuário recebido (luiz_fp - PL-2830).
-- 2020-11-23 -> Adiciona possui_hubodometro no insert da planilha (thaisksf - PL-3288).
create or replace function implantacao.func_veiculo_import_cria_tabela_import(f_cod_empresa bigint,
                                                                              f_cod_unidade bigint,
                                                                              f_usuario text,
                                                                              f_data date,
                                                                              out nome_tabela_criada text)
    returns text
    language plpgsql
as
$$
declare
    v_dia                text := (select EXTRACT(day from f_data));
    v_mes                text := (select EXTRACT(month from f_data));
    v_ano                text := (select EXTRACT(year from f_data)) ;
    v_nome_tabela_import text := LOWER(remove_all_spaces(
                                        'V_COD_EMP_' || f_cod_empresa || '_COD_UNIDADE_' || f_cod_unidade || '_' ||
                                        v_ano || '_' || v_mes ||
                                        '_' || v_dia || '_' || f_usuario));
begin
    execute FORMAT(
            'CREATE TABLE IF NOT EXISTS IMPLANTACAO.%I (
            CODIGO                       BIGSERIAL,
            COD_DADOS_AUTOR_IMPORT       BIGINT,
            COD_EMPRESA                  BIGINT,
            COD_UNIDADE                  BIGINT,
            PLACA_EDITAVEL               VARCHAR(255),
            KM_EDITAVEL                  BIGINT,
            MARCA_EDITAVEL               VARCHAR(255),
            MODELO_EDITAVEL              VARCHAR(255),
            TIPO_EDITAVEL                VARCHAR(255),
            QTD_EIXOS_EDITAVEL           VARCHAR(255),
            IDENTIFICADOR_FROTA_EDITAVEL VARCHAR(15),
            POSSUI_HUBODOMETRO_EDITAVEL  VARCHAR(15),
            PLACA_FORMATADA_IMPORT       VARCHAR(255),
            MARCA_FORMATADA_IMPORT       VARCHAR(255),
            MODELO_FORMATADO_IMPORT      VARCHAR(255),
            TIPO_FORMATADO_IMPORT        VARCHAR(255),
            IDENTIFICADOR_FROTA_IMPORT   VARCHAR(15),
            STATUS_IMPORT_REALIZADO      BOOLEAN,
            ERROS_ENCONTRADOS            VARCHAR(255),
            USUARIO_UPDATE               VARCHAR(255),
            PRIMARY KEY (CODIGO),
            FOREIGN KEY (COD_DADOS_AUTOR_IMPORT) REFERENCES IMPLANTACAO.DADOS_AUTOR_IMPORT (CODIGO)
        );', v_nome_tabela_import);

    -- Trigger para verificar planilha e realizar o import de veículos.
    execute FORMAT('DROP TRIGGER IF EXISTS TG_FUNC_IMPORT_VEICULO ON IMPLANTACAO.%I;
                   CREATE TRIGGER TG_FUNC_IMPORT_VEICULO
                    BEFORE INSERT OR UPDATE
                        ON IMPLANTACAO.%I
                    FOR EACH ROW
                   EXECUTE PROCEDURE IMPLANTACAO.TG_FUNC_VEICULO_CONFERE_PLANILHA_IMPORTA_VEICULO();',
                   v_nome_tabela_import,
                   v_nome_tabela_import);

    -- Cria audit para a tabela.
    execute format('DROP TRIGGER IF EXISTS TG_FUNC_AUDIT_IMPORT_VEICULO ON IMPLANTACAO.%I;
                    CREATE TRIGGER TG_FUNC_AUDIT_IMPORT_VEICULO
                    AFTER UPDATE OR DELETE
                    ON IMPLANTACAO.%I
                    FOR EACH ROW
                    EXECUTE PROCEDURE AUDIT_IMPLANTACAO.FUNC_AUDIT_IMPLANTACAO();',
                   v_nome_tabela_import,
                   v_nome_tabela_import);

    -- Garante select, update para o usuário que está realizando o import.
    execute FORMAT(
            'grant select, update on implantacao.%I to %I;',
            v_nome_tabela_import,
            (select interno.func_busca_nome_usuario_banco(f_usuario)));

    -- Retorna nome da tabela.
    select v_nome_tabela_import into nome_tabela_criada;
end ;
$$;