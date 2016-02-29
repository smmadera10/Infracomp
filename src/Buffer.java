import java.util.ArrayList;

public class Buffer {

	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------

	/**
	 * Modela el número inicial de clientes que han de existir durante la 
	 * ejecución.
	 */
	private int numeroClientes;

	/**
	 * Modela el número de servidores que existirán durante la ejecución.
	 */
	private int numeroServidores;

	/**
	 * Modela la capacidad del buffer para almacenar mensajes producidos
	 * por los clientes.
	 */
	private int capacidad;

	/**
	 * Contiene el número de clientes que existen durante cualquier determinado
	 * de la ejecución.
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

	/**
	 * Lista de clientes inicializados por el buffer
	 */
	private ArrayList<Cliente> clientes;
	
	private long start; //F

	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------

	public Buffer(int numeroClientes, int numeroServidores, int capacidad)
	{
		start = System.nanoTime();
		this.numeroClientes = numeroClientes;
		this.numeroServidores = numeroServidores;
		this.capacidad = capacidad;
		numeroActualClientes = numeroClientes;
		clientes = new ArrayList<Cliente>(numeroClientes);
		servidores = new ArrayList<Servidor>(numeroServidores);
		listaMensajes = new ArrayList<Mensaje>(this.capacidad);

		comenzarEjecucion();
	}

	//-------------------------------------------------------------------------------
	//Métodos
	//-------------------------------------------------------------------------------

	/**
	 * Método que se encarga de crear a los clientes y servidores y comenzar
	 * la ejecución del programa
	 */
	private void comenzarEjecucion()
	{
		//Crea y almacena referencias a los servidores
		for(int i = 0; i < numeroServidores; i++)
		{
			servidores.add( new Servidor(i, this) );
		}

		//Crea clientes
		for(int i = 0; i < numeroClientes; i++)
		{
			clientes.add( new Cliente(i, this) );
		}

		//Inicializa los threads de clientes
		for(int i = 0; i < clientes.size(); i++)
		{
			clientes.get(i).start();
		}

		//Inicializa los threads de servidores
		for(int i = 0; i < servidores.size(); i++)
		{
			servidores.get(i).start();
		}
	}

	/**
	 * Método llamado por un cliente para avisar que terminó de enviar todos los
	 * mensajes que debía y todos ellos fueron recibidos correctamente.
	 */
	synchronized public void retirarCliente(Cliente c)
	{
		synchronized(this)
		{
			//Nota: Es synchronized para garantizar la integridad de los datos, es 
			//decir, para que no hayan lecturas sucias

			for (int i = 0; i < clientes.size(); i++)
			{
				Cliente c1 = clientes.get(i);
				if (c1.compareTo(c) == 0)
				{
					clientes.remove(i);
				}
			}

			numeroActualClientes--;
			System.out.println("numero actual clientes: " + numeroActualClientes); //TODO
		}


		//					if (numeroActualClientes == 0)
		//					{
		//						terminarServidores();
		//					}



	}

	/**
	 * Método que llama un Cliente para enviar un mensaje al buffer
	 * @param mensaje objeto de tipo Mensaje que envía el cliente
	 * @return true si fue posible almacenar el mensaje, false de lo contrario
	 */
	synchronized public boolean recibirMensaje(Mensaje mensaje)
	{
		//Nota: no se sabe si es necesario que sea synchronized. Se pone
		//synchronized por defecto como medida de seguridad a la hora de
		//implementar.
		synchronized(listaMensajes)
		{
			synchronized(this)
			{
				if (listaMensajes.size() < capacidad)
				{
					listaMensajes.add(mensaje);
					System.out.println("cantidad mensajes guardados: " + listaMensajes.size());//TODO
					return true;
				}
			}

		}
		return false;
	}
		/**
		 * Método que un servidor llamda para solicitar un mensaje del buffer.
		 * Retorna un mensaje si hay alguno almacenado en la lista. De lo contrario,
		 * retorna null
		 * @return un mensaje si la lista de mensajes no está vacía. Null de lo 
		 * 			contrario.
		 */
		synchronized public Mensaje solicitarMensaje()
		{
			//F ->
			long actual = System.nanoTime();
			if( (actual - start) / 1000000000 > 10)
			{
				System.exit(0);
			}
			
			//-> F
			Mensaje rta = null;
			synchronized(listaMensajes)
			{
				if (!listaMensajes.isEmpty())
				{
					rta = listaMensajes.get(0);
					listaMensajes.remove(0);
				}
			}

//					if(numeroActualClientes == 1)
//						imprimirEstadoAplicacion(); //TODO
			return rta;

		}

		//Para debugging:
		//TODO
		public void imprimirEstadoAplicacion()
		{
			System.out.println("hay " + listaMensajes.size() + " mensajes almacenados por responder");
			System.out.println("Quedan " + numeroActualClientes + " clientes.");
			System.out.println("Los clientes almacenados que quedan son: " + clientes.size());
			System.out.println("Los clientes restantes son:");

			for(int i = 0; i < clientes.size(); i++)
			{
				Cliente actual = clientes.get(i);
				System.out.println("El cliente " + actual.darId() + ", con " + actual.darMensajesEnviados() + 
						" mensajes enviados y \n"
						+ ( actual.darNumeroMensajesAEnviar() - actual.darMensajesRespondidos() ) + " mensajes"
						+ " que faltan por respuesta");
			}
			System.out.println("Hay " + listaMensajes.size() + " mensajes sin responder.");
			System.out.println("Los servidores están respondiendo actualmente los mensajes de siguiente id:");

			for (int i = 0; i < servidores.size(); i++)
			{
				Mensaje mActual = servidores.get(i).darMensajeActual();
				if (mActual != null)
				{
					System.out.println("Servidor " + i + " está respondiendo " + mActual.darId());
				}
				else
				{
					System.out.println("Servidor " + i + " no está respondiendo ningún mensaje");
				}
			}
		}

		public boolean darHayClientes()
		{
			return numeroActualClientes > 0;
		}

		//	/**
		//	 * Método que se encarga de avisarles a los threads Servidor que procedan con
		//	 * su terminación
		//	 */
		//	public void terminarServidores()
		//	{
		//	//	System.exit(0);
		//		System.out.println("El buffer mandó a los servers a terminar");
		//		for(int i = 0; i < servidores.size(); i++)
		//		{
		//			System.out.println("corre el for con server " + i);
		//			//servidores.get(i).stop();
		//		}
		//	}
	}
