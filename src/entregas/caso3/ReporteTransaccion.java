package entregas.caso3;

import java.util.ArrayList;

public class ReporteTransaccion {

	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------

	/**
	 * Lista de indicadores sobre transacciones de autenticación
	 */
	private static ArrayList<IndicadorTransaccion> transaccionesAutenticacion;
	
	/**
	 * Lista de indicadores sobre transacciones referentes a una solicitud de actualización
	 */
	private static ArrayList<IndicadorTransaccion> transaccionesActualizacion;
	
	/**
	 * Instancia estática del reporte
	 */
	private static ReporteTransaccion instancia;
	
	/**
	 * Tiempo acumulado de todas las transacciones de actualización recibidas por el reporte
	 */
	private static float tiempoAcumuladoActualizacion = 0;
	
	/**
	 * Tiempo acumulado de todas las transacciones de autenticación recibidas por el reporte
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
	//Métodos
	//-------------------------------------------------------------------------------

	/**
	 * Retorna una instancia única por ejecución de la clase
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
	 * Registra una transacción perdida adicional en el reporte
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
		//Se calcula así porque una transacción de actualización y una de autenticación
		//se registran en cada transacción enviada por el Generator. Por lo tanto, sólo
		//se necesita la cantidad de una de estos dos tipos de transacciones para saber el
		//total de transacciones realizadas.
		return transaccionesPerdidas/transaccionesActualizacion.size() + "%";
	}
	
	/**
	 * Agrega la transacción ingresada por parámetro al reporte
	 * @param transaccion transacción a agregar al reporte
	 * @throws Exception si el tipo de la transacción no es definida adecuadamente o
	 * 					hay otro problema en la ejecución
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
			throw new Exception("Transacción de tipo no identificado");
		}
	}
	
	/**
	 * Retorna el tiempo de respuesta promedio en segundos de las transacciones 
	 * de autenticación registradas en el reporte
	 * @return tiempo de respuesta promedio de las transacciones de autenticación
	 */
	public float darTiempoAutenticacionPromedio()
	{
		return tiempoAcumuladoAutenticacion/transaccionesAutenticacion.size();
	}

	/**
	 * Retorna el tiempo de respuesta promedio en segundos de las transacciones 
	 * de actualización registradas en el reporte
	 * @return tiempo de respuesta promedio de las transacciones de actualización
	 */
	public float darTiempoActualizacionPromedio()
	{
		return tiempoAcumuladoActualizacion/transaccionesActualizacion.size();
	}
}
