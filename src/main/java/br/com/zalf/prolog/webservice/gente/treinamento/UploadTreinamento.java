package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.gente.treinamento.Treinamento;
import br.com.zalf.prolog.webservice.util.AmazonS3Utils;
import br.com.zalf.prolog.webservice.util.L;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.UnknownHostException;
import java.util.Date;

public class UploadTreinamento {

	private static final String TAG = UploadTreinamento.class.getSimpleName();
	private static final String BUCKET_TREINAMENTOS = "treinamentos-prolog";

	public boolean doIt(
			Treinamento treinamento,
			InputStream fileInputStream) {
		// Data do upload é a data atual do servidor
		treinamento.setDataHoraCadastro(new Date(System.currentTimeMillis()));
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
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			IOUtils.copy(fileInputStream, out);
			IOUtils.closeQuietly(out);
		} catch (FileNotFoundException e) {
			L.e(TAG, "Arquivo não encontrado", e);
			return false;
		} catch (IOException e) {
			L.e(TAG, "Erro ao salvar arquivo", e);
			return false;
		}

		L.d(TAG, "Iniciando upload");
		PutObjectRequest putObjectRequest = 
				AmazonS3Utils.createPutObjectRequest(
						BUCKET_TREINAMENTOS, 
						fileName, 
						file);
		try {
			AmazonS3Utils.putObject(putObjectRequest);
		} catch (UnknownHostException e) {
			L.e(TAG, "Erro ao enviar arquivo", e);
			return false;
		} catch (AmazonServiceException ase) {
			L.e(TAG, "Caught an AmazonServiceException, which " +
					"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.", ase);
			return false;
		} catch (AmazonClientException ace) {
			L.e(TAG, "Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.", ace);
			return false;
		}
		
		// Deu tudo certo
		treinamento.setUrlArquivo(AmazonS3Utils.generateFileUrl(BUCKET_TREINAMENTOS, fileName));
		return true;
	}
}