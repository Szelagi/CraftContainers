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
import pl.szelagi.event.player.initialize.PlayerConstructorEvent;

class NodeB extends Controller {
    private final TreeResult treeResult;
    private final String parentMessage;
    private final int index;
    public NodeB(ISessionComponent sessionComponent, TreeResult treeResult, String parentMessage, int index) {
        super(sessionComponent);
        this.treeResult = treeResult;
        this.parentMessage = parentMessage;
        this.index = index;
    }

    public String message() {
        return parentMessage + 'B' + index;
    }

    @SAPIEventHandler
    public void init(ComponentConstructorEvent event) {
        treeResult.constructorMessage.add(message());
        for (int i = 1; i <= 2; i++) {
            new DummyNode(this).start();
            new NodeC(this, treeResult, message(), i).start();
            new DummyNode(this).start();
        }
    }

    @SAPIEventHandler
    public void destroy(ComponentConstructorEvent event) {
        treeResult.destructorMessage.add(message());
    }

    @SAPIEventHandler
    public void playerInit(PlayerConstructorEvent event) {
        treeResult.playerConstructorMessage.add(message());
    }

    @SAPIEventHandler
    public void playerDestroy(PlayerConstructorEvent event) {
        treeResult.playerDestructorMessage.add(message());
    }



}
