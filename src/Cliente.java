import java.util.Random;

public class Cliente extends Thread {

	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Constante que modela la cantidad m�xima de mensajes que una instancia
	 * de Cliente podr� enviar durante su ciclo de vida. Se limita esta cantidad
	 * para que la ejecuci�n del programa se termine en un tiempo razonable.
	 */
	private final static int MAX_MENSAJES = 100;

	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------

	/**
	 * Identificador entero del Cliente
	 */
	private int id;
	
	/**
	 * N�mero de mensajes que enviar� el Cliente durante su ciclo de vida. 
	 * Su valor est� entre 1 y MAX_MENSAJES.
	 * 1 <= numeroMensajes <= MAX_MENSAJES
	 */
	private int numeroMensajesAEnviar;

	/**
	 * Cantidad de mensajes que han sido respondidos al cliente por los 
	 * servidores.
	 */
	private int mensajesRespondidos;
	
	/**
	 * Cantidad de mensajes que han sido enviados al servidor en un 
	 * determinado momento de la ejecuci�n
	 */
	private int mensajesEnviados;
	
	/**
	 * canal al que el cliente mandar� sus mensajes
	 */
	private Buffer canal;

	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------

	/**
	 * @param id id del cliente, no es necesario en la arquitectura actual (caso 1),
	 * 			pero se agrega por seguridad.
	 * @param canal
	 */
	public Cliente(int id, Buffer canal)
	{
		super();
		this.id = id;
		this.canal = canal;
		mensajesRespondidos = 0;
		mensajesEnviados = 0;
		numeroMensajesAEnviar = ( new Random() ).nextInt(MAX_MENSAJES) + 1;
	}

	//-------------------------------------------------------------------------------
	//M�todos
	//-------------------------------------------------------------------------------

	/**
	 * M�todo que se llama cuando el Cliente ha terminado de enviar todos los
	 * mensajes y ha recibido respuesta para cada uno.
	 */
	public void terminar()
	{
		canal.retirarCliente();
	}
	
	/**
	 * M�todo que es llamado por un objeto Mensaje que este cliente ha producido
	 * para informarle que se recibi� una respuesta. 
	 */
	
	synchronized public void recibirRespuestaMensaje()
	{
		//Nota: es synchronized porque, de recibir m�s de una respuesta a un mensaje
		//al mismo tiempo, podr�an haber inconsistencias en la informaci�n. 
		//Por ejemplo, el tercer mensaje es respondido al mismo tiempo que el cuatro. 
		//Si no fuera synchronized, el tercer mensaje dir�a que, cont�ndose a s� mismo,
		//han habido tres mensajes respondidos. Ya que esta informaci�n no se ha 
		//registrado para cuando el cuarto es respondido, el cuarto mensaje tambi�n
		//dice que, cont�ndose a s� mismo, se han respondido tres mensajes. En
		//otras palabras, ambos tomar�an el valor de mensajesRespondidos de 2 y lo
		//incrementar�a hasta 3, por lo que quedar�a registrado un 3 para el Cliente,
		//a pesar de que 4 mensajes suyos han sido respondidos.
		
		mensajesRespondidos++;
		
		if(mensajesRespondidos == numeroMensajesAEnviar)
		{
			canal.retirarCliente();
			//a continuaci�n, esta instancia termina su ejecuci�n
		}
	}
	
	/**
	 * M�todo que se encarga de enviar un mensaje al buffer
	 */
	private boolean enviarMensaje()
	{
		Mensaje mensaje = new Mensaje(mensajesEnviados, this);
		boolean rta = canal.recibirMensaje(mensaje);
		return rta;
	}
	
	public void run()
	{
		//TODO
	}

}
