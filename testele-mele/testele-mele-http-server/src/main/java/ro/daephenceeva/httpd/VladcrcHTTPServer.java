package ro.daephenceeva.httpd;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;

public class VladcrcHTTPServer {

	private static final Logger logger = Logger.getLogger(VladcrcHTTPServer.class.getName());
	
	// this is the folder where the server will get the HTML files from
	private static final String serverWorkspace = "t:\\work\\";
//	private static final String serverWorkspace = "/home/vlad/Documents/play";
	
	public static void main(String[] args) throws Exception {
		logger.info("Starting the HTTP server...");
		boolean listening = true;
		 
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		InetAddress inetAddress = InetAddress.getByName("localhost");
		ServerSocket serverSocket = factory.createServerSocket(8080, 100, inetAddress);
		
		// we use a thread pool because we don't want to create a new thread for each request,
		// that would be too expensive
		ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

		while (listening) {
			Socket socket = serverSocket.accept();

			String clientName = socket.getRemoteSocketAddress().toString();
			logger.info("Client " + clientName + " has connected.");
			
			// we process the request in a pooled thread
			executorService.execute(new ClientHandler(socket, serverWorkspace));
		}
	}

}
