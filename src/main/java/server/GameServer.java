package server;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class GameServer {
	public static void main( String[] args ) throws IOException, InterruptedException 
	   {
		 	String port = System.getProperty("PORT");
	        if (port != null) {
	        	System.out.println("Found system provided port: " + port);
	        } else {
	        	System.out.println("Using default port: " + port);
	            port = "8080";
	        }
	        
	      String baseUrl = ( args.length > 0 ) ? args[0] : "http://localhost:" + port;

	      final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
	            URI.create( baseUrl ), new ResourceConfig( GameService.class ), false );
	      Runtime.getRuntime().addShutdownHook( new Thread( new Runnable() {
	         @Override
	         public void run() {
	            server.shutdownNow();
	         }
	      } ) );
	      server.start();

	      System.out.println( String.format( "\nGrizzly-HTTP-Server gestartet mit der URL: %s\n"
                  + "Stoppen des Grizzly-HTTP-Servers mit:      Strg+C\n",
                  baseUrl + GameService.webContextPath ) );
	      
	      Thread.currentThread().join();
	   }
}
