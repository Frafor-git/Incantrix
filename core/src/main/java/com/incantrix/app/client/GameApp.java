package com.incantrix.app.client;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameApp extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}
