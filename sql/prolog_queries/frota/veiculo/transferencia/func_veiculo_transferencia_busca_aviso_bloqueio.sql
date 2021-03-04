-- Sobre:
-- Busca o aviso de bloqueio que deve ser exibido na tela de transferência de veículos para uma empresa específica.
-- Caso a empresa não tenha bloqueado a deleção de OS de check ou serviços de pneus por conta de transferência,
-- uma mensagem default é retornada.
--
-- Histórico:
-- 2019-09-16 -> Function criada (luizfp - PL-2083)
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