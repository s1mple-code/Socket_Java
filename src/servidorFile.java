import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class servidorFile {
	
	private ServerSocket server;
	private Socket socket;
	private Map<String, ObjectOutputStream> map = new HashMap<String, ObjectOutputStream>();

	
	public servidorFile() {
		
		try {
			server = new ServerSocket(5555);
			System.out.println("Servidor on");
			
			while(true) {
				
				socket = server.accept();
				
				new Thread(new ListenerSocket(socket)).start();
				
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	
	public class ListenerSocket implements Runnable {

		//envio de mensagens
		private ObjectOutputStream outputStream;
		//recebe as mensagens
		private ObjectInputStream inputStream;
		
		public ListenerSocket(Socket socket) throws IOException {
			
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
			this.inputStream = new ObjectInputStream(socket.getInputStream());
			
		}

		@Override
		public void run() {
			
			FileMessage msg = null;
			
			try {
				while((msg = (FileMessage) inputStream.readObject()) != null) {
				
					map.put(msg.getCliente(),outputStream);
					
					if(msg.getFile() != null) {
						
						for(Map.Entry<String,ObjectOutputStream> kv : map.entrySet()) {
							
							if(!msg.getCliente().equals(kv.getKey())) {
								kv.getValue().writeObject(msg);
							}
							
						}
						
					}
					
				}
				
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
				
			} catch (IOException e) {
				map.remove(msg.getCliente());
				System.out.println(msg.getCliente() + " desconectou");
				
				
			}

		}

	}
	
	public static void main(String[] args) {
		
		new servidorFile();
		
	}
	
}
