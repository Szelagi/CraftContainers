/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command;

import pl.szelagi.SessionAPI;
import pl.szelagi.command.debug.DebugSessionCommand;
import pl.szelagi.command.debug.SelfTestCommand;
import pl.szelagi.command.debug.TestSessionCommand;
import pl.szelagi.command.editor.EditBoardCommand;
import pl.szelagi.command.editor.ExitBoardCommand;
import pl.szelagi.command.editor.ListBoardsCommand;
import pl.szelagi.command.editor.SaveBoardCommand;
import pl.szelagi.command.info.SessionapiCommand;
import pl.szelagi.command.manage.JoinSessionCommand;
import pl.szelagi.command.manage.LeaveSessionCommand;
import pl.szelagi.command.manage.PlayerSessionCommand;
import pl.szelagi.command.manage.StopSessionCommand;


public class Command {
	public static void registerCommands() {
		var sapi = SessionAPI.getInstance();

		// sessionapi
		var sessionapi = sapi.getCommand("sessionapi");
		assert sessionapi != null;
		var sessionapiResolver = new SessionapiCommand();
		sessionapi.setExecutor(sessionapiResolver);

		// board-edit
		var boardEdit = sapi.getCommand("board-edit");
		assert boardEdit != null;
		var boardEditResolver = new EditBoardCommand();
		boardEdit.setExecutor(boardEditResolver);
		boardEdit.setTabCompleter(boardEditResolver);

		// board-exit
		var boardExit = sapi.getCommand("board-exit");
		assert boardExit != null;
		var boardExitResolver = new ExitBoardCommand();
		boardExit.setExecutor(boardExitResolver);

		// board-save
		var boardSave = sapi.getCommand("board-save");
		assert boardSave != null;
		var boardSaveResolver = new SaveBoardCommand();
		boardSave.setExecutor(boardSaveResolver);

		// board-list
		var boardList = sapi.getCommand("board-list");
		assert boardList != null;
		var boardListResolver = new ListBoardsCommand();
		boardList.setExecutor(boardListResolver);

		// session-join
		var sessionJoin = sapi.getCommand("session-join");
		assert sessionJoin != null;
		var sessionJoinResolver = new JoinSessionCommand();
		sessionJoin.setExecutor(sessionJoinResolver);
		sessionJoin.setTabCompleter(sessionJoinResolver);

		// session-leave
		var sessionLeave = sapi.getCommand("session-leave");
		assert sessionLeave != null;
		var sessionLeaveResolver = new LeaveSessionCommand();
		sessionLeave.setExecutor(sessionLeaveResolver);

		// session-stop
		var sessionStop = sapi.getCommand("session-stop");
		assert sessionStop != null;
		var sessionStopResolver = new StopSessionCommand();
		sessionStop.setExecutor(sessionStopResolver);
		sessionStop.setTabCompleter(sessionStopResolver);

		// session-add-player
		var sessionAddPlayer = sapi.getCommand("session-add-player");
		assert sessionAddPlayer != null;
		var sessionAddPlayerResolver = new PlayerSessionCommand();
		sessionAddPlayer.setExecutor(sessionAddPlayerResolver);
		sessionAddPlayer.setTabCompleter(sessionAddPlayerResolver);

		// session-remove-player
		var sessionRemovePlayer = sapi.getCommand("session-remove-player");
		assert sessionRemovePlayer != null;
		var sessionRemovePlayerResolver = new PlayerSessionCommand();
		sessionRemovePlayer.setExecutor(sessionRemovePlayerResolver);
		sessionRemovePlayer.setTabCompleter(sessionRemovePlayerResolver);

		// session-test
		var sessionTest = sapi.getCommand("session-test");
		assert sessionTest != null;
		var sessionTestResolver = new TestSessionCommand();
		sessionTest.setExecutor(sessionTestResolver);

		// session-debug
		var sessionDebug = sapi.getCommand("session-debug");
		assert sessionDebug != null;
		var sessionDebugResolver = new DebugSessionCommand();
		sessionDebug.setExecutor(sessionDebugResolver);

		// session-selftest
		var selfTest = sapi.getCommand("session-selftest");
		assert selfTest != null;
		var selfTestResolver = new SelfTestCommand();
		selfTest.setExecutor(selfTestResolver);
	}
}