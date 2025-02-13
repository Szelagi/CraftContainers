/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.state.manual.ManualContainerException;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Container<I, S> implements Serializable, Iterable<S> {
	private final ManualContainer<I, S> manualContainer;
	private final @NotNull Function<I, S> creator;

	public Container(@NotNull Function<I, S> creator) {
		this.manualContainer = new ManualContainer<>();
		this.creator = creator;
	}

	public @NotNull S getOrCreate(I input) throws ManualContainerException {
		if (manualContainer.isExists(input))
			return manualContainer.get(input);
		return manualContainer.create(input, creator);
	}

	public @NotNull S get(I input) throws ManualContainerException {
		return manualContainer.get(input);
	}

	public void clearState(I input) throws ManualContainerException {
		if (manualContainer.isExists(input))
			manualContainer.remove(input);
	}

	public @Nullable S find(Predicate<S> predicate) {
		return manualContainer.find(predicate);
	}

	public <R> @NotNull List<R> map(Function<? super S, ? extends R> mapper) {
		return manualContainer.map(mapper);
	}

	public @NotNull S create(@NotNull I input, @NotNull Function<I, S> creator) throws ManualContainerException {
		return manualContainer.create(input, creator);
	}

	public @NotNull S create(@NotNull I input) throws ManualContainerException {
		return manualContainer.create(input, creator);
	}

	public @NotNull Function<I, S> getCreator() {
		return creator;
	}

	@NotNull
	@Override
	public Iterator<S> iterator() {
		return manualContainer.iterator();
	}
}
