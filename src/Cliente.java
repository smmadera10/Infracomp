import java.util.Random;

public class Cliente extends Thread {

	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Constante que modela la cantidad máxima de mensajes que una instancia
	 * de Cliente podrá enviar durante su ciclo de vida. Se limita esta cantidad
	 * para que la ejecución del programa se termine en un tiempo razonable.
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
	 * Número de mensajes que enviará el Cliente durante su ciclo de vida. 
	 * Su valor está entre 1 y MAX_MENSAJES.
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
	 * determinado momento de la ejecución
	 */
	private int mensajesEnviados;
	
	/**
	 * canal al que el cliente mandará sus mensajes
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
	//Métodos
	//-------------------------------------------------------------------------------

	/**
	 * Método que se llama cuando el Cliente ha terminado de enviar todos los
	 * mensajes y ha recibido respuesta para cada uno.
	 */
	public void terminar()
	{
		canal.retirarCliente();
	}
	
	/**
	 * Método que es llamado por un objeto Mensaje que este cliente ha producido
	 * para informarle que se recibió una respuesta. 
	 */
	
	synchronized public void recibirRespuestaMensaje()
	{
		//Nota: es synchronized porque, de recibir más de una respuesta a un mensaje
		//al mismo tiempo, podrían haber inconsistencias en la información. 
		//Por ejemplo, el tercer mensaje es respondido al mismo tiempo que el cuatro. 
		//Si no fuera synchronized, el tercer mensaje diría que, contándose a sí mismo,
		//han habido tres mensajes respondidos. Ya que esta información no se ha 
		//registrado para cuando el cuarto es respondido, el cuarto mensaje también
		//dice que, contándose a sí mismo, se han respondido tres mensajes. En
		//otras palabras, ambos tomarían el valor de mensajesRespondidos de 2 y lo
		//incrementaría hasta 3, por lo que quedaría registrado un 3 para el Cliente,
		//a pesar de que 4 mensajes suyos han sido respondidos.
		
		mensajesRespondidos++;
		
		if(mensajesRespondidos == numeroMensajesAEnviar)
		{
			canal.retirarCliente();
			//a continuación, esta instancia termina su ejecución
		}
	}
	
	/**
	 * Método que se encarga de enviar un mensaje al buffer
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
