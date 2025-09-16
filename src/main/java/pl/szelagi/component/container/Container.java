/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.container;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.buildin.system.LoadingGameMap;
import pl.szelagi.buildin.system.sessionSavePlayers.SessionSavePlayers;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.base.StartException;
import pl.szelagi.component.base.StopException;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.playerRequest.PlayerJoinRequest;
import pl.szelagi.component.GameMap;
import pl.szelagi.event.bukkit.SessionStartEvent;
import pl.szelagi.event.bukkit.SessionStopEvent;
import pl.szelagi.event.internal.player.*;
import pl.szelagi.manager.ContainerManager;
import pl.szelagi.recovery.Recovery;
import pl.szelagi.recovery.internalEvent.PlayerRecovery;
import pl.szelagi.recovery.internalEvent.PlayerRecoveryCause;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Container extends Component {
    private final Recovery recovery;
    private final List<Player> players = new LinkedList<>();
    private GameMap currentGameMap;

    public Container(JavaPlugin plugin) {
        super(plugin);
        this.recovery = new Recovery(this);
    }

    @Override
    public final void start() throws StartException {
        super.start();

        currentGameMap = new LoadingGameMap(this);
        currentGameMap.start(false, null);

        setGameMap(defaultBoard());

        var event = new SessionStartEvent(this);
        callBukkit(event);
    }

    @Override
    public final void stop() throws StopException {
        // Usuwanie wszystkich graczy przed rozpoczęciem wyłączanie sesji.
        // Inaczej metoda super.stop(), wywoła destruktory graczy InvokeType SELF.
        var playersCopy = new ArrayList<>(players);
        for (var player : playersCopy) {
            removePlayer(player, PlayerDestroyCause.SESSION_DESTROY);
        }

        // mapa jest rodzicem sesji, więc zostanie też wyłączona
        super.stop();

        var event = new SessionStopEvent(this);
        callBukkit(event);
    }


    @MustBeInvokedByOverriders
    public final void addPlayer(Player player) throws PlayerJoinException {
        // Sprawdzanie, czy gracz jest żywy
        if (player.getHealth() <= 0) {
            throw new PlayerJoinException("Player " + player.getName() + " is not alive");
        }
        // Sprawdzenie, czy gracz nie znajduje się w innej sesji
        if (ContainerManager.inSession(player)) {
            throw new PlayerJoinException("Player " + player.getName() + " is already in session");
        }

        var joinRequestEvent = new PlayerJoinRequest(player, players);
        callSpecialization(joinRequestEvent);

        if (joinRequestEvent.isCanceled()) {
            var cancelCause = joinRequestEvent.getCancelCause();
            // Jeżeli isCanceled to cancelCause musi być zdefiniowane
            assert cancelCause != null;
            throw new PlayerJoinException("Player " + player.getName() + " join canceled! Reason: " + cancelCause.message());
        }

        // Dodaj relację gracza z sesją
        ContainerManager.addRelation(player, this);
        // Dodaj gracza do listy graczy sesji
        players.add(player);

        // wywołaj event o dołączeniu gracza
        var prevPlayers = players.stream().filter(fp -> !fp.equals(player)).toList();
        var newPlayers = players();
        var playerChange = new PlayerChange(prevPlayers, newPlayers);
        var playerConstructorEvent = new PlayerConstructor(player, newPlayers, PlayerInitCause.PLAYER_JOIN, playerChange);
        callSpecialization(playerConstructorEvent);

        // zarejestruj gracza w recovery (dla każdego komponentu)
        var recovery = container().recovery();
        var recoveryEvent = new PlayerRecovery(player, PlayerRecoveryCause.PLAYER_JOIN);
        callSpecialization(recoveryEvent);
        recovery.updatePlayer(recoveryEvent);
    }

    public final void removePlayer(Player player) throws PlayerQuitException {
        removePlayer(player, PlayerDestroyCause.PLAYER_QUIT);
    }

    private void removePlayer(Player player, PlayerDestroyCause cause) throws PlayerQuitException {
        // Sprawdzenie, czy gracz jest w sesji, z której jest usuwany.
        if (!players.contains(player)) {
            throw new PlayerQuitException("Player " + player.getName() + " is not in this session");
        }

        // wyrejestruj gracza z recovery
        recovery().destryPlayer(player);

        // wykonaj event o opuszczeniu gracza
        var prevPlayers = new ArrayList<>(players);
        prevPlayers.add(player);

        var newPlayers = players.stream().filter(fp -> !fp.equals(player)).toList();
        var playerChange = new PlayerChange(prevPlayers, newPlayers);
        var event = new PlayerDestructor(player, prevPlayers, cause, playerChange);
        callGeneralization(event);

        // usuń relację o graczu w managerze
        ContainerManager.removeRelation(player);

        // usuń gracza z listy graczy w sesji
        players.remove(player);
    }

    @MustBeInvokedByOverriders
    public final void setGameMap(GameMap gameMap) {
        gameMap.start(true, () -> {
            currentGameMap.stop();
            currentGameMap = gameMap;
        });
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        new SessionSavePlayers(this).start();
    }

    protected abstract @NotNull GameMap defaultBoard();

    @Override
    public final @NotNull List<Player> players() {
        return players;
    }

    @Override
    public @NotNull Container container() {
        return this;
    }

    @Override
    public final @Nullable GameMap gameMap() {
        return currentGameMap;
    }

    public final Recovery recovery() {
        return recovery;
    }
}