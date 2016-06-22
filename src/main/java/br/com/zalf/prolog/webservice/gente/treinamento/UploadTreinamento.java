package br.com.zalf.prolog.webservice.gente.treinamento;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.PutObjectRequest;

import br.com.zalf.prolog.models.treinamento.Treinamento;
import br.com.zalf.prolog.webservice.util.AmazonS3Utils;
import br.com.zalf.prolog.webservice.util.L;

public class UploadTreinamento {
	private static final String TAG = UploadTreinamento.class.getSimpleName();
	private static final String BUCKET_TREINAMENTOS = "treinamentos-prolog";
	private boolean enviou = false;

	public boolean doIt(
			Treinamento treinamento,
			InputStream fileInputStream) {
		// Data do upload é a data atual do servidor
		treinamento.setDataLiberacao(new Date(System.currentTimeMillis()));
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
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
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
			e.printStackTrace();
			return false;
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " +
					"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			L.e(TAG, "Erro ao enviar arquivo", ase);
			return false;
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			L.e(TAG, "Erro ao enviar arquivo", ace);
			return false;
		}
		
		// Deu tudo certo
		treinamento.setUrlArquivo(AmazonS3Utils.generateFileUrl(BUCKET_TREINAMENTOS, fileName));
		return true;
	}
}
