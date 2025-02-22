/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.selfTest;

import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentDestructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerConstructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerDestructor;
import pl.szelagi.component.controller.Controller;

class NodeC extends Controller {
    private final TreeResult treeResult;
    private final String parentMessage;
    private final int index;

    public NodeC(BaseComponent baseComponent, TreeResult treeResult, String parentMessage, int index) {
        super(baseComponent);
        this.treeResult = treeResult;
        this.parentMessage = parentMessage;
        this.index = index;
    }

    public String message() {
        return parentMessage + 'C' + index;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        treeResult.constructorMessage.add(message());
    }

    @Override
    public void onComponentDestroy(ComponentDestructor event) {
        super.onComponentDestroy(event);
        treeResult.destructorMessage.add(message());
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        treeResult.playerConstructorMessage.add(message());
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        treeResult.playerDestructorMessage.add(message());
    }

}
