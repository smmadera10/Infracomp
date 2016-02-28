
public class LogisticaAeroportuaria {

	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Constante que modela el nombre del archivo que especifica el número
	 * de clientes, número de servidores y la capacidad del Buffer para la
	 * ejecución
	 */
	private final static String NOMBRE_ARCHIVO_PROPIEDADES = "";
	
	/**
	 * Constante que contiene la dirección local del archivo que especifica 
	 * el número de clientes, número de servidores y la capacidad del Buffer 
	 * para la ejecución
	 */
	private final static String DIRECCION_ARCHIVO_PROPIEDADES = "";
	
	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------

	/**
	 * Modela el número inicial de clientes que han de existir durante la 
	 * ejecución. Este atributo es pasado al buffer
	 */
	private int numeroClientes;
	
	/**
	 * Modela el número de servidores que existirán durante la ejecución.
	 * Este atributo es pasado al buffer.
	 */
	private int numeroServidores;
	
	/**
	 * Modela la capacidad del buffer para almacenar mensajes producidos
	 * por los clientes. Este atributo es pasado al buffer.
	 */
	private int capacidad;
	
	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------

	public LogisticaAeroportuaria()
	{
		leerInformacionEjecucion();
		Buffer canal = new Buffer(numeroClientes, numeroServidores, capacidad);
	}
	
	//-------------------------------------------------------------------------------
	//Métodos
	//-------------------------------------------------------------------------------

	/**
	 * Método que se encarga de leer el archivo en el que se encuentran especificados
	 * el número de servidores, el número de clientes y la capacidad del Buffer para
	 * la ejecución y almacena la información
	 * 
	 * Pos: el número de servidores, el número de clientes y la capacidad están
	 * 		almacenados en numeroClientes, numeroServidores y capacidad 
	 * 		respectivamente.
	 */
	private void leerInformacionEjecucion()
	{
		//TODO
		numeroClientes = 20;
		numeroServidores = 20;
		capacidad = 20;
	}
	
	public static void main(String[] args)
	{
		LogisticaAeroportuaria la = new LogisticaAeroportuaria();
	}
	
}
