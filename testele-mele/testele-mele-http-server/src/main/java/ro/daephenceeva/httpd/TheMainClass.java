package ro.daephenceeva.httpd;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;

public class TheMainClass {

	private static final Logger logger = Logger.getLogger(TheMainClass.class.getName());
	
	public static void main(String[] args) throws Exception {
		logger.info("Starting the HTTP server...");
		
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		InetAddress inetAddress = InetAddress.getByName("localhost");
		ServerSocket serverSocket = factory.createServerSocket(8080, 100, inetAddress);
		
		ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

		while (true) {
			Socket socket = serverSocket.accept();

			String clientName = socket.getRemoteSocketAddress().toString();
			logger.info("Client " + clientName + " has connected.");
			
			// we process the request in a pooled thread
			executorService.execute(new ClientHandler(socket));
			
//			Thread clientThread = new Thread(new ClientHandler(socket));
//			clientThread.setName(clientName);
//			clientThread.start();
		}
	}

}
