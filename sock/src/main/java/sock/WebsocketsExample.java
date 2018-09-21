package sock;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebsocketsExample extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(WebsocketsExample.class);
  private CommonUtil util = new CommonUtil();

  @Override
  public void start() throws Exception {
    HttpServer server = Vertx.vertx().createHttpServer();

    server.websocketHandler(new Handler<ServerWebSocket>() {
      @Override
      public void handle(ServerWebSocket webs) {
        if (webs.path().equals("/web-socket")) {
          LOG.info("Client connected");
          vertx.setPeriodic(3000l, t -> {
            util.updateWorkflowStatus();
            Map<String, List<Workflow>> workflowResponse = new HashMap<>();
            workflowResponse.put("workflows", util.getWorkflows());
            webs.writeFinalTextFrame(Json.encodePrettily(workflowResponse));
          });

        } else {
          webs.reject();
        }


        LOG.info("Client's message: ");
        webs.handler(data -> {
          LOG.info("Received data " + data.toString("ISO-8859-1"));
        });

      }
    });

    server.listen(9091, "localhost", res -> {
      if (res.succeeded()) {
        LOG.info("Server is now listening!");
      } else {
        LOG.info("Failed to bind!");
      }
    });
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
      LOG.info("Id: " + wf.getId() + "status: " + wf.getStatus());
    }
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
