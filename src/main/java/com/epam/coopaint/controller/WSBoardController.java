package com.epam.coopaint.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint(value = "/board", configurator = CDIConfigurator.class)
public class WSBoardController {
    /*
    private static Logger logger = LogManager.getLogger();

    @Inject
    private WSBoardService sessionHandler;

    @OnOpen
    public void open(Session session) {
        sessionHandler.addSession(session);
        logger.info("New session.");
    }

    @OnClose
    public void close(Session session) {
        sessionHandler.removeSession(session);
        logger.info("Closed session.");
    }

    @OnError
    public void onError(Throwable error) {
        logger.error("WS error.", error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        var mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(message);
            JsonNode action = rootNode.path("action");
            String strAction = action.asText();
            switch (strAction) {
                case "put-msg": {
                    sessionHandler.addMessage(rootNode.path("message"));
                    break;
                }
                case "remove-msg": {
                    Message msg = mapper.readValue(rootNode.path("message").asText(), Message.class);
                    sessionHandler.removeMessage(msg.getId());
                    break;
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    */
}
