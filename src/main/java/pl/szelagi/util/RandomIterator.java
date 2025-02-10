/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util;

import java.util.*;

public class RandomIterator<T> implements Iterator<T> {
    private final List<T> shuffledElements;
    private int currentIndex;

    public RandomIterator(Collection<T> collection) {
        List<T> elements = new ArrayList<>(collection);
        Random random = new Random();
        this.shuffledElements = new ArrayList<>(elements);
        Collections.shuffle(shuffledElements, random);
        this.currentIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < shuffledElements.size();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return shuffledElements.get(currentIndex++);
    }

}
