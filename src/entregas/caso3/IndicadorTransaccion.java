package entregas.caso3;

public class IndicadorTransaccion {
	
	//-------------------------------------------------------------------------------
	//Constantes
	//-------------------------------------------------------------------------------

	/**
	 * Representa el tipo de transacción que se refiere a la autenticación de los
	 * participantes (intercambio de certificados)
	 */
	public final static String TIPO_AUTENTICACION = "Autenticación participantes";
	
	/**
	 * Representa el tipo de transacción que se refiere a una solicitud de actualización
	 * (tiempo transcurrido entre la salida del primer mensaje del cliente “ACT1” y la 
	 * llegada de la respuesta respectiva –OK o ERROR–) 
	 */
	public final static String TIPO_ACTUALIZACION = "Actualización";
	
	//-------------------------------------------------------------------------------
	//Atributos
	//-------------------------------------------------------------------------------
	
	/**
	 * Contiene el tiempo de respuesta de la transacción registrado por el usuario
	 * final
	 */
	private float tiempoRespuesta;
	
	/**
	 * Modela el tipo de transacción que se está tratando
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
	//Métodos
	//-------------------------------------------------------------------------------

	/**
	 * @return el tipo de la transacción
	 */
	public String darTipoTransaccion()
	{
		return tipoTransaccion;
	}
	
	/**
	 * @return el tiempo de respuesta de la transacción medido por el usuario final
	 */
	public float darTiempoRespuesta()
	{
		return tiempoRespuesta;
	}

}
