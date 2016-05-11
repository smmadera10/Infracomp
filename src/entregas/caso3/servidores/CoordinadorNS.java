package entregas.caso3.servidores;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoordinadorNS {
	
	/**
	 * Constante que especifica el numero de threads que se usan en el pool de conexiones.
	 */
	public static final int N_THREADS = 16;
	
	private static final String MAESTRO = "MAESTRO: ";
	
	/**
	 * Puerto en el cual escucha el servidor.
	 */
	public static final int PUERTO = 6500;
	
	/**
	 * El id del Thread
	 */
	private int id;
	
	private static ServerSocket ss;	
	ExecutorService threadPool=Executors.newFixedThreadPool(N_THREADS); 
	
	public void run() {
		openServerSocket();
		System.out.println("El servidor esta listo para aceptar conexiones.");
		while (true) {
			
			Socket s = null;
			// ////////////////////////////////////////////////////////////////////////
			// Recibe una conexion del socket.
			// ////////////////////////////////////////////////////////////////////////

			try {
				s = ss.accept();		
	
				
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
			ss=new ServerSocket(PUERTO);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
		
		int idThread = 0;
		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println(MAESTRO + "Socket creado.");
		
		while (true) {
			try { 
				// Crea un delegado por cliente. Atiende por conexion. 
				Socket sc = ss.accept();
				System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");
				DelegadoNS d = new DelegadoNS(sc,idThread);
				idThread++;
				d.start();
			} catch (IOException e) {
				System.out.println(MAESTRO + "Error creando el socket cliente.");
				e.printStackTrace();
			}
		}
	}
}