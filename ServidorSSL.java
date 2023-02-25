import java.net.*;
import java.io.*;
import javax.net.ssl.SSLServerSocketFactory;

public class ServidorSSL {
    public static void main(String[] args) {
        try{
            System.setProperty("javax.net.ssl.keyStore","keystore_servidor.jks");
            System.setProperty("javax.net.ssl.keyStorePassword","1234567");
            SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket socket_servidor = socket_factory.createServerSocket(50000);
            Socket conexion = socket_servidor.accept();
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            double x = entrada.readDouble();
            System.out.println(x);
            conexion.close();
        }catch(Exception e){ // Manejo de excepciones
            e.printStackTrace();
        } // Catch
    } // Main
}
