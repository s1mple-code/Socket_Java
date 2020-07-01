import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFileChooser;



public class ClienteFile {
	
	private Socket socket;
	private ObjectOutputStream outputStream;
	
	public ClienteFile() throws IOException {
		
		this.socket = new Socket("127.0.0.1", 5555);
		this.outputStream = new ObjectOutputStream(socket.getOutputStream());
		
		new Thread(new ListenerSocket(socket)).start();
		
		menu();
	}
	
	//enviar mensagem
	private void menu() throws IOException {
		
		Scanner scanner = new Scanner(System.in);
		System.out.print("Digite seu nome: ");
		
		String nome = scanner.nextLine();
		
		this.outputStream.writeObject(new FileMessage(nome));
		
		int option = 0;
		
		while(option != 1) {
			
			System.out.print("1 - Sair | 2 - Enviar: ");
			option = scanner.nextInt();
			
			if(option == 2) {	
				send(nome);	
			}else if(option == 1) {
				System.exit(0);
			}
			
		}
		
	}
	
private void send(String nome) throws IOException {
	
	JFileChooser fileChooser =  new JFileChooser();
	
	int opt = fileChooser.showOpenDialog(null);
	
	if(opt == JFileChooser.APPROVE_OPTION) {
		
		File file = fileChooser.getSelectedFile();
		
		this.outputStream.writeObject(new FileMessage(nome, file));
		
	}
		
	}

// receber mensagem
	public class ListenerSocket implements Runnable {

		private ObjectInputStream inputStream;
		
		public ListenerSocket(Socket socket) throws IOException {
			
			this.inputStream = new ObjectInputStream(socket.getInputStream());
			
		}

		@Override
		public void run() {
			
     FileMessage msg = null;
			
				try {
					while((msg = (FileMessage) inputStream.readObject()) != null) {
						
						System.out.println("\nVoce recebeu um arquivo de " + msg.getCliente());
						System.out.println("O arquivo é " + msg.getFile().getName());
						
						//imprime(msg);
						
						salvar(msg);
						
						System.out.print("1 - Sair | 2 - Enviar: ");
						
					}
				} catch (ClassNotFoundException e) {
					
					
					e.printStackTrace();
				} catch (IOException e) {
					
					
					e.printStackTrace();
				}

		}

		private void salvar(FileMessage msg) {
			
			try {
				
				Thread.sleep(new Random().nextInt(1000));
				
				long time = System.currentTimeMillis();
				
				FileInputStream fileInputStream = new FileInputStream(msg.getFile());
				FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Vinícius\\Documents\\chat\\" + time + " " + msg.getFile().getName());
				
				FileChannel fin = fileInputStream.getChannel();
				FileChannel fout = fileOutputStream.getChannel();
				
				long size = fin.size();
				
				fin.transferTo(0, size, fout);
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
		}

		//imprime o arquivo no console
		private void imprime(FileMessage msg) {
			
			try {
				FileReader fileReader = new FileReader(msg.getFile());
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String linha;
				while((linha = bufferedReader.readLine()) != null) {
					
				System.out.println(linha);	
					
				}
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}

	}
	
	public static void main(String[] args) {
		
		try {
			new ClienteFile();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

}
