package entregas.caso3;

public class IndicadorTransaccion {
	
	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Representa el tipo de transacci�n que se refiere a la autenticaci�n de los
	 * participantes (intercambio de certificados)
	 */
	public final static String TIPO_AUTENTICACION = "Autenticaci�n participantes";
	
	/**
	 * Representa el tipo de transacci�n que se refiere a una solicitud de actualizaci�n
	 * (tiempo transcurrido entre la salida del primer mensaje del cliente �ACT1� y la 
	 * llegada de la respuesta respectiva �OK o ERROR�) 
	 */
	public final static String TIPO_ACTUALIZACION = "Actualizaci�n";
	
	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------
	
	/**
	 * Contiene el tiempo de respuesta de la transacci�n registrado por el usuario
	 * final
	 */
	private float tiempoRespuesta;
	
	/**
	 * Modela el tipo de transacci�n que se est� tratando
	 */
	private String tipoTransaccion;
	
	//-------------------------------------------------------------------------------
	//Constructores
	//-------------------------------------------------------------------------------
	
	public IndicadorTransaccion(String tipoTransaccion, float tiempoRespuesta)
	{
		this.tipoTransaccion = tipoTransaccion;
		this.tiempoRespuesta = tiempoRespuesta;
	}
	
	//-------------------------------------------------------------------------------
	//M�todos
	//-------------------------------------------------------------------------------

	/**
	 * @return el tipo de la transacci�n
	 */
	public String darTipoTransaccion()
	{
		return tipoTransaccion;
	}
	
	/**
	 * @return el tiempo de respuesta de la transacci�n medido por el usuario final
	 */
	public float darTiempoRespuesta()
	{
		return tiempoRespuesta;
	}

}
