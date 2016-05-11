package entregas.caso3.servidores;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyPair;
import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Coordinador {

	/**
	 * Constante que especifica el tiempo máximo en milisegundos que se esperara 
	 * por la respuesta de un cliente en cada una de las partes de la comunicación
	 */
	private static final int TIME_OUT = 1000;

	/**
	 * Constante que especifica el numero de threads que se usan en el pool de conexiones.
	 */
	public static final int N_THREADS = 16;

	/**
	 * Puerto en el cual escucha el servidor.
	 */
	public static final int PUERTO = 6500;

	/**
	 * El socket que permite recibir requerimientos por parte de clientes.
	 */
	private static ServerSocket socket;

	/**
	 * El id del Thread
	 */
	private int id;
	
	private static ServerSocket ss;	
	private static final String MAESTRO = "MAESTRO: ";
	static java.security.cert.X509Certificate certSer; /* acceso default */
	static KeyPair keyPairServidor; /* acceso default */
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		System.out.println(MAESTRO + "Establezca puerto de conexion:");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		int ip = Integer.parseInt(br.readLine());
		System.out.println(MAESTRO + "Empezando servidor maestro en puerto " + ip);
		// Adiciona la libreria como un proveedor de seguridad.
		// Necesario para crear llaves.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		
		
		int idThread = 0;
		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println(MAESTRO + "Socket creado.");
		
		keyPairServidor = S.grsa();
		certSer = S.gc(keyPairServidor);
		while (true) {
			try { 
				// Crea un delegado por cliente. Atiende por conexion. 
				Socket sc = ss.accept();
				System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");
				Delegado d = new Delegado(sc,idThread);
				idThread++;
				d.start();
			} catch (IOException e) {
				System.out.println(MAESTRO + "Error creando el socket cliente.");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Metodo que inicializa un thread y lo pone a correr.
	 * 
	 * @param socket
	 *            El socket por el cual llegan las conexiones.
	 * @param semaphore
	 *            Un semaforo que permite dar turnos para usar el socket.
	 * @throws InterruptedException
	 *             Si hubo un problema con el semaforo.
	 * @throws SocketException 
	 */
	
	ExecutorService threadPool=Executors.newFixedThreadPool(N_THREADS); 
	boolean usaSeguridad = true;
	public static int atentidos=0;
       
	public Coordinador(boolean _usaSeguridad) throws  SocketException 
	{
	
		usaSeguridad=_usaSeguridad;
		
		
	}
	

	/**
	 * Metodo que atiende a los usuarios.
	 */
	
	public void run() {
		openServerSocket();
		System.out.println("El servidor esta listo para aceptar conexiones.");
		System.out.println("Seguridad"+usaSeguridad);
		while (true) {
			
			Socket s = null;
			// ////////////////////////////////////////////////////////////////////////
			// Recibe una conexion del socket.
			// ////////////////////////////////////////////////////////////////////////

			try {
				s = socket.accept();		
	
				
			} 
			catch(Exception e)
			{
				System.out.println("Error aceptando la conexion del cliente");
				e.printStackTrace();
				
			}
			
			threadPool.execute(new Delegado(s,id));		
			System.out.println("Servidor recibe a un cliente.");
			}
			
			
	}

	private void openServerSocket()
	{
		try {
			socket=new ServerSocket(PUERTO);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
