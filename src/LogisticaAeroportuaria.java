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
	 * Constante que modela el nombre del archivo que especifica el número
	 * de clientes, número de servidores y la capacidad del Buffer para la
	 * ejecución
	 */
	private final static String NOMBRE_ARCHIVO_PROPIEDADES = "config.properties";
	
	/**
	 * Constante que contiene la dirección local del archivo que especifica 
	 * el número de clientes, número de servidores y la capacidad del Buffer 
	 * para la ejecución
	 */
	private final static String DIRECCION_ARCHIVO_PROPIEDADES = "docs/";
	
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

	public LogisticaAeroportuaria() throws IOException
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
