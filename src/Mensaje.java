
public class Mensaje {

	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Mensaje que el objeto contiene en caso de que no se especifique ningún
	 * otro. Este será el mensaje que todos los objetos Mensaje contendrán 
	 * durante el caso 1.
	 */
	private final static String MENSAJE_POR_DEFECTO = "Vamos Snake, sólo sígueme";

	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------

	/**
	 * id del mensaje
	 */
	private String id;

	/**
	 * Cliente que remitió el mensaje al servidor.
	 */
	private Cliente remitente;

	/**
	 * Mensaje que fue remitido al servidor. 
	 */
	private String mensaje;

	/**
	 * Respuesta que fue recibida por parte del servidor
	 */
	private String respuesta;

	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------

	/**
	 * Constructor que asigna al mensaje del objeto el mensaje por defecto.
	 * @param remitente creador del objeto Mensaje
	 */
	public Mensaje(String id, Cliente remitente)
	{
		this.id = id;
		this.remitente = remitente;
		mensaje = MENSAJE_POR_DEFECTO;
		respuesta = "";
	}

	/**
	 * Constructor que asigna al mensaje del objeto el mensaje ingresado por 
	 * parámetro.
	 * @param remitente creador del objeto Mensaje
	 * @param mensaje mensaje que contendrá el objeto
	 */
	public Mensaje(String id, Cliente remitente, String mensaje)
	{
		this.id = id;
		this.remitente = remitente;
		this.mensaje = mensaje;
		respuesta = "";
	}

	//-------------------------------------------------------------------------------
	//Métodos
	//-------------------------------------------------------------------------------

	/**
	 * Método con el que un servidor puede responder el mensaje. El mensaje, a
	 * su vez, avisará a su cliente remitente que una respuesta fue recibida.
	 * @param respuesta respuesta recibida por parte del servidor
	 */
	synchronized public void recibirRespuesta(String respuesta)
	{
		this.respuesta = respuesta;

		enviarRespuestaARemitente();
	}

	/**
	 * Método con el que el objeto de tipo Mensaje le avisa a su remitente que 
	 * una respuesta fue recibida
	 */
	synchronized private void enviarRespuestaARemitente()
	{
		//notify() debe usarse en un bloque de código sincronizado
		synchronized(this)
		{
			synchronized(remitente)
			{
				remitente.notify();
			}
		}
	}

	public String darId()
	{
		return id;
	}

}
