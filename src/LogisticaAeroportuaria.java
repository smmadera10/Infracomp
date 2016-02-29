import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class LogisticaAeroportuaria {

	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Constante que modela el nombre del archivo que especifica el n�mero
	 * de clientes, n�mero de servidores y la capacidad del Buffer para la
	 * ejecuci�n
	 */
	private final static String NOMBRE_ARCHIVO_PROPIEDADES = "config.properties";
	
	/**
	 * Constante que contiene la direcci�n local del archivo que especifica 
	 * el n�mero de clientes, n�mero de servidores y la capacidad del Buffer 
	 * para la ejecuci�n
	 */
	private final static String DIRECCION_ARCHIVO_PROPIEDADES = "docs/";
	
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

	public LogisticaAeroportuaria() throws IOException
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
	 * @throws IOException 
	 */
	private void leerInformacionEjecucion() throws IOException
	{
		//TODO
		Properties prop = new Properties();
		File file = new File(DIRECCION_ARCHIVO_PROPIEDADES + NOMBRE_ARCHIVO_PROPIEDADES);
		FileInputStream fileInput = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(fileInput);

		numeroClientes = Integer.parseInt( properties.getProperty("numeroClientes") );
		numeroServidores = Integer.parseInt( properties.getProperty("numeroServidores") );
		capacidad = Integer.parseInt( properties.getProperty("capacidad") );
		fileInput.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		LogisticaAeroportuaria la = new LogisticaAeroportuaria();
	}
	
}
