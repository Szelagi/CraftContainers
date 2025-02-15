/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.selfTest;

import pl.szelagi.component.ISessionComponent;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.event.SAPIEventHandler;
import pl.szelagi.event.component.ComponentConstructorEvent;

import java.util.ArrayList;
import java.util.List;

class TreeRoot extends Controller {

    public static final List<String> CONS_EXPECTED_RESULT = new ArrayList<>();
    public static final List<String> DEST_EXPECTED_RESULT = new ArrayList<>();

    static {
        CONS_EXPECTED_RESULT.add("A1");
        CONS_EXPECTED_RESULT.add("A1B1");
        CONS_EXPECTED_RESULT.add("A1B1C1");
        CONS_EXPECTED_RESULT.add("A1B1C2");
        CONS_EXPECTED_RESULT.add("A1B2");
        CONS_EXPECTED_RESULT.add("A1B2C1");
        CONS_EXPECTED_RESULT.add("A1B2C2");
        CONS_EXPECTED_RESULT.add("A2");
        CONS_EXPECTED_RESULT.add("A2B1");
        CONS_EXPECTED_RESULT.add("A2B1C1");
        CONS_EXPECTED_RESULT.add("A2B1C2");
        CONS_EXPECTED_RESULT.add("A2B2");
        CONS_EXPECTED_RESULT.add("A2B2C1");
        CONS_EXPECTED_RESULT.add("A2B2C2");

        DEST_EXPECTED_RESULT.add("A2B2C2");
        DEST_EXPECTED_RESULT.add("A2B2C1");
        DEST_EXPECTED_RESULT.add("A2B2");
        DEST_EXPECTED_RESULT.add("A2B1C2");
        DEST_EXPECTED_RESULT.add("A2B1C1");
        DEST_EXPECTED_RESULT.add("A2B1");
        DEST_EXPECTED_RESULT.add("A2");
        DEST_EXPECTED_RESULT.add("A1B2C2");
        DEST_EXPECTED_RESULT.add("A1B2C1");
        DEST_EXPECTED_RESULT.add("A1B2");
        DEST_EXPECTED_RESULT.add("A1B1C2");
        DEST_EXPECTED_RESULT.add("A1B1C1");
        DEST_EXPECTED_RESULT.add("A1B1");
        DEST_EXPECTED_RESULT.add("A1");
    }

    private final TreeResult treeResult;

    public TreeRoot(ISessionComponent sessionComponent, TreeResult treeResult) {
        super(sessionComponent);
        this.treeResult = treeResult;
    }

    @Override
    @SAPIEventHandler
    public void componentConstructor(ComponentConstructorEvent event) {
        super.componentConstructor(event);

        for (int i = 1; i <= 2; i++) {
            new DummyNode(this).start();
            new NodeA(this, treeResult, "", i).start();
            new DummyNode(this).start();
        }
    }
}
