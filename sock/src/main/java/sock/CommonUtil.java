package sock;

import java.util.LinkedList;
import java.util.List;

public class CommonUtil {

  private List<Workflow> workflows = new LinkedList<>();

  CommonUtil() {
    createSomeData();
  }

  public void createSomeData() {
    for (int i = 1; i <= 10; i++) {
      Workflow wf = new Workflow();
      wf.setId(i);
      wf.setName("workflow" + i);
      wf.setStatus("Yet to be scheduled");
      workflows.add(wf);

    }
  }

  public void printStatus() {
    for (Workflow wf : workflows) {
      System.out.print("Id: " + wf.getId() + "status: " + wf.getStatus());
    }
    System.out.println("");
  }

  public void updateWorkflowStatus() {

    for (Workflow wf : workflows) {
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

  public List<Workflow> getWorkflows() {
    return workflows;
  }

  public void setWorkflows(List<Workflow> workflows) {
    this.workflows = workflows;
  }

}
