
public class Servidor extends Thread {

	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Respuesta que el servidor da por defecto a una solicitud si no hay
	 * otra respuesta especificada
	 */
	private final static String RESPUESTA_POR_DEFECTO = "VALE T�O";

	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------

	/**
	 * id del servidor
	 */
	private int id;

	/**
	 * El canal del que el servidor tomar� los mensajes para responderlos
	 */
	private Buffer canal;

	/**
	 * Mensaje que el servidor est� respondiendo actualmente
	 */
	private Mensaje mensajeActual;

	/**
	 * True si hay clientes, false de lo contrario. Mientras haya clientes, el
	 * servidor seguir� ejecut�ndose. 
	 */
	private boolean hayClientes;

	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------

	public Servidor(int id, Buffer canal)
	{
		this.id = id;
		this.canal = canal;
		mensajeActual = null;
		hayClientes = true;
	}

	//-------------------------------------------------------------------------------
	//M�todos
	//-------------------------------------------------------------------------------

//	/**
//	 * M�todo que llama el buffer para avisarle al servidor que no hay m�s clientes
//	 * que atender, y que puede terminar su ejecuci�n
//	 */
//	synchronized public void recibirNoHayClientes()
//	{
//		System.out.println("El servidor " + id + " recibi� la orden de terminar");
//
//		this.interrupt();
//		synchronized(this)
//		{
//			System.out.println("El servidor " + id + " recibi� la orden de terminar");
//			hayClientes = false;
//		}
//	}
	/**
	 * M�todo que se encarga de solicitar un mensaje del buffer y almcenarlo como
	 * mensajeActual
	 */
	synchronized private void solicitarMensaje()
	{
		//Nota: Es synchronized para evitar que el mismo mensaje sea enviado como
		//respuesta a dos o m�s servidores que est�n solicitando al mismo tiempo.
		mensajeActual = canal.solicitarMensaje();
	}

	/**
	 * M�todo que se encarga de enviar una respuesta al mensaje que se 
	 * est� respondiendo actualmente
	 */
	synchronized private void responderMensaje()
	{
		mensajeActual.recibirRespuesta(RESPUESTA_POR_DEFECTO);
		mensajeActual = null;
	}

	public Mensaje darMensajeActual()
	{
		return mensajeActual;
	}


	public void run()
	{
		while(hayClientes)
		{
			hayClientes = canal.darHayClientes();
			
			while(mensajeActual == null && hayClientes)
			{
				//actualizo hayClientes para que no se quede atrapado en el loop
				hayClientes = canal.darHayClientes();
				solicitarMensaje();
				yield();
			}
			
			if(mensajeActual != null && hayClientes)
			{
				//actualizo hayClientes para que no se quede atrapado en el loop
				hayClientes = canal.darHayClientes();

				synchronized(mensajeActual)
				{
					responderMensaje();
				}
			}
		}

		System.err.println("El servidor " + id + " termin� su ejecuci�n exitosamente"); //TODO
	}

}
