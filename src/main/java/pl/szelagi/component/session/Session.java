/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.session;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.buildin.controller.HideOtherPlayers;
import pl.szelagi.buildin.system.LoadingBoard;
import pl.szelagi.buildin.system.SessionWatchDog;
import pl.szelagi.buildin.system.sessionSavePlayers.SessionSavePlayers;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.StartException;
import pl.szelagi.component.baseComponent.StopException;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.baseComponent.internalEvent.player.*;
import pl.szelagi.component.baseComponent.internalEvent.playerRequest.PlayerJoinRequest;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.bukkitEvent.SessionStartEvent;
import pl.szelagi.component.session.bukkitEvent.SessionStopEvent;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.recovery.Recovery;
import pl.szelagi.recovery.internalEvent.PlayerRecovery;

import java.util.ArrayList;
import java.util.List;

public abstract class Session extends BaseComponent {
    private final Recovery recovery;
    private final ArrayList<Player> players = new ArrayList<>();
    private Board currentBoard;

    public Session(JavaPlugin plugin) {
        super(plugin);
        this.recovery = new Recovery(this);
    }

    @Override
    public final void start() throws StartException {
        super.start();

        currentBoard = new LoadingBoard(this);
        currentBoard.start(false, null);

        setBoard(defaultBoard());

        var event = new SessionStartEvent(this);
        callBukkit(event);
    }

    @Override
    public final void stop() throws StopException {
        // Usuwanie wszystkich graczy przed rozpoczęciem wyłączanie sesji.
        // Inaczej metoda super.stop(), wywoła destruktory graczy InvokeType SELF.
        var playersCopy = new ArrayList<>(players);
        for (var player : playersCopy) {
            removePlayer(player, Cause.SESSION_END);
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
        if (SessionManager.inSession(player)) {
            throw new PlayerJoinException("Player " + player.getName() + " is already in session");
        }

        var joinRequestEvent = new PlayerJoinRequest(player, players);
        callOldToYoung(joinRequestEvent);

        if (joinRequestEvent.isCanceled()) {
            var cancelCause = joinRequestEvent.getCancelCause();
            // Jeżeli isCanceled to cancelCause musi być zdefiniowane
            assert cancelCause != null;
            throw new PlayerJoinException("Player " + player.getName() + " join canceled! Reason: " + cancelCause.message());
        }

        // Dodaj relację gracza z sesją
        SessionManager.addRelation(player, this);
        // Dodaj gracza do listy graczy sesji
        players.add(player);

        // wywołaj event o dołączeniu gracza
        var prevPlayers = players.stream().filter(fp -> !fp.equals(player)).toList();
        var newPlayers = players();
        var playerConstructorEvent = new PlayerChangeConstructor(player, newPlayers, prevPlayers, newPlayers);
        callOldToYoung(playerConstructorEvent);

        // zarejestruj gracza w recovery (dla każdego komponentu)
        var recovery = session().recovery();
        var recoveryEvent = new PlayerRecovery(player);
        callOldToYoung(recoveryEvent);
        recovery.updatePlayer(recoveryEvent);
    }

    public final void removePlayer(Player player) throws PlayerQuitException {
        removePlayer(player, Cause.OTHER);
    }

    private void removePlayer(Player player, Cause cause) throws PlayerQuitException {
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

        var event = new PlayerChangeDestructor(player, prevPlayers, prevPlayers, newPlayers, cause);
        callYoungToOld(event);

        // usuń relację o graczu w managerze
        SessionManager.removeRelation(player);

        // usuń gracza z listy graczy w sesji
        players.remove(player);
    }

    @MustBeInvokedByOverriders
    public final void setBoard(Board board) {
        board.start(true, () -> {
            currentBoard.stop();
            currentBoard = board;
        });
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        new SessionWatchDog(this).start();
        new SessionSavePlayers(this).start();
        new HideOtherPlayers(this).start();
    }

    protected abstract @NotNull Board defaultBoard();

    @Override
    public final @NotNull List<Player> players() {
        return players;
    }

    @Override
    public final @NotNull Session session() {
        return this;
    }

    @Override
    public final @Nullable Board board() {
        return currentBoard;
    }

    @Override
    public String defineDirectoryPath() {
        return "session/" + name();
    }

    public final Recovery recovery() {
        return recovery;
    }
}