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
	//Métodos
	//-------------------------------------------------------------------------------

	/**
	 * Método que se encarga de crear a los clientes y servidores y comenzar
	 * la ejecución del programa
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
	 * Método llamado por un cliente para avisar que terminó de enviar todos los
	 * mensajes que debía y todos ellos fueron recibidos correctamente.
	 */
	synchronized public void retirarCliente()
	{
		//Nota: Es synchronized para garantizar la integridad de los datos, es 
		//decir, para que no hayan lecturas sucias		numeroActualClientes--;
		
		if (numeroActualClientes == 0)
		{
			terminarServidores();
			//TODO terminar ejecución de servidores y aplicación
		}
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
		
		if (listaMensajes.size() < capacidad)
		{
			listaMensajes.add(mensaje);
			return true;
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
		Mensaje rta = null;
		
		if (!listaMensajes.isEmpty())
		{
			rta = listaMensajes.get(0);
			listaMensajes.remove(0);
		}
		
		return rta;
	}
	
	/**
	 * Método que se encarga de terminar todos los threads
	 */
	public void terminarServidores()
	{
		//TODO
	}
}
