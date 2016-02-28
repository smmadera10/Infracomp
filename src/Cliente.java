import java.util.ArrayList;
import java.util.Random;

public class Cliente extends Thread implements Comparable<Cliente> {

	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Constante que modela la cantidad máxima de mensajes que una instancia
	 * de Cliente podrá enviar durante su ciclo de vida. Se limita esta cantidad
	 * para que la ejecución del programa se termine en un tiempo razonable.
	 */
	private final static int MAX_MENSAJES = 10;

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
		canal.retirarCliente(this);
	}

	/**
	 * Método que es llamado para procesar la respuesta recibida a un
	 * cierto mensaje
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
	}

	/**
	 * Método que se encarga de enviar un mensaje al buffer. Si es recibido
	 * por el buffer exitosamente, incrementa el número de mensajes enviados.
	 * 
	 * @return true si el mensaje fue recibido por el buffer y almacenado 
	 *  	exitosamente. False de lo contrario.
	 */
	private boolean enviarMensaje()
	{
		Mensaje mensaje = new Mensaje(id + ":" + mensajesEnviados, this);
		boolean rta = canal.recibirMensaje(mensaje);

		if(rta)
		{
			mensajesEnviados++;
		}

		return rta;
	}
	
	public int darId()
	{
		return id;
	}

	public int darNumeroMensajesAEnviar()
	{
		return numeroMensajesAEnviar;
	}
	
	public int darMensajesRespondidos()
	{
		return mensajesRespondidos;
	}
	
	public int darMensajesEnviados()
	{
		return mensajesEnviados;
	}
	public void run()
	{
		
		while(mensajesRespondidos < numeroMensajesAEnviar)
		{
			boolean respuestaAUltimoMensajeEnviado = enviarMensaje();
			
			while( !respuestaAUltimoMensajeEnviado )
			{
				System.err.println("El cliente " + id + " está intentado enviar mensajes");
				respuestaAUltimoMensajeEnviado = enviarMensaje();
				yield();
			}
			
			//wait() debe usarse en un bloque de código sincronizado
			synchronized(this)
			{
				try 
				{
					wait();
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			recibirRespuestaMensaje();
			
			System.out.println("Se han respondido " + mensajesRespondidos + " para el cliente de id " + id); //TODO
		}
		
		terminar();
	}

	public int compareTo(Cliente c) {
		return id - c.darId();
	}

}
