
public class LogisticaAeroportuaria {

	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Constante que modela el nombre del archivo que especifica el n�mero
	 * de clientes, n�mero de servidores y la capacidad del Buffer para la
	 * ejecuci�n
	 */
	private final static String NOMBRE_ARCHIVO_PROPIEDADES = "";
	
	/**
	 * Constante que contiene la direcci�n local del archivo que especifica 
	 * el n�mero de clientes, n�mero de servidores y la capacidad del Buffer 
	 * para la ejecuci�n
	 */
	private final static String DIRECCION_ARCHIVO_PROPIEDADES = "";
	
	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------

	/**
	 * Modela el n�mero inicial de clientes que han de existir durante la 
	 * ejecuci�n. Este atributo es pasado al buffer
	 */
	private int numeroClientes;
	
	/**
	 * Modela el n�mero de servidores que existir�n durante la ejecuci�n.
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
	//M�todos
	//-------------------------------------------------------------------------------

	/**
	 * M�todo que se encarga de leer el archivo en el que se encuentran especificados
	 * el n�mero de servidores, el n�mero de clientes y la capacidad del Buffer para
	 * la ejecuci�n y almacena la informaci�n
	 * 
	 * Pos: el n�mero de servidores, el n�mero de clientes y la capacidad est�n
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
