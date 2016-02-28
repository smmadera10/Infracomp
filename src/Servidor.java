
public class Servidor extends Thread {

	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Respuesta que el servidor da por defecto a una solicitud si no hay
	 * otra respuesta especificada
	 */
	private final static String RESPUESTA_POR_DEFECTO = "VALE TÍO";

	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------

	/**
	 * id del servidor
	 */
	private int id;

	/**
	 * El canal del que el servidor tomará los mensajes para responderlos
	 */
	private Buffer canal;

	/**
	 * Mensaje que el servidor está respondiendo actualmente
	 */
	private Mensaje mensajeActual;

	/**
	 * True si hay clientes, false de lo contrario. Mientras haya clientes, el
	 * servidor seguirá ejecutándose. 
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
	//Métodos
	//-------------------------------------------------------------------------------

//	/**
//	 * Método que llama el buffer para avisarle al servidor que no hay más clientes
//	 * que atender, y que puede terminar su ejecución
//	 */
//	synchronized public void recibirNoHayClientes()
//	{
//		System.out.println("El servidor " + id + " recibió la orden de terminar");
//
//		this.interrupt();
//		synchronized(this)
//		{
//			System.out.println("El servidor " + id + " recibió la orden de terminar");
//			hayClientes = false;
//		}
//	}
	/**
	 * Método que se encarga de solicitar un mensaje del buffer y almcenarlo como
	 * mensajeActual
	 */
	synchronized private void solicitarMensaje()
	{
		//Nota: Es synchronized para evitar que el mismo mensaje sea enviado como
		//respuesta a dos o más servidores que estén solicitando al mismo tiempo.
		mensajeActual = canal.solicitarMensaje();
	}

	/**
	 * Método que se encarga de enviar una respuesta al mensaje que se 
	 * está respondiendo actualmente
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

		System.err.println("El servidor " + id + " terminó su ejecución exitosamente"); //TODO
	}

}
