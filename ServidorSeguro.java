import java.net.*;
import java.io.*;
import java.io.IOException;
import javax.net.ssl.SSLServerSocketFactory;

class ServidorSeguro{
    static void readd(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception{
        while (longitud > 0){
            int n = f.read(b, posicion,longitud);
            posicion += n;
            longitud -= n;
        }
    }
    synchronized public static void RecibirArchivos(DataInputStream entrada, DataOutputStream salida2, String nombre) throws IOException{
        long tam = entrada.readInt();
        int tam_buffer = 2000;
        System.out.println("\nRecibimos el archivo: " + nombre + " con: " + tam + " bytes");
        DataOutputStream salida = new DataOutputStream(new FileOutputStream(nombre));
        long recibidos = 0;
        int n, porcentaje = 0;
        String mensaje_salida;
        byte[] b = new byte[tam_buffer];
        while(recibidos < tam){
            n = entrada.read(b);
            salida.write(b, 0, n);
            salida.flush();
            recibidos = recibidos + n;
            porcentaje = (int)(recibidos * 100 / tam);
            System.out.print("Recibido: " + porcentaje + "%\r");
        } // While
        if(porcentaje == 100)
            mensaje_salida = "OK";
        else
            mensaje_salida = "El servidor no pudo guardar el archivo " + nombre;
        int longitud_mensaje = mensaje_salida.length();
        salida2.writeInt(longitud_mensaje);
        salida2.flush();
        salida2.write(mensaje_salida.getBytes()); // Se envía información de los archivos
        salida2.flush();
        System.out.println("**************************************************************");
    }
    static class Worker extends Thread{
        Socket conexion;
        Worker(Socket conexion){
            this.conexion = conexion;
        }
        public void run(){
            try{
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida2 = new DataOutputStream(conexion.getOutputStream());
                int bandera = entrada.readInt();
                if(bandera == 0){
                    int longitud_nombre = entrada.readInt();
                    byte[] buffer = new byte[longitud_nombre];
                    readd(entrada, buffer, 0, longitud_nombre);
                    String nombre = new String(buffer, "UTF-8");
                    RecibirArchivos(entrada, salida2, nombre);
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }       
    }
    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "1234567");
        SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket socket_servidor = socket_factory.createServerSocket(50000);
        for(;;){
            Socket conexion = socket_servidor.accept();
            Worker w = new Worker(conexion);
            w.start();
            w.join();
        }
    } // Main
}