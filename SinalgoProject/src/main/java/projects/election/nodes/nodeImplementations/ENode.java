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

  private ENode successor;
  private long elected;
  private boolean isParticipant;
  Logging log;

  public ENode() {
    super();
    this.isParticipant = false;
    this.log = Logging.getLogger("Node " + this.getID() + ": ");
  }

  @Override
  public void handleMessages(Inbox inbox) {

    while (inbox.hasNext()) {
      Message msg = inbox.next();
      if (msg instanceof EMessage) {
        EMessage emsg = (EMessage) msg;
        switch (emsg.getType()) {
        case ELECTION:
          treatElectionMessage(emsg);
          break;

        case COORDENATOR:
          if (emsg.getId() != this.getID()){
            System.out.println(String.format("Node %d setting elected variable to %d.", this.getID(), emsg.getId()));
            this.isParticipant = false;
            this.elected = emsg.getId();
            send(msg, successor);
          } else {
            System.out.println("Election terminated!");
          }
          break;

        default:
        }
      }
    }
  }

  public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
    Color nodeColor = this.elected == this.getID() ? Color.RED : Color.BLUE;
    Color textColor = Color.WHITE;
    String text = "" + this.getID();

    this.setColor(nodeColor);
    super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, textColor);
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
      System.out.println(String.format("Node %d seding message to %d.", this.getID(), successor.getID()));
      send(msg, successor);
    } else if (msg.getId() < this.getID()) {
      System.out.println(String.format("Node %d is participant, sending message with his ID to %d.", this.getID(), successor.getID()));
      if (!this.isParticipant) {
        this.isParticipant = true;
        msg.setId(this.getID());
        send(msg, successor);
      }
    } else { // Id igual
      System.out.println(String.format("Node %d is the winner, start sending coord message.", this.getID()));
      EMessage coordMessage = new EMessage(this.getID(), MessageType.COORDENATOR);
      send(coordMessage, successor);
    }
  }

  public String toString() {
    return "Node " + this.getID();
  }

  public void preStep() {
  }

  public void init() {
  }

  @Override
  public void neighborhoodChange() {
    
    successor = null;
    Connections nodeConnections = this.getOutgoingConnections();
    ENode firstConnectionNode = (ENode) nodeConnections.iterator().next().getEndNode();

    for (Edge edge : nodeConnections) {
      ENode endNode = (ENode) edge.getEndNode();
      successor = successor == null ? endNode : // Initial
        endNode.compareTo(this) < 0 ? successor :   // More than self
        endNode.compareTo(successor) > 0 ? successor : endNode; // Less than current

      System.out.println(String.format("Node %d: comparing successor %d with node %d", this.getID(), successor.getID(), endNode.getID()));
    }

    // TODO: treat for the case the node is the biggest in ring

    System.out.println(String.format("Sucessor do nó  %d mudou para %d.", this.getID(), successor.getID()));
  }

  public void postStep() {
  }

  public void checkRequirements() {
  }

  public void compute() {
  }

  @NodePopupMethod(menuText = "Start Election")
  public void startElection() {
    EMessage msg = new EMessage(this.getID(), MessageType.ELECTION);
    // MessageTimer msgTimer = new MessageTimer(msg, successor);
    ETimer timer = new ETimer(this, successor, 1);
    timer.startRelative(1, this);
    Tools.appendToOutput("Start Routing from node " + this.getID() + "\n");
  }
}