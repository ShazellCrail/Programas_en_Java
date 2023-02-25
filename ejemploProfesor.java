// NOTA IMPORTANTE: Lo que está comentado es lo que hace que el programa tenga sincronización.
// Si dejamos el programa como está entonces se encuentra sin sincronización.
class ejemploProfesor extends Thread
{
	static long n;
	//static Object obj = new Object();
	public void run()
	{
		for(int i = 0;i < 100000;i++)
			//synchronized(obj)
			//{
				n++;
			//}
	}
	public static void main(String[] args) throws Exception
	{
		ejemploProfesor t1 = new ejemploProfesor();
		ejemploProfesor t2 = new ejemploProfesor();
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		System.out.println(n);
                long tiempo = System.currentTimeMillis();
                System.out.println("" + tiempo);

	}
}