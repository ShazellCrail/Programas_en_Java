import java.net.*;
import java.io.*;
import javax.net.ssl.SSLSocketFactory;

public class ClienteSSL {
    public static void main(String[] args) {
        try{
            System.setProperty("javax.net.ssl.trustStore","keystore_cliente.jks");
            System.setProperty("javax.net.ssl.trustStorePassword","123456");
            SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket conexion = cliente.createSocket("localhost",50000);
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeDouble(123456789.123456789);
            Thread.sleep(1000);
            conexion.close();
        }catch(Exception e){ // Manejo de excepciones
            e.printStackTrace();
        } // Catch
    } // Main
}
