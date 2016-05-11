package entregas.caso3.servidores;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;

public class Delegado extends Thread {
	
	// ----------------------------------------------------
	// CONSTANTES DE CONTROL DE IMPRESION EN CONSOLA
	// ----------------------------------------------------
	public static final boolean SHOW_ERROR = true;
	public static final boolean SHOW_S_TRACE = false;
	public static final boolean SHOW_IN = false;
	public static final boolean SHOW_OUT = false;
	
	
	// Constantes
	public static final String STATUS = "ESTADO";
	public static final String OK = "OK";
	public static final String ALGORITMOS = "ALGORITMOS";
	public static final String HMACMD5 = "HMACMD5";
	public static final String HMACSHA1 = "HMACSHA1";
	public static final String HMACSHA256 = "HMACSHA256";
	public static final String CERTSRV = "CERTSRV";
	public static final String CERCLNT = "CERCLNT";
	public static final String SEPARADOR = ":";
	public static final String HOLA = "HOLA";
	public static final String INICIO = "INICIO";
	public static final String ERROR = "ERROR";
	public static final String ERRORPRT = "ERROR EN PROTOCOLO:";
	public static final String REC = "recibio-";
	// Atributos
	private Socket sc = null;
	private String dlg;
	
	Delegado (Socket csP, int idP) {
		sc = csP;
		dlg = new String("dlg " + idP + ": ");
	}
	
	public void run() {
		String me = new String(STATUS+SEPARADOR+ERROR);
		String mok = new String(STATUS+SEPARADOR+OK);
		String mt;
		String linea;
	    System.out.println(dlg + "Empezando atencion.");
	        try {

				PrintWriter ac = new PrintWriter(sc.getOutputStream() , true);
				BufferedReader dc = new BufferedReader(new InputStreamReader(sc.getInputStream()));

				/***** Fase 1: Inicio *****/
				linea = dc.readLine();
				if (!linea.equals(HOLA)) {
					ac.println(me);
			        sc.close();
					throw new Exception(dlg + ERRORPRT + REC + linea +"-terminando.");
				}
				System.out.println(dlg + REC + linea + "-continuando.");
				ac.println(INICIO);

				/***** Fase 2: Algoritmos *****/
				linea = dc.readLine();
				if (!(linea.contains(SEPARADOR) && linea.split(SEPARADOR)[0].equals(ALGORITMOS))) {
					ac.println(me);
					sc.close();
					throw new Exception(dlg + ERRORPRT + REC + linea +"-terminando.");
				}
				
				String[] algoritmos = linea.split(SEPARADOR);
				if (!algoritmos[1].equals(S.DES) && !algoritmos[1].equals(S.AES) &&
					!algoritmos[1].equals(S.BLOWFISH) && !algoritmos[1].equals(S.RC4)){
					ac.println(STATUS+SEPARADOR+ERROR);
					sc.close();
					throw new Exception(dlg + ERRORPRT + "Alg.Simetrico" + REC + algoritmos + "-terminando.");
				}
				if (!algoritmos[2].equals(S.RSA)) {
					ac.println(me);
					sc.close();
					throw new Exception(dlg + ERRORPRT + "Alg.Asimetrico." + REC + algoritmos + "-terminando.");
				}
				if (!(algoritmos[3].equals(HMACMD5) || algoritmos[3].equals(HMACSHA1) ||
					  algoritmos[3].equals(HMACSHA256))) {
					ac.println(me);
					sc.close();
					throw new Exception(dlg + ERRORPRT + "AlgHash." + REC + algoritmos + "-terminando.");
				}
				System.out.println(dlg + REC + linea + "-continuando.");
				ac.println(mok);

				/***** Fase 3: Recibe certificado del cliente *****/
				linea = dc.readLine();
				mt = new String(CERCLNT + SEPARADOR);
				if (!(linea.equals(mt))) {
					ac.println(me);
					sc.close();
					throw new Exception(dlg + ERRORPRT + "CERCLNT." + REC + linea + "-terminando.");
				}
				System.out.println(dlg + REC + mt + "-continuando.");
				int offset = 0;
				byte[] certificadoServidorBytes = new byte[520];
				int numBytesLeidos = sc.getInputStream().read(certificadoServidorBytes,offset,520-offset);
				if (numBytesLeidos<=0) {
					ac.println(me);
					sc.close();
					throw new Exception(dlg + "Error recibiendo certificado. terminando.");					
				}
				CertificateFactory creador = CertificateFactory.getInstance("X.509");
				InputStream in = new ByteArrayInputStream(certificadoServidorBytes);
				X509Certificate certificadoCliente = (X509Certificate)creador.generateCertificate(in);
				System.out.println(dlg + "recibio certificado del cliente. continuando.");
				
				/***** Fase 4: Envia certificado del servidor *****/
				mt= new String(CERTSRV + SEPARADOR);
				ac.println(mt);
				byte[] mybyte = Coordinador.certSer.getEncoded( );
				sc.getOutputStream( ).write( mybyte );
				sc.getOutputStream( ).flush( );
				System.out.println(dlg + "envio certificado del servidor. continuando.");
				linea = dc.readLine();
				if (!(linea.equals(mok))) {
					ac.println(me);
					throw new Exception(dlg + ERRORPRT + REC + linea + "-terminando.");
				}
				System.out.println(dlg + "recibio-" + linea + "-continuando.");

				/***** Fase 5: Envia llave simetrica *****/
				SecretKey simetrica = S.kgg(algoritmos[1]);
				byte [ ] ciphertext1 = S.ae(simetrica.getEncoded(), 
						                 certificadoCliente.getPublicKey(), algoritmos[2]);
				ac.println("DATA:" + Transformacion.transformar(ciphertext1));
				System.out.println(dlg + "envio llave simetrica al cliente. continuado.");
				
				/***** Fase 6: Confirma llave simetrica *****/
				linea = dc.readLine();
				byte[] llaveS = S.ad(
						Transformacion.destransformar(linea.split(SEPARADOR)[1]), Coordinador.keyPairServidor.getPrivate(), algoritmos[2]);
				if (!Transformacion.transformar(llaveS).equals(Transformacion.transformar(simetrica.getEncoded()))) {
					ac.println(me);
					throw new Exception(dlg + "Error confirmando llave. terminando.");
				}
				ac.println(mok);
				
				/***** Fase 7: Actualizacion del agente *****/
				linea = dc.readLine();
				if (!(linea.contains(SEPARADOR) && linea.split(SEPARADOR)[0].equals("ACT1"))) {
					ac.println(me);
					throw new Exception(dlg + "Error en ACT1. terminando.");
				}
				String datos = new String(S.sd(
						Transformacion.destransformar( linea.split(SEPARADOR)[1]), simetrica, algoritmos[1]));
				linea = dc.readLine();
				if (!(linea.contains(SEPARADOR) && linea.split(SEPARADOR)[0].equals("ACT2"))) {
					ac.println(me);
					throw new Exception(dlg + "Error en ACT2. terminando.");
				}
				System.out.println(dlg + "descifro informacion-" + datos +  "-continuado.");
				byte[] hmac = S.ad(
						Transformacion.destransformar( linea.split(SEPARADOR)[1]), 
						certificadoCliente.getPublicKey(), algoritmos[2]);
				boolean verificacion = S.verificarIntegridad(
						                 datos.getBytes(), simetrica, algoritmos[3], hmac);
				if (verificacion) {
					System.out.println(dlg + "verificacion de integridad:OK. -continuado.");
					ac.println(mok);
				} else {
					ac.println(me);
					throw new Exception(dlg + "Error en verificacion de integridad. -terminando.");
				}
		        sc.close();
		        System.out.println(dlg + "Termino exitosamente.");
				
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	}
}
