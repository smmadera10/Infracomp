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
	private int numeroActualClientes;

	/**
	 * Lista de mensajes recibidos por el buffer
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
	
	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------

	public Buffer(int numeroClientes, int numeroServidores, int capacidad)
	{
		this.numeroClientes = numeroClientes;
		this.numeroServidores = numeroServidores;
		this.capacidad = capacidad;
		clientes = new ArrayList<Cliente>(numeroClientes);
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
		for(int i = 0; i < numeroServidores - 1; i++)
		{
			servidores.add( new Servidor(i, this) );
		}
		
		//Crea clientes
		for(int i = 0; i < numeroClientes - 1; i++)
		{
			clientes.add( new Cliente(i, this) );
		}
		
		//Inicializa los threads de clientes
		for(int i = 0; i < clientes.size() - 1; i++)
		{
			clientes.get(i).start();
		}
		
		//Inicializa los threads de servidores
		for(int i = 0; i < servidores.size() - 1; i++)
		{
			servidores.get(i).start();
		}
	}
	
	/**
	 * M�todo llamado por un cliente para avisar que termin� de enviar todos los
	 * mensajes que deb�a y todos ellos fueron recibidos correctamente.
	 */
	synchronized public void retirarCliente()
	{
		//Nota: Es synchronized para garantizar la integridad de los datos, es 
		//decir, para que no hayan lecturas sucias		numeroActualClientes--;
		
		if (numeroActualClientes == 0)
		{
			terminarServidores();
			//TODO terminar ejecuci�n de servidores y aplicaci�n
		}
	}
	
	/**
	 * M�todo que llama un Cliente para enviar un mensaje al buffer
	 * @param mensaje objeto de tipo Mensaje que env�a el cliente
	 * @return true si fue posible almacenar el mensaje, false de lo contrario
	 */
	synchronized public boolean recibirMensaje(Mensaje mensaje)
	{
		//Nota: no se sabe si es necesario que sea synchronized. Se pone
		//synchronized por defecto como medida de seguridad a la hora de
		//implementar.
		
		if (listaMensajes.size() < capacidad)
		{
			listaMensajes.add(mensaje);
			return true;
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
		
		if (!listaMensajes.isEmpty())
		{
			rta = listaMensajes.get(0);
			listaMensajes.remove(0);
		}
		
		return rta;
	}
	
	/**
	 * M�todo que se encarga de terminar todos los threads
	 */
	public void terminarServidores()
	{
		//TODO
	}
}
