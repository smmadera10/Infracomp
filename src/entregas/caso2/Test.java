package entregas.caso2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;


public class Test {

	
	public static void main(String[] args) throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, SignatureException
	{
		System.out.println("corrió");
		Agente a = new Agente();
	}
	
}
