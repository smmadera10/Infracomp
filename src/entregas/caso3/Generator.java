package entregas.caso3;

import java.util.concurrent.TimeUnit;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {
	
	private LoadGenerator generator;
	
	public Generator(int numberOfTasks, int gapBetweenTasks)
	{
		Task work=createTask();
		generator=new LoadGenerator("Agente-ServerloadTest",numberOfTasks,work, gapBetweenTasks);
		generator.generate();
	}
	
	private Task createTask()
	{
		return new ClientServTask();
	}

	public static void main(String[] args)
	{
		int tipoCarga = 3;
		
		if(tipoCarga == 1)
		{
			Generator g = new Generator(400, 20);
			System.err.println(ReporteTransaccion.getInstance().darTiempoActualizacionPromedio());
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(tipoCarga == 2)
		{
			Generator g = new Generator(200, 40);
			System.err.println(ReporteTransaccion.getInstance().darTiempoActualizacionPromedio());
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(tipoCarga == 3)
		{
			Generator g = new Generator(80, 100);
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		//System.err.println("instancia: " + ReporteTransaccion.getInstance());
		System.err.println("Tiempo autenticación(ms): " + ReporteTransaccion.getInstance().darTiempoAutenticacionPromedio() * 1000);
		System.err.println("Tiempo actualización(ms): " + ReporteTransaccion.getInstance().darTiempoActualizacionPromedio() * 1000);
		System.err.println("Porcentaje transacciones perdidas: " + ReporteTransaccion.getInstance().darPorcentajeTransaccionesPerdidas());
		
	}
}
