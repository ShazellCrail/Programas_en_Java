import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class ClienteMulticast {
    static byte[] recibe_mensaje(MulticastSocket socket,int longitud_mensaje) throws IOException
    {
	byte[] buffer = new byte[longitud_mensaje];
	DatagramPacket paquete = new DatagramPacket(buffer,buffer.length);
	socket.receive(paquete);
	return paquete.getData();
    }
    public static void main(String[] args) {
        try{
            System.setProperty("java.net.preferIPv4Stack","true");
            MulticastSocket socket = new MulticastSocket(50000);
            InetSocketAddress grupo = new InetSocketAddress(InetAddress.getByName("230.0.0.0"),50000);
            NetworkInterface netInter = NetworkInterface.getByName("em1");
            socket.joinGroup(grupo,netInter);
            byte[] a = recibe_mensaje(socket,4);
            System.out.println(new String(a,"UTF-8"));
            byte[] buffer = recibe_mensaje(socket,5*8);
            ByteBuffer b = ByteBuffer.wrap(buffer);
            for (int i = 0; i < 5; i++)
                    System.out.println(b.getDouble());
            socket.leaveGroup(grupo,netInter);
            socket.close();
        }catch(Exception e){ // Manejo de excepciones
            e.printStackTrace();
        } // Catch
    } // Main
}