package br.com.zalf.prolog.webservice.errorhandling.exception;

public enum ProLogErrorCodes {

	AMAZON_CREDENTIALS(1),
	VERSAO_DADOS_INTERVALO_DESATUALIZADA(2),
	TIPO_AFERICAO_NAO_SUPORTADO(3),
	INTEGRACAO(5);
	TIPO_AFERICAO_NAO_SUPORTADO(3),
	ESCALA_DIARIA(4);

	private final int errorCode;

	ProLogErrorCodes(int errorCode) {
		this.errorCode = errorCode;
	}

	public int errorCode() {
		return errorCode;
	}
}