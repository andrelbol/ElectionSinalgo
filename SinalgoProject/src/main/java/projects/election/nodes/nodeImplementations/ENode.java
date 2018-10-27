package projects.election.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import projects.defaultProject.nodes.timers.MessageTimer;
import sinalgo.tools.Tools;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import projects.election.nodes.timers.ETimer;
import projects.election.nodes.messages.EMessage;
import projects.election.enums.MessageType;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.logging.Logging;
import sinalgo.nodes.Connections;
import sinalgo.nodes.edges.Edge;

public class ENode extends Node {

  private Color color = Color.BLUE;
  private long elected;
  private boolean isParticipant;
  private ENode successor;

  public ENode() {
    super();
    this.isParticipant = false;
  }

  @Override
  public void handleMessages(Inbox inbox) {
    for (Message msg : inbox) {
      if (msg instanceof EMessage) {
        EMessage emsg = (EMessage) msg;
        switch (emsg.getType()) {

          case ELECTION:
            treatElectionMessage(emsg);
            break;

          case COORDENATOR:
            treatCoordMessage(emsg);
            break;

          default:
        }
      }
    }
  }

  /**
   * Regras:
   *  - Msgid >Pi => repassa a mensagem
   *  - Msgid < Pi e Pi não é participante, substitui o id na mensagem e repassa, marca como participante.
   *  - Pi participante => não reenvia
   *  - Msgid = Pi => Pi tem o maior id, marca como não participante e envia mensagem coordenador(pi) par todo mundo.
   */
  public void treatElectionMessage(EMessage msg) {
    if (msg.getId() > this.getID()) {
      System.out.println(String.format("%s seding message to %s.", this, successor));
      send(msg, successor);
    } else if (msg.getId() < this.getID()) {
      System.out.println(
          String.format("%s is participant, sending message with his ID to %s.", this, successor));
      if (!this.isParticipant) {
        this.isParticipant = true;
        msg.setId(this.getID());
        send(msg, successor);
      }
    } else { // Same Id
      System.out.println(String.format("%s is the winner, start sending coord message.", this));
      elected = this.getID();
      EMessage coordMessage = new EMessage(this.getID(), MessageType.COORDENATOR);
      send(coordMessage, successor);
    }
  }

  public void treatCoordMessage(EMessage msg) {
    if (msg.getId() != this.getID()) {
      System.out.println(String.format("%s setting elected variable to %d.", this, msg.getId()));
      this.isParticipant = false;
      this.elected = msg.getId();
      send(msg, successor);
    } else {
      System.out.println("Election terminated!");
      this.color = Color.RED;
    }
  }

  public String toString() {
    return "Node " + this.getID();
  }

  @Override
  public void neighborhoodChange() {
    Connections nodeConnections = this.getOutgoingConnections();
    ENode firstConnectionNode = (ENode) nodeConnections.iterator().next().getEndNode();
    successor = null;

    for (Edge edge : nodeConnections) {
      ENode endNode = (ENode) edge.getEndNode();
      if (endNode.compareTo(this) > 0) {
        if (successor == null)
          successor = endNode;
        else
          successor = endNode.compareTo(successor) < 0 ? endNode : successor;
      }
    }

    if (successor == null) { // Last Node
      successor = firstConnectionNode;
      System.out.println(String.format("Last node %d.", this.getID()));
      for (Edge edge : nodeConnections) { // Find the smallest
        ENode endNode = (ENode) edge.getEndNode();
        successor = endNode.compareTo(successor) < 0 ? endNode : successor;
      }
    }
    System.out.print(String.format("Sucessor of %s is %s", this, successor));
  }

  public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
    String text = "" + this.getID();
    Color textColor = Color.WHITE;

    this.setColor(this.color);
    super.drawNodeAsDiskWithText(g, pt, highlight, text, 70, textColor);
  }

  @NodePopupMethod(menuText = "Start Election")
  public void startElection() {
    EMessage msg = new EMessage(this.getID(), MessageType.ELECTION);
    ETimer timer = new ETimer(this, successor, 1);

    timer.startRelative(1, this);
    Tools.appendToOutput(String.format("Start Routing from %s \n", this));
  }

  public void preStep() {
  }

  public void init() {
  }

  public void postStep() {
  }

  public void checkRequirements() {
  }

  public void compute() {
  }
}