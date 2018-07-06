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
	RAIZEN_PRODUTIVIDADE(8);

	private final int errorCode;

	ProLogErrorCodes(int errorCode) {
		this.errorCode = errorCode;
	}

	public int errorCode() {
		return errorCode;
	}
}