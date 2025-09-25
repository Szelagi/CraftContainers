/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test;

public enum TestName {
    CONTAINER_START_STOP_TEST,
    CONTAINER_START_STOP_WITH_PLAYER_TEST,

    CONSTRUCTOR_TREE_TEST,
    DESTRUCTOR_TREE_TEST,
    PLAYER_CONSTRUCTOR_AFTER_TREE_TEST,
//    PLAYER_CONSTRUCTOR_BEFORE_TREE_TEST,
    PLAYER_DESTRUCTOR_AFTER_TREE_TEST,
    PLAYER_DESTRUCTOR_BEFORE_TREE_TEST,

    PLAYER_JOIN_REQUEST_TREE_TEST,
    CONTAINER_ADD_REMOVE_PLAYER,
    PLAYER_CONSTRUCTOR_WRAP_TEST
}
