/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Utility class for generating integer sequences in various orders.
 */
public class Sequence {
    /**
     * Returns an iterator over integers from {@code start} (inclusive) to {@code end} (exclusive) in random order.
     *
     * @param start the first number in the range (inclusive)
     * @param end   the last number in the range (exclusive)
     * @return an iterator that returns the numbers in random order
     */
    public static Iterator<Integer> shuffledRange(int start, int end) {
        var numbers = new ArrayList<Integer>();
        for (int i = start; i < end; i++) {
            numbers.add(i);
        }
        return new RandomIterator<>(numbers);
    }
}
