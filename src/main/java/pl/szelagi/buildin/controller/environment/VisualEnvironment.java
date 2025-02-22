/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.environment;

import org.bukkit.WeatherType;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerConstructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerDestructor;
import pl.szelagi.component.controller.Controller;

public class VisualEnvironment extends Controller {
	public static final Class<TimeType> TIME_TYPE = TimeType.class;
	private final long ticks;
	private final WeatherType weatherType;

	public VisualEnvironment(BaseComponent baseComponent, WeatherType weatherType, long ticks) {
		super(baseComponent);
		this.weatherType = weatherType;
		this.ticks = ticks;
	}

	public VisualEnvironment(BaseComponent baseComponent, WeatherType weatherType, TimeType timeType) {
		super(baseComponent);
		this.weatherType = weatherType;
		this.ticks = timeType.getTicks();
	}

	@Override
	public void onPlayerInit(PlayerConstructor event) {
		super.onPlayerInit(event);
		event.getPlayer()
				.setPlayerWeather(weatherType);
		event.getPlayer()
				.setPlayerTime(ticks, false);
	}

	@Override
	public void onPlayerDestroy(PlayerDestructor event) {
		super.onPlayerDestroy(event);
		event.getPlayer().resetPlayerWeather();
		event.getPlayer().resetPlayerTime();
	}

}
