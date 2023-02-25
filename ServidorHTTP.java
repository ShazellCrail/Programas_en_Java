import java.net.*;
import java.io.*;

class ServidorHTTP {
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
        Worker(Socket conexion)
        {
            this.conexion = conexion;
        }
        public void run()
        {
            System.out.println(conexion.getRemoteSocketAddress().toString());
            try{
                BufferedReader entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                PrintWriter salida = new PrintWriter(conexion.getOutputStream());
                String s = entrada.readLine();
                String[] parts = s.split("=");
                String[] partes = parts[parts.length - 1].split(" ");
                String numero_enviado = partes[partes.length - 2];
                long numero = Long.parseLong(numero_enviado);
                if (numero > 1 && s.startsWith("GET /primo?numero="))
                {
                    long numero_inicial = 2;
                    double aux1 = numero/2;
                    long tope = (long)aux1;
                    double numeroDividido = tope/4;
                    long aux = (long)numeroDividido;
                    long numero_final = aux;
                    int aux2 = 0;
                    int[ ] bandera = new int[4];
                    for(int i=1;i<5;i++)
                    {
                        Socket conexion2 = new Socket("localhost",1234);
                        DataOutputStream salida2 = new DataOutputStream(conexion2.getOutputStream());
                        DataInputStream entrada2 = new DataInputStream(conexion2.getInputStream());
                        salida2.writeLong(numero);
                        salida2.writeLong(numero_inicial);
                        salida2.writeLong(numero_final);
                        numero_inicial = numero_final+1;
                        if(i==3)
                            numero_final = tope;
                        else
                            numero_final = aux * (i + 1);
                        int n = entrada2.readInt();
                        byte[]  buffer = new byte[n];
                        read(entrada2,buffer,0,n);
                        System.out.println(new String(buffer,"UTF-8"));
                        if("DIVIDE".equals(new String(buffer)))
                            bandera[aux2] = 0;
                        else
                            bandera[aux2] = 1;
                        aux2++;
                    }
                    if(bandera[0]==1 && bandera[1]==1 && bandera[2]==1 && bandera[3]==1)
                    {
                        String ayuda = "<html><button onclick='alert(\"ES PRIMO\")'>Da click para ver la respuesta</button></html>";
                        salida.println("HTTP/1.1 200 OK");
                        salida.println("Content-type: text/html; charset=utf-8");
                        salida.println("Content-length: "+ayuda.length());
                        salida.println();
                        salida.flush();
                        salida.println(ayuda);
                        salida.flush();
                    }else
                    {
                        String ayuda = "<html><button onclick='alert(\"NO ES PRIMO\")'>Da click para ver la respuesta</button></html>";
                        salida.println("HTTP/1.1 200 OK");
                        salida.println("Content-type: text/html; charset=utf-8");
                        salida.println("Content-length: "+ayuda.length());
                        salida.println();
                        salida.flush();
                        salida.println(ayuda);
                        salida.flush();
                    }
                }else{
                    salida.println("HTTP/1.1 404 File Not Found");
                    salida.flush();
                }
            }catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }       
    }
    public static void main(String[] args) throws Exception 
    {
        ServerSocket servidor = new ServerSocket(8080);
        for(;;)
        {
            Socket conexion = servidor.accept();
            Worker w = new Worker(conexion);
            w.start();
        }
    } // Main
}