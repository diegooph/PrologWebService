package br.com.zalf.prolog.webservice.treinamento;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;

import br.com.zalf.prolog.models.treinamento.Treinamento;
import br.com.zalf.prolog.webservice.util.AmazonS3Utils;
import br.com.zalf.prolog.webservice.util.L;

public class UploadTreinamento implements ProgressListener {
	private static final String TAG = UploadTreinamento.class.getSimpleName();
	private static final String BUCKET_TREINAMENTOS = "treinamentos-prolog";
	private boolean enviou = false;

	public boolean doIt(
			Treinamento treinamento,
			InputStream fileInputStream) {
		// Data do upload é a data atual do servidor
		treinamento.setDataLiberacao(new Date(System.currentTimeMillis()));
		try {
			// Salva o arquivo
			// FIXME: fileName não pode ser algo genérico porque se outra pessoa enviar pode 
			// ser que substitua enquanto está ainda usando o arquivo
			String fileName =  TreinamentoHelper.createFileName(treinamento);
			L.d(TAG, "File name: " + fileName);

			// Pasta temporária da JVM
			File tmpDir = new File(System.getProperty("java.io.tmpdir"), "treinamentos");
			if (!tmpDir.exists()) {
				// Cria a pasta treinamentos se não existe
				tmpDir.mkdir();
			}

			// Cria o arquivo
			File file = new File(tmpDir, fileName);
			FileOutputStream out = new FileOutputStream(file);
			IOUtils.copy(fileInputStream, out);
			IOUtils.closeQuietly(out);

			L.d(TAG, "Iniciando upload");
			PutObjectRequest putObjectRequest = 
					AmazonS3Utils.createPutObjectRequest(
							BUCKET_TREINAMENTOS, 
							fileName, 
							file);
			putObjectRequest.setGeneralProgressListener(this);
			AmazonS3Utils.putObject(putObjectRequest);
			
			if (enviou) {
				treinamento.setUrlArquivo(AmazonS3Utils.generateFileUrl(BUCKET_TREINAMENTOS, fileName));
				return true;
			}
			
		} catch (IOException e) {
			L.e(TAG, "Erro ao enviar arquivo do treinamento", e);
			return false;
		}
		
		return false;
	}

	@Override
	public void progressChanged(ProgressEvent progressEvent) {
		switch (progressEvent.getEventCode()) {
		case ProgressEvent.COMPLETED_EVENT_CODE:
			L.d(TAG, "Completou o envio");
			this.enviou = true;
			break;
		case ProgressEvent.FAILED_EVENT_CODE:
			L.e(TAG, "Falha no envio");
			this.enviou = false;
			break;
		}
	}
}
