/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util;

import java.util.*;

/**
 * Iterator that returns elements of a collection in random order.
 *
 * @param <T> the type of elements returned by this iterator
 */
public class RandomIterator<T> implements Iterator<T> {
    private final List<T> shuffledElements;
    private int currentIndex;

    /**
     * Creates a new iterator that iterates over the given collection in random order.
     *
     * @param collection the collection to iterate randomly
     * @return a new {@link RandomIterator} instance
     * @param <T> the type of elements in the collection
     */
    public static <T> RandomIterator<T> from(Collection<T> collection) {
        return new RandomIterator<>(collection);
    }

    /**
     * Creates a new iterator that iterates over the given collection in random order.
     *
     * @param collection the collection to iterate randomly
     */
    public RandomIterator(Collection<T> collection) {
        List<T> elements = new ArrayList<>(collection);
        Random random = new Random();
        this.shuffledElements = new ArrayList<>(elements);
        Collections.shuffle(shuffledElements, random);
        this.currentIndex = 0;
    }

    /**
     * Returns true if there are more elements to iterate.
     *
     * @return true if more elements exist
     */
    @Override
    public boolean hasNext() {
        return currentIndex < shuffledElements.size();
    }

    /**
     * Returns the next element in random order.
     *
     * @return the next element
     * @throws NoSuchElementException if no more elements
     */
    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return shuffledElements.get(currentIndex++);
    }

}
