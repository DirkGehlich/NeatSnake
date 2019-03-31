package io.battlesnake.server;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import server.Snake;

public class GameServer {
	
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final GameService HANDLER = new GameService();
    private static final Logger LOG = LoggerFactory.getLogger(Snake.class);
    
    
	public static void main( String[] args ) {
		String port = System.getProperty("PORT");
        if (port != null) {
            LOG.info("Found system provided port: {}", port);
        } else {
            port = "8080";
            LOG.info("Using default port: {}", port);
        }
        port(Integer.parseInt(port));
        get("/", (req, res) -> "Battlesnake documentation can be found at " +
                "<a href=\"https://docs.battlesnake.io\">https://docs.battlesnake.io</a>.");
        post("/start", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/ping", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/move", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/end", HANDLER::process, JSON_MAPPER::writeValueAsString);	 	
	}
}
