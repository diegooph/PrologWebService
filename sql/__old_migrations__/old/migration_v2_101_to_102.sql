BEGIN TRANSACTION ;

-- Já lançado, comentado para histórico.
-- CREATE TRIGGER TG_FUNC_AUDIT_PNEU_RESTRICAO_UNIDADE
--   AFTER INSERT OR UPDATE OR DELETE
--   ON PNEU_RESTRICAO_UNIDADE
--   FOR EACH ROW EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();

--######################################################################################################################
--######################################################################################################################
--####################### CRIA TABELA VEICULO_TRANSFERENCIA_EMPRESA_BLOQUEADA_FECHAMENTO_OS ############################
--######################################################################################################################
--######################################################################################################################
-- PL-2083
CREATE TABLE IF NOT EXISTS VEICULO_TRANSFERENCIA_BLOQUEIO_DELECAO_OS
(
    COD_EMPRESA                       BIGINT  NOT NULL,
    BLOQUEAR_DELECAO_OS_CHECKLIST     BOOLEAN NOT NULL,
    BLOQUEAR_DELECAO_SERVICOS_PNEU    BOOLEAN NOT NULL,
    AVISO_BLOQUEIO_TELA_TRANSFERENCIA TEXT    NOT NULL,
    CONSTRAINT PK_EMPRESA_BLOQUEADA_FECHAMENTO_OS_TRANSFERENCIA PRIMARY KEY (COD_EMPRESA),
    CONSTRAINT FK_VEICULO_TRANSFERENCIA_EMPRESA_BLOQUEADA_FECHAMENTO_OS_EMPRESA
        FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA (CODIGO),
    CONSTRAINT CHECK_PELO_MENOS_UM_BLOQUEADO CHECK (BLOQUEAR_DELECAO_SERVICOS_PNEU OR BLOQUEAR_DELECAO_OS_CHECKLIST)
);

COMMENT ON TABLE VEICULO_TRANSFERENCIA_BLOQUEIO_DELECAO_OS IS 'Tabela com a função de armazenar as empresas
onde a deleção automática de OS de check e serviços de pneu não acontecerá sempre que uma transferência de veículos
for realizada.';

CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_BUSCA_AVISO_BLOQUEIO(F_COD_EMPRESA BIGINT)
    RETURNS TABLE
            (
                COD_EMPRESA                       BIGINT,
                BLOQUEAR_DELECAO_OS_CHECKLIST     BOOLEAN,
                BLOQUEAR_DELECAO_SERVICOS_PNEU    BOOLEAN,
                AVISO_BLOQUEIO_TELA_TRANSFERENCIA TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_BLOQUEAR_DELECAO_OS_CHECKLIST     BOOLEAN;
    F_BLOQUEAR_DELECAO_SERVICOS_PNEU    BOOLEAN;
    F_AVISO_BLOQUEIO_TELA_TRANSFERENCIA TEXT;
    F_AVISO_DEFAULT                     TEXT := '<b>Atenção!</b> Ao transferir um veículo, todas as OSs de ' ||
                                                'checklist e serviços de pneus em aberto serão automaticamente ' ||
                                                'removidos. Isso garante que eles não ficarão em um estado de ' ||
                                                'inconsistência e que não levarão histórico de problemas para uma ' ||
                                                'nova unidade, assim prevenimos que relatórios e dashboards sejam ' ||
                                                'comprometidos.';
BEGIN
      SELECT VTBD.BLOQUEAR_DELECAO_OS_CHECKLIST,
             VTBD.BLOQUEAR_DELECAO_SERVICOS_PNEU,
             VTBD.AVISO_BLOQUEIO_TELA_TRANSFERENCIA
      FROM VEICULO_TRANSFERENCIA_BLOQUEIO_DELECAO_OS VTBD
      WHERE VTBD.COD_EMPRESA = F_COD_EMPRESA
      INTO
          F_BLOQUEAR_DELECAO_OS_CHECKLIST,
          F_BLOQUEAR_DELECAO_SERVICOS_PNEU,
          F_AVISO_BLOQUEIO_TELA_TRANSFERENCIA;

      IF (F_BLOQUEAR_DELECAO_OS_CHECKLIST,
          F_BLOQUEAR_DELECAO_SERVICOS_PNEU,
          F_AVISO_BLOQUEIO_TELA_TRANSFERENCIA) IS NULL
      THEN
          RETURN QUERY
              SELECT F_COD_EMPRESA,
                     FALSE,
                     FALSE,
                     F_AVISO_DEFAULT;
      ELSE
          RETURN QUERY
              SELECT F_COD_EMPRESA,
                     F_BLOQUEAR_DELECAO_OS_CHECKLIST,
                     F_BLOQUEAR_DELECAO_SERVICOS_PNEU,
                     F_AVISO_BLOQUEIO_TELA_TRANSFERENCIA;
      END IF;

END;
$$;

INSERT INTO veiculo_transferencia_bloqueio_delecao_os (cod_empresa,
                                                       bloquear_delecao_os_checklist,
                                                       bloquear_delecao_servicos_pneu,
                                                       aviso_bloqueio_tela_transferencia)
VALUES (4,
        true,
        false,
        '<b>Atenção!</b> Por conta da integração entre o ProLog e o Transport, ao realizar a transferência as OSs de checklist não serão removidas, apenas os serviços em aberto dos pneus.');
--######################################################################################################################
--######################################################################################################################
END TRANSACTION ;