package net.alcuria.online.client;

import net.alcuria.online.client.OnlineRPG;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(416, 240);
		return cfg;
	}

	@Override
	public ApplicationListener getApplicationListener () {
		return new OnlineRPG();
	}
}