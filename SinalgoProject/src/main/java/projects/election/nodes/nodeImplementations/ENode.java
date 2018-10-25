package projects.election.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import projects.election.nodes.timers.ETimer;
import projects.election.nodes.messages.EMessage;
import projects.election.enums.MessageType;
import sinalgo.nodes.messages.Message;
import sinalgo.nodes.Connections;
import sinalgo.nodes.edges.Edge;

public class ENode extends Node {

  private Node successor;
  private long elected;
  private boolean isParticipant;

  public ENode() {
    super();
    this.isParticipant = false;
  }

  public void handleMessages(Inbox inbox) {
    
    // O ConnectivityModel vai ter reajustado as conexões para ter somente 1, o próximo
    this.successor = findSuccessor(this.getOutgoingConnections());

    while(inbox.hasNext()) {
      Message msg = inbox.next();
      if(msg instanceof EMessage){
        EMessage emsg = (EMessage) msg; 
        switch(emsg.getType()){
          case ELECTION: 
            treatElectionMessage(emsg);
            break;

          case COORDENATOR:
            if(emsg.getId() != this.getID())
              this.isParticipant = false;
              this.elected = emsg.getId();
              send(msg, successor);
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
      send(msg, successor);
    } else if (msg.getId() < this.getID()) {
      if (!this.isParticipant) {
        this.isParticipant = true;
        msg.setId(this.getID());
        send(msg, successor);
      }
    } else { // Id igual
      EMessage coordMessage = new EMessage(this.getID(), MessageType.COORDENATOR);
      send(coordMessage, this.successor);
    }
  }

  public Node findSuccessor(Connections connections) {
    Node sucessor;
    for(Edge e : connections) {
      
    }
    return null;
  }

  public String toString() {
    return "Node " + this.getID();
  }

  public void preStep() {}

  public void init() {}

  public void neighborhoodChange() {}

  public void postStep() {}

  public void checkRequirements() {}

  public void compute() {}
}