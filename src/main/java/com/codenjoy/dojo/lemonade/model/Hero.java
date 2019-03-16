package com.codenjoy.dojo.lemonade.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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


import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.joystick.MessageJoystick;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Это реализация героя. Обрати внимание, что он имплементит {@see Joystick}, а значит может быть управляем фреймворком
 * Так же он имплементит {@see Tickable}, что значит - есть возможность его оповещать о каждом тике игры.
 * Эти интерфейсы объявлены в {@see PlayerHero}.
 */
public class Hero extends PlayerHero<Field> implements MessageJoystick {

    private Simulator simulator;
    private boolean alive;
    private String answer;

    public Hero() {
        simulator = new Simulator((int) System.currentTimeMillis());
        alive = true;
    }

    @Override
    public void init(Field field) {
        simulator.reset();

        this.field = field;
    }

    @Override
    public void message(String s) {
        this.answer = s;

        String command = s.toLowerCase();

        Pattern patternGo = Pattern.compile(
                "go\\s*(-?[\\d]+)[,\\s]\\s*(-?[\\d]+)[,\\s]\\s*(-?[\\d]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patternGo.matcher(command);
        if (matcher.matches()) {
            int lemonadeToMake = Integer.parseInt(matcher.group(1));
            int signsToMake = Integer.parseInt(matcher.group(2));
            int lemonadePriceCents = Integer.parseInt(matcher.group(3));
            simulate(lemonadeToMake, signsToMake, lemonadePriceCents);
            return;
        }
    }

    @Override
    public void tick() {
        if (!alive) return;
    }

    public boolean isAlive() {
        return alive;
    }

    public String popAnswer() {
        String answer = this.answer;
        this.answer = null;
        return answer;
    }

    private void simulate(int lemonadeToMake, int signsToMake, int lemonadePriceCents) {
        simulator.step(lemonadeToMake, signsToMake, lemonadePriceCents);
        //TODO
    }
}
