package projects.election.nodes.timers;

import projects.election.nodes.nodeImplementations.ENode;
import projects.election.nodes.messages.EMessage;
import projects.election.enums.MessageType;
import sinalgo.nodes.timers.Timer;

/* Description of Timer */
public class ETimer extends Timer {
  ENode sender, receiver;
  int interval;

  public ETimer(ENode sender, ENode receiver, int interval) {
    this.sender = sender;
    this.receiver = receiver;
    this.interval = interval;
  }

  /**
   * This function execute when called timer.startRelative(x, node), after x rounds
   */
  public void fire() {
    EMessage msg = new EMessage(sender.getID(), MessageType.ELECTION);
    System.out.println(String.format("Node %d sending to node %d.", sender.getID(), receiver.getID()));
    sender.send(msg, receiver);
  }
}