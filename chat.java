import java.net.*;
import java.io.*;

class chat {
    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
	DatagramSocket socket = new DatagramSocket();
	socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
	socket.close();
    }
    
    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
	byte[] buffer = new byte[longitud_mensaje];
	DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
	socket.receive(paquete);
	return paquete.getData();
    }
    
    static class Worker extends Thread {
        public void run(){
            for(;;){
                try{
                    MulticastSocket socket = new MulticastSocket(10000);
                    InetSocketAddress grupo = new InetSocketAddress(InetAddress.getByName("239.10.10.10"), 10000);
                    NetworkInterface netInter = NetworkInterface.getByName("em1");
                    socket.joinGroup(grupo, netInter);
                    byte[] a = recibe_mensaje_multicast(socket, 50);
                    System.out.println(new String(a, "UTF-8"));
                    socket.leaveGroup(grupo, netInter);
                    socket.close();
                }catch(IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }       
    }
    
    public static void main(String[] args) throws Exception {
        new Worker().start();
        String nombre = args[0];
        for(;;){
	    System.out.println("Mensaje: ");
            BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
            String mensaje = br1.readLine();
            System.setProperty("java.net.preferIPv4Stack", "true");
            String mensaje_completo = nombre + " :- " + mensaje;
            envia_mensaje_multicast(mensaje_completo.getBytes(), "239.10.10.10", 10000);
        }
    } // Main
}