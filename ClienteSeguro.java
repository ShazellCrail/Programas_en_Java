import java.net.*;
import java.io.*;
import javax.net.ssl.SSLSocketFactory;

public class ClienteSeguro{
    
    static void readd(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception{
        while (longitud > 0){
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }
    
    public static void main(String[] args){
        try{
            int contador = 0, tam_arreglo = args.length;
            for(int i = 0; i < tam_arreglo; i++)
                if(args[i].contains(".") == true)
                    contador++;
            String[] archivos_sin_espacios = new String[contador];
            String auxiliar = "";
            int j = 0, n, l = 0;
            for(int i = 0; i < tam_arreglo; i++)
                if(args[i].contains(".") == true){
                    archivos_sin_espacios[j] = args[i];
                    j++;
                }else{
                    for(int k = i + 1; k < tam_arreglo; k++)
                        if(args[k].contains(".") == false)
                            l++;
                        else{
                            l++;
                            k = tam_arreglo;
                        }
                    l = l + i;
                    for(n = i; n < l; n++)
                        auxiliar = auxiliar + args[n] + " ";
                    auxiliar = auxiliar + args[n];
                    archivos_sin_espacios[j] = auxiliar;
                    j++;
                    i = n;
                    auxiliar = "";
                    l = 0;
                } 
            for(int i = 0; i < contador; i++){
                File archivo = new File(archivos_sin_espacios[i]);
                if(!archivo.exists())
                    System.out.println("El archivo " + archivos_sin_espacios[i] + " no existe");
                else{
                    String rutaOrigen = archivo.getAbsolutePath();
                    Runnable r = new EnviarArchivo(archivo, rutaOrigen, 2000);
                    new Thread(r).start();
                    new Thread(r).join();
                }
            }
        }catch(Exception e){ // Manejo de excepciones
            e.printStackTrace();
        } // Catch
    } // Main
    
    static class EnviarArchivo implements Runnable{
        File f;
        String pathOrigen;
        int tam_buffer;
	public EnviarArchivo(File f, String pathOrigen, int tam_buffer){ // Constructor - inicializa atributos
            this.f = f;
            this.pathOrigen = pathOrigen;
            this.tam_buffer = tam_buffer;
	}
	synchronized public void run(){
            try{
                if(f.isFile()){  
                    System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
                    System.setProperty("javax.net.ssl.trustStorePassword", "123456");
                    SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    Socket conexion = null;
                    for(;;)
                        try{
                            conexion = cliente.createSocket("localhost", 50000);
                            break;
                        }catch (Exception e){
                            Thread.sleep(100);       
                        }
                    DataOutputStream salida = new DataOutputStream(conexion.getOutputStream()); // OutputStream
                    String nombre = f.getName();
                    long tam_archivo = f.length();
                    int tam = (int) tam_archivo;
                    System.out.println("\nSe envia el archivo: " + nombre + " con: " + tam + " bytes");
                    DataInputStream entrada = new DataInputStream(new FileInputStream(pathOrigen)); // InputStream
                    DataInputStream entrada2 = new DataInputStream(conexion.getInputStream());
                    salida.writeInt(0); // La bandera tiene el valor de 0 = Subir archivo
                    salida.flush();
                    int longitud_nombre = nombre.length();
                    salida.writeInt(longitud_nombre);
                    salida.flush();
                    salida.write(nombre.getBytes()); // Se envía información de los archivos
                    salida.flush();
                    salida.writeInt(tam);	
                    salida.flush();
                    long enviados = 0;
                    int n, porciento;
                    byte[] b = new byte[tam_buffer];
                    while(enviados < tam){
                        n = entrada.read(b);
                        salida.write(b,0,n);
                        salida.flush();
                        enviados = enviados+n;
                        porciento = (int)((enviados * 100) / tam);
                    } // While
                    System.out.print("Archivo enviado.\n");
                    int longitud_mensaje = entrada2.readInt();
                    byte[] buffer = new byte[longitud_mensaje];
                    readd(entrada2, buffer, 0, longitud_mensaje);
                    if(!"OK".equals(new String(buffer)))
                        System.out.println(new String(buffer, "UTF-8"));
                    conexion.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            } // Catch
	}
    }
}