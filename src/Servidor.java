
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
	
	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------

	public Servidor(int id, Buffer canal)
	{
		this.id = id;
		this.canal = canal;
		mensajeActual = null;
	}
	
	//-------------------------------------------------------------------------------
	//Métodos
	//-------------------------------------------------------------------------------

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
	private void responderMensaje()
	{
		mensajeActual.recibirRespuesta(RESPUESTA_POR_DEFECTO);
	}
	
	public void run()
	{
		while(mensajeActual == null)
		{
			solicitarMensaje();
		}
		
		//TODO
	}

}
