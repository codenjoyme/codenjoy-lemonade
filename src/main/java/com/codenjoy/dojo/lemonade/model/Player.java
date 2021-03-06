package com.codenjoy.dojo.lemonade.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2022 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.lemonade.services.Event;
import com.codenjoy.dojo.lemonade.services.GameSettings;
import com.codenjoy.dojo.lemonade.services.ScoreMode;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;

import static com.codenjoy.dojo.lemonade.services.GameSettings.Keys.LIMIT_DAYS;

public class Player extends GamePlayer<Hero, GameField<Player, Hero>> {

    private Queue<SalesResult> history;
    private long heroRandomSeed;

    public Player(EventListener listener, long heroRandomSeed, GameSettings settings) {
        super(listener, settings);
        this.heroRandomSeed = heroRandomSeed;
        history = new LinkedList<>();
    }

    public void clearScore() {
        if (history != null) {
            history.clear();
        }

        if (hero != null) {
            hero.clear();
        }
    }

    @Override
    public Hero createHero(Point pt) {
        return new Hero(heroRandomSeed, history);
    }

    public JSONObject getNextQuestion() { // TODO test me
        return hero.getNextQuestion().toJson();
    }

    public JSONArray getHistoryJson() {
        JSONArray historyJson = new JSONArray();
        history.forEach(sr -> historyJson.put(sr.toJSONObject()));
        return historyJson;
    }

    public void checkAnswer() {
        hero.tick();
        SalesResult salesResult = hero.popSalesResult();

        // put to history and raise events if there is salesResult and no input errors
        if (salesResult != null && !salesResult.isInputError()) {
            int day = salesResult.getDay();
            boolean isLastDayAssetsGameMode = ((GameSettings)settings).scoreMode() == ScoreMode.LAST_DAY_ASSETS;
            if (isLastDayAssetsGameMode && day > settings.integer(LIMIT_DAYS)) {
                return;
            }

            history.add(salesResult);
            while (history.size() > 10)
                history.remove();
            if (salesResult.isBankrupt()) {
                event(new Event(Event.Type.LOSE,
                        salesResult.getProfit(),
                        salesResult.getAssetsAfter()));
            } else {
                // raise WIN event only on SUM_OF_PROFITS game mode OR on the last day in LAST_DAY_ASSETS game mode
                if (!isLastDayAssetsGameMode || day == settings.integer(LIMIT_DAYS)) {
                    event(new Event(Event.Type.WIN,
                            salesResult.getProfit(),
                            salesResult.getAssetsAfter()));
                }
            }
        }
    }

    public void updateSeed(int newSeed) {
        heroRandomSeed = newSeed;
    }
}
