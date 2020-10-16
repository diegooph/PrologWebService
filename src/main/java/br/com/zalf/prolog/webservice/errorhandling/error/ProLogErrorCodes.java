package br.com.zalf.prolog.webservice.errorhandling.error;

public enum ProLogErrorCodes {
	GENERIC(0),
	AMAZON_CREDENTIALS(1),
	VERSAO_DADOS_INTERVALO_DESATUALIZADA(2),
	TIPO_AFERICAO_NAO_SUPORTADO(3),
	ESCALA_DIARIA(4),
	INTEGRACAO(5),
	RECAPADORA_EXCEPTION(6),
	RECURSO_JA_EXISTE(7),
	VERSAO_APP_BLOQUEADA(8),
	BLOQUEADO_INTEGRACAO(9),
	VEICULO_SEM_DIAGRAMA(10),
	NOT_AUTHORIZED(11),
	MULTIPLES_AUTHORIZATIONS_HEADERS(12);

	private final int errorCode;

	ProLogErrorCodes(int errorCode) {
		this.errorCode = errorCode;
	}

	public int errorCode() {
		return errorCode;
	}
}