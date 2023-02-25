import java.net.*;
import java.io.*;

class Servidor {
    static void read(DataInputStream f,byte[] b,int posicion,int longitud) throws Exception
    {
        while (longitud > 0)
        {
            int n = f.read(b,posicion,longitud);
            posicion += n;
            longitud -= n;
        }
    }
    static class Worker extends Thread
    {
        Socket conexion;
        synchronized void primo(long numero,long numero_inicial,long numero_final) throws Exception
        {
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            for(long i=numero_inicial;i<(numero_final+1);i++)
                {  
                    if((numero%i) == 0)
                    {
                        System.out.println("El numero: " + i + " DIVIDE");
                        salida.writeInt(6);
                        salida.write("DIVIDE".getBytes());
                        i = numero_final + 1;
                    }
                    else
                        if(i == numero_final)
                        {
                            salida.writeInt(9);
                            salida.write("NO DIVIDE".getBytes());
                        }
                }
        }
        Worker(Socket conexion)
        {
            this.conexion = conexion;
        }
        public void run()
        {
            try
            {
                System.out.println("Conexión establecida desde"+  conexion.getInetAddress()+": " + conexion.getPort());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                long numero = entrada.readLong();
                System.out.println("Número: " + numero);
                long numero_inicial = entrada.readLong();
                System.out.println("Número inicial: " + numero_inicial);
                long numero_final = entrada.readLong();
                System.out.println("Número final: " + numero_final);
                primo(numero,numero_inicial,numero_final);
            }catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }       
    }
    public static void main(String[] args) throws Exception 
    {
        ServerSocket servidor = new ServerSocket(1234);
        for(;;)
        {
            Socket conexion = servidor.accept();
            Worker w = new Worker(conexion);
            w.start();
            w.join();
        }
    } // Main
}