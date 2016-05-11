package entregas.caso3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.x509.X509V3CertificateGenerator;


public class AgenteCaso3 {

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

	/**
	 * Contiene la cadena para declarar un estado al servidor
	 */
	private final static String ESTADO = "ESTADO:";

	/**
	 * Contiene la cadena que comunica a servidor que se encuentra en el estado "Ok"
	 */
	private final static String ESTADO_OK = ESTADO + "OK";

	/**
	 * Contiene la cadena que comunica a servidor que se encuentra en el estado "Error"
	 */
	private final static String ESTADO_ERROR = ESTADO + "ERROR";

	/**
	 * Cadena con la que el servidor ha de indicar el inicio correcto de la comunicación
	 */
	private final static String INICIO = "INICIO";

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

	/**
	 * Modela el par de llaves que el agente está utilizando en un momento del tiempo
	 */
	private KeyPair parLlaves;

	/**
	 * Llave pública del agente en un momento del tiempo
	 */
	private PublicKey publicKey;

	/**
	 * Llave privada dl agente en un momento del tiempo
	 */
	private PrivateKey privateKey;

	int bytesRecibidos;

	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------

	public AgenteCaso3(boolean usaSeguridad) throws Exception
	{
		generarParLlaves();
		iniciarComunicacion();
		if(usaSeguridad)
		{
			realizarComunicacionConSeguridad();
		}
		else
		{
			realizarComunicacionSinSeguridad();
		}
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
	 * Realiza la comunicación con seguridad descrita por el enunciado del caso 2
	 * @throws Exception 
	 */
	private void realizarComunicacionConSeguridad() throws Exception
	{
		//Se usan para medir el tiempo de respuesta de diferentes transacciones
		float tiempo1 = 0;
		float tiempo2 = 0;

		pw.println("HOLA");

		if (! recibirRespuesta().equals(INICIO) )
		{
			ReporteTransaccion.getInstance().transaccionPerdida();
			terminarComunicacion();
		}

		pw.println("ALGORITMOS:" + ALGORITMO_SIMETRICO + ":RSA:" + HMAC);

		recibirRespuesta(); 

		tiempo1 = System.nanoTime();
		X509Certificate cert = generarCertificadoDigital();
		byte[] certEncoded = cert.getEncoded();

		pw.println("CERCLNT:");
		intermediario.getOutputStream().write(certEncoded);
		intermediario.getOutputStream().flush();

		System.err.println("funcionó hasta el envío del cert");

		recibirRespuesta();

		//		byte[] bytes = new byte[520];
		//		InputStream bis = intermediario.getInputStream(); 
		//		bis.read(bytes, 0, 520);
		//		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		//		InputStream in = new ByteArrayInputStream(bytes);
		//		X509Certificate certServidor = (X509Certificate)certFactory.generateCertificate(in);
		//		System.out.println(Transformacion.transformar(bytes) ); 

		PublicKey llavePublicaServidor = null;
		IndicadorTransaccion tr1 = null;
		try
		{
			X509Certificate certServidor = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(intermediario.getInputStream());
			llavePublicaServidor = certServidor.getPublicKey();

			System.out.println("cogió el certificado");

			pw.println(ESTADO_OK);
			tiempo2 = System.nanoTime();

			//Tiempo transacción autenticación

			float tiempoEnSegundos = (tiempo2 - tiempo1) / 1000000000;
			tr1 = new IndicadorTransaccion(IndicadorTransaccion.TIPO_AUTENTICACION, tiempoEnSegundos);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			pw.println(ESTADO_ERROR);
			return;
		}

		//Obtener y descifrar la llave simétrica
		String cadenaLlaveEncriptada = recibirRespuesta().split(":")[1];
		byte[] llaveDescifrada = descifrar(Transformacion.destransformar(cadenaLlaveEncriptada), privateKey, "RSA");
		SecretKey llaveSimetrica = new SecretKeySpec(llaveDescifrada, ALGORITMO_SIMETRICO);

		//Cifrar la llave simétrica y enviarla de vuelta
		byte[] llaveRecifrada = cifrar(llaveDescifrada, llavePublicaServidor, "RSA");

		pw.println("DATA:" + Transformacion.transformar(llaveRecifrada) );

		recibirRespuesta();

		//ACT1
		String posicion = "41 24.2028";
		byte[] posicionCifrada = cifrar(posicion.getBytes(), llaveSimetrica, ALGORITMO_SIMETRICO);
		pw.println("ACT1:" + Transformacion.transformar(posicionCifrada));
		tiempo1 = System.nanoTime();

		//ACT2
		SecretKey llaveSim2 = new SecretKeySpec(llaveSimetrica.getEncoded(), "HMACSHA1");
		byte[] hashPosicion = generarSecureHash(posicion.getBytes(), llaveSim2, "HMACSHA1");
		byte[] hashPosicionCifrado = cifrar(hashPosicion, privateKey, "RSA");

		pw.println("ACT2:" + Transformacion.transformar(hashPosicionCifrado) );

		recibirRespuesta();
		tiempo2 = System.nanoTime();

		//Tiempo transacción actualización
		float ts = (tiempo2 - tiempo1) / 1000000000;

		ReporteTransaccion.getInstance().agregarTransaccion(tr1);
		ReporteTransaccion.getInstance().agregarTransaccion(new IndicadorTransaccion(IndicadorTransaccion.TIPO_ACTUALIZACION, ts));
		terminarComunicacion();

	}
	
	/**
	 * Realiza la comunicación sin seguridad descrita por el enunciado del caso 2
	 * @throws Exception 
	 */
	private void realizarComunicacionSinSeguridad() throws Exception
	{
		//Se usan para medir el tiempo de respuesta de diferentes transacciones
		float tiempo1 = 0;
		float tiempo2 = 0;

		pw.println("HOLA");

		if (! recibirRespuesta().equals(INICIO) )
		{
			ReporteTransaccion.getInstance().transaccionPerdida();
			terminarComunicacion();
		}

		pw.println("ALGORITMOS:" + ALGORITMO_SIMETRICO + ":RSA:" + HMAC);

		recibirRespuesta(); 

		tiempo1 = System.nanoTime();
		X509Certificate cert = generarCertificadoDigital();
		byte[] certEncoded = cert.getEncoded();

		pw.println("CERCLNT:");
		intermediario.getOutputStream().write(certEncoded);
		intermediario.getOutputStream().flush();

		System.err.println("funcionó hasta el envío del cert");

		recibirRespuesta();

		//		byte[] bytes = new byte[520];
		//		InputStream bis = intermediario.getInputStream(); 
		//		bis.read(bytes, 0, 520);
		//		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		//		InputStream in = new ByteArrayInputStream(bytes);
		//		X509Certificate certServidor = (X509Certificate)certFactory.generateCertificate(in);
		//		System.out.println(Transformacion.transformar(bytes) ); 

		IndicadorTransaccion tr1 = null;
		
		try
		{
//			X509Certificate certServidor = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(intermediario.getInputStream());
//			llavePublicaServidor = certServidor.getPublicKey();

			System.out.println("cogió el certificado");

			pw.println(ESTADO_OK);
			tiempo2 = System.nanoTime();

			//Tiempo transacción autenticación

			float tiempoEnSegundos = (tiempo2 - tiempo1) / 1000000000;
			tr1 = new IndicadorTransaccion(IndicadorTransaccion.TIPO_AUTENTICACION, tiempoEnSegundos);
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
			pw.println(ESTADO_ERROR);
			return;
		}

		recibirRespuesta();

		pw.println("DATA");

		recibirRespuesta();

		//ACT1
		pw.println("ACT1");
		tiempo1 = System.nanoTime();

		//ACT2
		pw.println("ACT2");
		recibirRespuesta();
		tiempo2 = System.nanoTime();

		
		//Tiempo transacción actualización
		float ts = (tiempo2 - tiempo1) / 1000000000;
		
		ReporteTransaccion.getInstance().agregarTransaccion(tr1);
		ReporteTransaccion.getInstance().agregarTransaccion(new IndicadorTransaccion(IndicadorTransaccion.TIPO_ACTUALIZACION, ts));
		terminarComunicacion();

	}

	private byte[] generarSecureHash(byte[] data, SecretKey llave, String algoritmo) throws NoSuchAlgorithmException, InvalidKeyException
	{
		Mac mc = Mac.getInstance(algoritmo);
		mc.init(llave);
		mc.update(data);
		return mc.doFinal();
	}

	/**
	 * Genera el certificado digital con el estándar X509 versión 3 del agente y lo retorna
	 * @return certificado digital de versión 3 del agente que sigue el estándar X509
	 * @throws CertificateEncodingException
	 * @throws InvalidKeyException
	 * @throws IllegalStateException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 */
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
		certGen.setNotAfter(new Date(2017,1,1));
		certGen.setPublicKey(publicKey);
		certGen.setSignatureAlgorithm("SHA1withRSA");

		X509Certificate cert = certGen.generate(privateKey);

		return cert;
	}

	/**
	 * Descifra el arreglo de bytes provisto utilizando el algoritmo especificado usando
	 * la llave ingresada por parámetro y retorna el resultado.
	 * @param datosADescifrar arreglo de bytes a descifrar
	 * @param llave llave con la que se intentará descifrar el arreglo
	 * @return arreglo de bytes descifrado
	 * 
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] descifrar(byte[] datosADescifrar, Key llave, String algoritmo) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{
		//	byte[] hex = Hex.decode(datosADescifrar);
		Cipher c = Cipher.getInstance(algoritmo); 
		c.init(Cipher.DECRYPT_MODE, llave); 
		return c.doFinal(datosADescifrar);
	}

	/**
	 * Cifra el arreglo de bytes provisto utilizando el algoritmo especificadp usando
	 * la llave ingresada por parámetro y retorna el resultado.
	 * @param datosACifrar arreglo de bytes a cifrar
	 * @param llave llave con la que se realizará el cifrado
	 * @return arreglo de bytes cifrado
	 * 
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public byte[] cifrar(byte[] datosACifrar, Key llave, String algoritmo) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException
	{
		Cipher c = Cipher.getInstance(algoritmo);
		c.init(Cipher.ENCRYPT_MODE, llave);
		return c.doFinal(datosACifrar);
	}

	/**
	 * Genera un par de llaves, pública y privada, y las guarda para su posterior uso
	 * por parte del agente
	 * @throws NoSuchAlgorithmException
	 */
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
	private String recibirRespuesta() throws IOException
	{
		String rta = br.readLine();

		if(rta != null)
		{
			if(rta.equals(ESTADO_ERROR))
			{
				System.err.println("Error reportado por el servidor. Terminando conexión.");
				ReporteTransaccion.getInstance().transaccionPerdida();
				terminarComunicacion();
			}
			else
			{
				System.out.println("Respuesta recibida: " + rta);
			}
		}
		else
		{
			System.out.println("No se ha recibido ninguna respuesta");
		}
		return rta;
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
