/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utility for reversing a Java Stream.
 * Consumes the stream and returns elements in reverse order.
 */
public class ReverseStream {
    /**
     * Reverses the given stream.
     * method from <a href="https://stackoverflow.com/questions/24010109/how-can-i-reverse-a-java-8-stream-and-generate-a-decrementing-intstream-of-value">...</a>
     */
    @SuppressWarnings("unchecked")
    public static <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream
                .range(0, temp.length)
                .mapToObj(i -> temp[temp.length - i - 1]);
    }
}
