package com.domgarr.android.volleyballscorekeeper;

import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.UUID;

public class ScoreBoardGattAttributes {
    public static HashMap<String, UUID> scoreboard_services = new HashMap<>();
    public static final String SCOREBOARD_SERVICE_1 = "Scoreboard 1";
    public static final String SCOREBOARD_SERVICE_2 = "Green Scoreboard";


    static {
        scoreboard_services.put(SCOREBOARD_SERVICE_1, UUID.fromString("74e6fc68-dc9a-11e9-8a34-2a2ae2dbcce4"));
        scoreboard_services.put(SCOREBOARD_SERVICE_2, UUID.fromString("a7fe1050-e168-11e9-81b4-2a2ae2dbcce4"));
    }
}
