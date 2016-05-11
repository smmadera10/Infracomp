package entregas.caso3;

import java.util.ArrayList;

public class ReporteTransaccion {

	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------

	/**
	 * Lista de indicadores sobre transacciones de autenticaci�n
	 */
	private static ArrayList<IndicadorTransaccion> transaccionesAutenticacion;
	
	/**
	 * Lista de indicadores sobre transacciones referentes a una solicitud de actualizaci�n
	 */
	private static ArrayList<IndicadorTransaccion> transaccionesActualizacion;
	
	/**
	 * Instancia est�tica del reporte
	 */
	private static ReporteTransaccion instancia;
	
	/**
	 * Tiempo acumulado de todas las transacciones de actualizaci�n recibidas por el reporte
	 */
	private static float tiempoAcumuladoActualizacion = 0;
	
	/**
	 * Tiempo acumulado de todas las transacciones de autenticaci�n recibidas por el reporte
	 */
	private static float tiempoAcumuladoAutenticacion = 0;
	
	/**
	 * Cantidad de transacciones perdidas registradas en el reporte
	 */
	private static int transaccionesPerdidas = 0;
	
	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------

	private ReporteTransaccion()
	{
		transaccionesAutenticacion = new ArrayList<IndicadorTransaccion>();
		transaccionesActualizacion = new ArrayList<IndicadorTransaccion>();
	}
	
	//-------------------------------------------------------------------------------
	//M�todos
	//-------------------------------------------------------------------------------

	/**
	 * Retorna una instancia �nica por ejecuci�n de la clase
	 * @return instancia de ReporteTransaccion
	 */
	public synchronized static ReporteTransaccion getInstance()
	{
		if (instancia == null)
		{
			instancia = new ReporteTransaccion();
		}
		return instancia;
	}
	
	/**
	 * Registra una transacci�n perdida adicional en el reporte
	 */
	public synchronized void transaccionPerdida()
	{
		transaccionesPerdidas++;
	}
	
	/**
	 * Retorna el porcentaje de transacciones perdidas respecto a las transacciones
	 * totales registradas.
	 * @return porcentaje de transacciones perdidas respecto a las totales
	 */
	public String darPorcentajeTransaccionesPerdidas()
	{
		//Se calcula as� porque una transacci�n de actualizaci�n y una de autenticaci�n
		//se registran en cada transacci�n enviada por el Generator. Por lo tanto, s�lo
		//se necesita la cantidad de una de estos dos tipos de transacciones para saber el
		//total de transacciones realizadas.
		return transaccionesPerdidas/transaccionesActualizacion.size() + "%";
	}
	
	/**
	 * Agrega la transacci�n ingresada por par�metro al reporte
	 * @param transaccion transacci�n a agregar al reporte
	 * @throws Exception si el tipo de la transacci�n no es definida adecuadamente o
	 * 					hay otro problema en la ejecuci�n
	 */
	public synchronized void agregarTransaccion(IndicadorTransaccion transaccion) throws Exception
	{
		
		if(transaccion.darTipoTransaccion().equals(IndicadorTransaccion.TIPO_ACTUALIZACION))
		{
			transaccionesActualizacion.add(transaccion);
			tiempoAcumuladoActualizacion += transaccion.darTiempoRespuesta();

				System.out.println("actualizacion: " + transaccionesActualizacion.size());
		}
		
		else if(transaccion.darTipoTransaccion().equals(IndicadorTransaccion.TIPO_AUTENTICACION) )
		{
			transaccionesAutenticacion.add(transaccion);
			tiempoAcumuladoAutenticacion += transaccion.darTiempoRespuesta();
		}
		
		else
		{
			throw new Exception("Transacci�n de tipo no identificado");
		}
	}
	
	/**
	 * Retorna el tiempo de respuesta promedio en segundos de las transacciones 
	 * de autenticaci�n registradas en el reporte
	 * @return tiempo de respuesta promedio de las transacciones de autenticaci�n
	 */
	public float darTiempoAutenticacionPromedio()
	{
		return tiempoAcumuladoAutenticacion/transaccionesAutenticacion.size();
	}

	/**
	 * Retorna el tiempo de respuesta promedio en segundos de las transacciones 
	 * de actualizaci�n registradas en el reporte
	 * @return tiempo de respuesta promedio de las transacciones de actualizaci�n
	 */
	public float darTiempoActualizacionPromedio()
	{
		return tiempoAcumuladoActualizacion/transaccionesActualizacion.size();
	}
}
