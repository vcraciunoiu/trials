package ro.daephenceeva.httpd;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;

public class VladcrcHTTPServer {

	private static final Logger logger = Logger.getLogger(VladcrcHTTPServer.class.getName());
	
	// this is the folder where the server will get the HTML files from, like "www-data" in Apache httpd.
	// for the homework scope I set it to the "resources" folder in the project, so the demo can be run very quickly
	private static final String serverWorkspace = "src/main/resources";
	
	public static void main(String[] args) {
		logger.info("Starting the HTTP server...");
		boolean listening = true;

		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		ServerSocket serverSocket;

		// the port we will listen on
		int port = 8080;

		// how many connections are queued 
		int backlog = 100;

		try {
			// the address we will bind to; for now we bind locally, for the scope of this homework
			InetAddress inetAddress = InetAddress.getByName("localhost");
			serverSocket = factory.createServerSocket(port, backlog, inetAddress);
		} catch (Exception e) {
			logger.severe("Error initializing server socket: " + e.getMessage());
			return;
		}

		// we use a thread pool because we don't want to create a new thread for each request,
		// that would be too expensive
		ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		
		while (listening) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();

				String clientName = socket.getRemoteSocketAddress().toString();
				logger.info("Client " + clientName + " has connected.");
				
				// we process the request in a pooled thread
				executorService.execute(new ClientHandler(socket, serverWorkspace));
			} catch (IOException e) {
				logger.severe("Server cannot accept. Exiting...");
				listening = false;
			}
		}
		
		stopAll(executorService, serverSocket);
		logger.info("HTTP server stopped.");
	}

	private static void stopAll(ThreadPoolExecutor executorService, ServerSocket serverSocket) {
        try {
    		serverSocket.close();
    		
    		// prevent new Runnable objects from being submitted
    		executorService.shutdown();

    		// wait for existing connections to complete
			if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
				// stop executing threads
				executorService.shutdownNow();
			}
		} catch (Exception e) {
			logger.severe("Cannot stop.");
			e.printStackTrace();
		}
	}

}
