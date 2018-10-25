/*
BSD 3-Clause License

Copyright (c) 2007-2013, Distributed Computing Group (DCG)
                         ETH Zurich
                         Switzerland
                         dcg.ethz.ch
              2017-2018, André Brait

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the copyright holder nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package projects.sample2;

import projects.sample2.nodes.nodeImplementations.S2Node;
import sinalgo.nodes.Node;
import sinalgo.runtime.AbstractCustomGlobal;
import sinalgo.tools.Tools;

import javax.swing.*;
import java.lang.reflect.Method;

/**
 * This class holds customized global state and methods for the framework. The
 * only mandatory method to overwrite is <code>hasTerminated</code> <br>
 * Optional methods to override are
 * <ul>
 * <li><code>customPaint</code></li>
 * <li><code>handleEmptyEventQueue</code></li>
 * <li><code>onExit</code></li>
 * <li><code>preRun</code></li>
 * <li><code>preRound</code></li>
 * <li><code>postRound</code></li>
 * <li><code>checkProjectRequirements</code></li>
 * </ul>
 *
 * @see sinalgo.runtime.AbstractCustomGlobal for more details. <br>
 * In addition, this class also provides the possibility to extend the
 * framework with custom methods that can be called either through the menu
 * or via a button that is added to the GUI.
 */
public class CustomGlobal extends AbstractCustomGlobal {

    @Override
    public boolean hasTerminated() {
        return false;
    }

    /**
     * An example of a method that will be available through the menu of the GUI.
     */
    @AbstractCustomGlobal.GlobalMethod(menuText = "Echo")
    public void echo() {
        // Query the user for an input
        String answer = JOptionPane.showInputDialog(null, "This is an example.\nType in any text to echo.");
        // Show an information message
        JOptionPane.showMessageDialog(null, "You typed '" + answer + "'", "Example Echo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void postRound() {

    }

    @Override
    public void preRun() {
        // A method called at startup, before the first round is executed.
    }

    /**
     * Reset all nodes, s.t. they forget their history.
     */
    @GlobalMethod(menuText = "reset")
    public void reset() {
        for (Node n : Tools.getNodeList()) {
            ((S2Node) n).reset();
        }
        Tools.repaintGUI();
    }

    @Override
    public String includeGlobalMethodInMenu(Method m, String defaultText) {
        if (m.getName().equals("reset")) {
            int size = Tools.getNodeList().size();
            if (size == 0) {
                return null;
            } else {
                return "Reset all " + Tools.getNodeList().size() + " nodes"; // a context sensitive menu entry
            }
        }
        return defaultText;
    }
}
