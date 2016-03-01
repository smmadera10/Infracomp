import java.util.ArrayList;

public class Buffer {

	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------

	/**
	 * Modela el n�mero inicial de clientes que han de existir durante la 
	 * ejecuci�n.
	 */
	private int numeroClientes;

	/**
	 * Modela el n�mero de servidores que existir�n durante la ejecuci�n.
	 */
	private int numeroServidores;

	/**
	 * Modela la capacidad del buffer para almacenar mensajes producidos
	 * por los clientes.
	 */
	private int capacidad;

	/**
	 * Contiene el n�mero de clientes que existen durante cualquier determinado
	 * de la ejecuci�n.
	 */
	volatile private int numeroActualClientes;

	/**
	 * Lista de mensajes pendientes de respuesta recibidos por el buffer
	 */
	private ArrayList<Mensaje> listaMensajes;

	/**
	 * Lista de servidores inicializados por el Buffer
	 */
	private ArrayList<Servidor> servidores;

	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------

	public Buffer(int numeroClientes, int numeroServidores, int capacidad)
	{
		this.numeroClientes = numeroClientes;
		this.numeroServidores = numeroServidores;
		this.capacidad = capacidad;
		numeroActualClientes = numeroClientes;
		servidores = new ArrayList<Servidor>(numeroServidores);
		listaMensajes = new ArrayList<Mensaje>(this.capacidad);

		comenzarEjecucion();
	}

	//-------------------------------------------------------------------------------
	//M�todos
	//-------------------------------------------------------------------------------

	/**
	 * M�todo que se encarga de crear a los clientes y servidores y comenzar
	 * la ejecuci�n del programa
	 */
	private void comenzarEjecucion()
	{
		//Crea y almacena referencias a los servidores
		for(int i = 0; i < numeroServidores; i++)
		{
			servidores.add( new Servidor(i, this) );
		}

		//Inicializa los threads de clientes
		for(int i = 0; i < numeroClientes; i++)
		{
			( new Cliente (i, this) ).start();
		}

		//Inicializa los threads de servidores
		for(int i = 0; i < servidores.size(); i++)
		{
			servidores.get(i).start();
		}
	}

	/**
	 * M�todo llamado por un cliente para avisar que termin� de enviar todos los
	 * mensajes que deb�a y todos ellos fueron recibidos correctamente.
	 */
	synchronized public void retirarCliente(Cliente c)
	{
		synchronized(this)
		{
			//Nota: Es synchronized para garantizar la integridad de los datos, es 
			//decir, para que no hayan lecturas sucias

			numeroActualClientes--;

			System.out.println("numero actual clientes: " + numeroActualClientes); //TODO
		}
	}

	/**
	 * M�todo que llama un Cliente para enviar un mensaje al buffer
	 * @param mensaje objeto de tipo Mensaje que env�a el cliente
	 * @return true si fue posible almacenar el mensaje, false de lo contrario
	 */
	synchronized public boolean recibirMensaje(Mensaje mensaje)
	{
		synchronized(listaMensajes)
		{
			synchronized(this)
			{
				if (listaMensajes.size() < capacidad)
				{
					System.out.println("cantidad mensajes guardados: " + listaMensajes.size());//TODO
					listaMensajes.add(mensaje);
					return true;
				}
			}
		}

		return false;
	}
	/**
	 * M�todo que un servidor llamda para solicitar un mensaje del buffer.
	 * Retorna un mensaje si hay alguno almacenado en la lista. De lo contrario,
	 * retorna null
	 * @return un mensaje si la lista de mensajes no est� vac�a. Null de lo 
	 * 			contrario.
	 */
	synchronized public Mensaje solicitarMensaje()
	{
		Mensaje rta = null;

		synchronized(listaMensajes)
		{
			if (!listaMensajes.isEmpty())
			{
				rta = listaMensajes.get(0);
				listaMensajes.remove(0);
			}
		}

		return rta;
	}

	//Para debugging:
	//TODO
//	public void imprimirEstadoAplicacion()
//	{
//		System.out.println("hay " + listaMensajes.size() + " mensajes almacenados por responder");
//		System.out.println("Quedan " + numeroActualClientes + " clientes.");
//		System.out.println("Los clientes almacenados que quedan son: " + clientes.size());
//		System.out.println("Los clientes restantes son:");
//
//		for(int i = 0; i < clientes.size(); i++)
//		{
//			Cliente actual = clientes.get(i);
//			System.out.println("El cliente " + actual.darId() + ", con " + actual.darMensajesEnviados() + 
//					" mensajes enviados y \n"
//					+ ( actual.darNumeroMensajesAEnviar() - actual.darMensajesRespondidos() ) + " mensajes"
//					+ " que faltan por respuesta");
//		}
//		System.out.println("Hay " + listaMensajes.size() + " mensajes sin responder.");
//		System.out.println("Los servidores est�n respondiendo actualmente los mensajes de siguiente id:");
//
//		for (int i = 0; i < servidores.size(); i++)
//		{
//			Mensaje mActual = servidores.get(i).darMensajeActual();
//			if (mActual != null)
//			{
//				System.out.println("Servidor " + i + " est� respondiendo " + mActual.darId());
//			}
//			else
//			{
//				System.out.println("Servidor " + i + " no est� respondiendo ning�n mensaje");
//			}
//		}
//	}

	public boolean darHayClientes()
	{
		return numeroActualClientes > 0;
	}
}
