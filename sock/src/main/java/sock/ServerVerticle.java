package sock;


import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;


public class ServerVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);
  private CommonUtil util = new CommonUtil();

  @Override
  public void start(Future<Void> fut) {
    Router router = Router.router(vertx);
    // add cors to allow all domains
    router.route().handler(io.vertx.ext.web.handler.CorsHandler.create("*")
        .allowedMethod(io.vertx.core.http.HttpMethod.GET)
        .allowedMethod(io.vertx.core.http.HttpMethod.POST)
        .allowedMethod(io.vertx.core.http.HttpMethod.PUT)
        .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS).allowCredentials(true)
        .allowedHeader("Access-Control-Allow-Method").allowedHeader("Access-Control-Allow-Origin")
        .allowedHeader("Access-Control-Allow-Credentials").allowedHeader("Content-Type"));
    // you will need to allow outbound and inbound to allow eventbus communication.
    BridgeOptions opts = new BridgeOptions()
        .addOutboundPermitted(new PermittedOptions().setAddress("address-workflowStatus"));
    SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);

    router.route("/eventbus/*").handler(ebHandler);
    router.get("/api/workflows").handler(this::getAllWorkflows);
    router.route().handler(StaticHandler.create());

    // create the server with 9090 port to bind
    vertx.createHttpServer().requestHandler(router::accept).listen(9090);
    LOG.info("server has started");

    EventBus eb = vertx.eventBus();
    // publish messages in 3 sec. interval
    vertx.setPeriodic(3000l, t -> {
      String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)
          .format(Date.from(Instant.now()));
      util.updateWorkflowStatus();
      Map<String, List<Workflow>> workflowResponse = new HashMap<>();
      workflowResponse.put("workflows", util.getWorkflows());
      eb.send("address-workflowStatus", Json.encodePrettily(workflowResponse));
    });
  }

  private void getAllWorkflows(RoutingContext rc) {
    Map<String, List<Workflow>> workflowResponse = new HashMap<>();
    workflowResponse.put("workflows", util.getWorkflows());
    rc.response().putHeader("content-type", "text/plain")
        .end(Json.encodePrettily(workflowResponse));
  }

  public void createSomeData() {

    for (int i = 1; i <= 10; i++) {
      Workflow wf = new Workflow();
      wf.setId(i);
      wf.setName("workflow" + i);
      wf.setStatus("Yet to be scheduled");
      util.getWorkflows().add(wf);

    }
  }

  public void printStatus() {
    for (Workflow wf : util.getWorkflows()) {
      System.out.print("Id: " + wf.getId() + "status: " + wf.getStatus());
    }
    System.out.println("");
  }

  public void updateWorkflowStatus() {

    for (Workflow wf : util.getWorkflows()) {
      if (wf.getStatus().equals("Completed")) {
        continue;
      }
      if (wf.getStatus().equals("Yet to be scheduled")) {
        wf.setStatus("Scheduled");
        break;
      } else if (wf.getStatus().equals("Scheduled")) {
        wf.setStatus("In Progress");
        break;
      } else if (wf.getStatus().equals("In Progress")) {
        wf.setStatus("Completed");
        break;
      }
      if (wf.getStatus().equals("Completed")) {
        wf.setStatus("Yet to be scheduled");
        break;
      }
    }
  }



}
