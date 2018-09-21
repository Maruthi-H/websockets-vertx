package sock;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Start {

  public static void main(String[] args) {
    VertxOptions options = new VertxOptions();

    // add additional options you need
    Vertx vertx = Vertx.vertx(options);

    // Websocket functionality is implemented using SockJS in ServerVerticle
    vertx.deployVerticle(ServerVerticle.class.getName());

    // Websocket is implemented in its pure form in WebsocketsExample Verticle
    vertx.deployVerticle(WebsocketsExample.class.getName());

  }

}
