package entregas.caso2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.x509.X509V3CertificateGenerator;


public class Agente {

	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Contiene la IP a la que se intentará conectar el agente
	 */
	private final static String IP_SERVIDOR = "127.0.0.1";

	/**
	 * Contiene el puerto de la IP a la que se intentará conectar el agente
	 */
	private final static int PUERTO_SERVIDOR = 6500; 

	/**
	 * Constante representando el algoritmo que fue escogido para el cifrado simétrico
	 * con el servidor.
	 */
	private final static String ALGORITMO_SIMETRICO = "DES";

	/**
	 * Constante representando el tipo mensaje de autenticación con función criptográfica
	 * de hash (HMAC) que se utilizará en la comunicación con el servidor
	 */
	private final static String HMAC = "HMACSHA1";

	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------

	/**
	 * Socket que se conectará al servidor en el IP y el puerto contenidos en las constantes
	 */
	private Socket intermediario;

	/**
	 * Lector para la información enviada por el servidor
	 */
	private BufferedReader br;

	/**
	 * Emisor de información hacia el servidor
	 */
	private PrintWriter pw;

	private KeyPair parLlaves;

	private PublicKey publicKey;

	private PrivateKey privateKey;

	private X509Certificate certificadoServidor;

	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------

	public Agente() throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, SignatureException
	{
		//generarLlavePublica();
		generarParLlaves();
		iniciarComunicacion();
		realizarComunicacion();
	}

	//-------------------------------------------------------------------------------
	//Métodos
	//-------------------------------------------------------------------------------

	/**
	 * Inicia la conexión con el servidor en el ip y el puerto guardados como constantes
	 * @throws IOException si no encuentra el servidor o hay otro problema
	 */
	private void iniciarComunicacion() throws IOException
	{
		intermediario = new Socket(IP_SERVIDOR, PUERTO_SERVIDOR);
		br = new BufferedReader(new InputStreamReader(intermediario.getInputStream()));
		pw = new PrintWriter(intermediario.getOutputStream(), true);

		System.out.println("Se inició la conexión con el servidor");
	}

	/**
	 * Realiza la comunicación descrita 
	 * @throws IOException
	 * @throws CertificateException 
	 * @throws SignatureException 
	 * @throws NoSuchAlgorithmException 
	 * @throws IllegalStateException 
	 * @throws InvalidKeyException 
	 */
	private void realizarComunicacion() throws IOException, CertificateException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException
	{
		pw.println("HOLA");

		recibirRespuesta();

		pw.println("ALGORITMOS:" + ALGORITMO_SIMETRICO + ":RSA:" + HMAC);

		recibirRespuesta(); 

		System.out.println("va a correr cert");
		//		X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
		//				.generateCertificate(intermediario.getInputStream());
		//
		//		System.out.println("corrió cert");
		//byte[] certByte = cert.getEncoded();
		//
		//System.out.println("codeó cert");

		X509Certificate cert = generarCertificadoDigital();
		byte[] certEncoded = cert.getEncoded();

		System.err.println("funcionó hasta el envío del cert");
		pw.println("CERCLNT:");
		pw.println( certEncoded );

		recibirRespuesta();

		X509Certificate cert2 = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(intermediario.getInputStream());
		
//		byte[] aux = new byte[1000];
//		int leidos = intermediario.getInputStream().read(aux, 0, 1000);
//		byte[] cadenaCertificado = new byte[leidos];
//		for ( int i = 0; i < leidos; i++ )
//		{
//			cadenaCertificado[i] = aux[i];
//		}
//		System.out.println("pasó por to byte array");
//		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
//		InputStream in = new ByteArrayInputStream(cadenaCertificado);
//		System.out.println(Transformacion.transformar(cadenaCertificado));
//		certificadoServidor = (X509Certificate)certFactory.generateCertificate(in);
//		certificadoServidor.getPublicKey();

		terminarComunicacion();
	}

	private X509Certificate generarCertificadoDigital() throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException
	{
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		X500Principal nombre = new X500Principal("CN=Test V3 Certificate");
		BigInteger serialAleatorio = new BigInteger( 10, new Random() );

		//Configuración fecha actual y 
		Date fechaActual = new Date();

		//Configuración del generador del certificado
		certGen.setSerialNumber(serialAleatorio);
		certGen.setIssuerDN(nombre);
		certGen.setSubjectDN(nombre);
		certGen.setNotBefore(fechaActual);
		certGen.setNotAfter(fechaActual);
		certGen.setPublicKey(publicKey);
		certGen.setSignatureAlgorithm("SHA1withRSA");

		X509Certificate cert = certGen.generate(privateKey);

		return cert;
	}

	private void generarParLlaves() throws NoSuchAlgorithmException
	{
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
		keygen.initialize(1024);
		parLlaves = keygen.generateKeyPair();
		publicKey = parLlaves.getPublic();
		privateKey = parLlaves.getPrivate();
	}

	/**
	 * Recibe la respuesta del servidor que está almacenada en el lector
	 * @throws IOException
	 */
	private void recibirRespuesta() throws IOException
	{
		System.out.println("Respuesta recibida: " + br.readLine());
	}

	/**
	 * Termina la conexión con el servidor
	 * @throws IOException
	 */
	private void terminarComunicacion() throws IOException
	{
		intermediario.close();
		System.out.println("Se terminó la conexión con el servidor");
	}

}
