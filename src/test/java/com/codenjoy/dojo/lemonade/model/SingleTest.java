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


import com.codenjoy.dojo.lemonade.services.GameRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.multiplayer.Single;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.utils.JsonUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class SingleTest {

    private EventListener listener1;
    private EventListener listener2;
    private EventListener listener3;
    private Game game1;
    private Game game2;
    private Game game3;
    private Dice dice;
    private Lemonade field;

    private final String welcomeMessage = "\\nHI! WELCOME TO LEMONSVILLE, CALIFORNIA!\\n\\nIN THIS SMALL TOWN, YOU ARE IN CHARGE OF RUNNING YOUR OWN LEMONADE STAND.\\nHOW MUCH PROFIT YOU MAKE IS UP TO YOU.\\nIF YOU MAKE THE MOST MONEY, YOU'RE THE WINNER!!\\n\\nTO MANAGE YOUR LEMONADE STAND, YOU WILL NEED TO MAKE THESE DECISIONS EVERY DAY:\\n1. HOW MANY GLASSES OF LEMONADE TO MAKE (ONLY ONE BATCH IS MADE EACH MORNING)\\n2. HOW MANY ADVERTISING SIGNS TO MAKE (THE SIGNS COST FIFTEEN CENTS EACH)\\n3. WHAT PRICE TO CHARGE FOR EACH GLASS\\n\\nYOU WILL BEGIN WITH $2.00 CASH (ASSETS).BECAUSE YOUR MOTHER GAVE YOU SOME SUGAR,\\nYOUR COST TO MAKE LEMONADE IS $0.02 (TWO CENTS A GLASS, THIS MAY CHANGE IN THE FUTURE).\\n\\nYOUR EXPENSES ARE THE SUM OF THE COST OF THE LEMONADE AND THE COST OF THE SIGNS.\\nYOUR PROFITS ARE THE DIFFERENCE BETWEEN THE INCOME FROM SALES AND YOUR EXPENSES.\\nTHE NUMBER OF GLASSES YOU SELL EACH DAY DEPENDS ON THE PRICE YOU CHARGE, AND ON\\nTHE NUMBER OF ADVERTISING SIGNS YOU USE.\\nKEEP TRACK OF YOUR ASSETS, BECAUSE YOU CAN'T SPEND MORE MONEY THAN YOU HAVE!\\n\\n";
    private final String expectedDay0 = "{" +
            "'assets':2,'day':0,'history':[],'isBankrupt':false,'lemonadePrice':0," +
            "'messages':'" + welcomeMessage + "'," +
            "'weatherForecast':'UNKNOWN'" +
            "}";

    // появляется другие игроки, игра становится мультипользовательской
    @Before
    public void setup() {
        Level level = new LevelImpl(
                "question1=answer1",
                "question2=answer2",
                "question3=answer3");

        dice = mock(Dice.class);
        field = new Lemonade(level, dice);
        PrinterFactory factory = new GameRunner().getPrinterFactory();

        listener1 = mock(EventListener.class);
        game1 = new Single(new Player(listener1, 1), factory);
        game1.on(field);

        listener2 = mock(EventListener.class);
        game2 = new Single(new Player(listener2, 1), factory);
        game2.on(field);

        listener3 = mock(EventListener.class);
        game3 = new Single(new Player(listener3, 1), factory);
        game3.on(field);

        dice(1, 4);
        game1.newGame();

        dice(2, 2);
        game2.newGame();

        dice(3, 4);
        game3.newGame();
    }

    private void dice(int x, int y) {
        when(dice.next(anyInt())).thenReturn(x, y);
    }

    private void asrtFl1(String expected) {
        assertField(expected, game1);
    }

    private void assertField(String expected, Game game) {
        assertEquals(expected, JsonUtils.toStringSorted(game.getBoardAsString().toString()).replace('\"', '\''));
    }

    private void asrtFl2(String expected) {
        assertField(expected, game2);
    }

    private void asrtFl3(String expected) {
        assertField(expected, game3);
    }

    // рисуем несколько игроков
    @Test
    public void shouldPrint() {
        asrtFl1(expectedDay0);
        asrtFl2(expectedDay0);
        asrtFl3(expectedDay0);
    }

    // Каждый игрок может упраыляться за тик игры независимо,
    // все их последние ходы применяются после тика любой борды
    @Test
    public void shouldJoystick() {
        // when
        game1.getJoystick().message("wrong-message");
        game1.getJoystick().message("answer1");

        game2.getJoystick().message("answer2");

        game3.getJoystick().message("answer3");

        field.tick();

        // then
        asrtFl1(expectedDay0);

        asrtFl2(expectedDay0);

        asrtFl3(expectedDay0);
    }

    // игроков можно удалять из игры
    @Test
    public void shouldRemove() {
        game3.close();

        field.tick();

        asrtFl1(expectedDay0);
        asrtFl2(expectedDay0);
        try {
            asrtFl3(expectedDay0);
        } catch (IllegalStateException e) {
            assertEquals("No board for this player", e.getMessage());
        }
    }

    // игрока можно ресетнуть
    @Test
    public void shouldKill() {
        // given
        game1.getJoystick().message("answer1");
        game2.getJoystick().message("answer1");
        game3.getJoystick().message("answer1");

        field.tick();

        asrtFl1(expectedDay0);
        asrtFl2(expectedDay0);
        asrtFl3(expectedDay0);

        // when
        game1.newGame();
        field.tick();

        asrtFl1(expectedDay0);
        asrtFl2(expectedDay0);
        asrtFl3(expectedDay0);
    }

    // игрок может ответить правильно и неправильно
    @Test
    public void shouldEvents() {
        // given
        game1.getJoystick().message("answer1");
        game2.getJoystick().message("answer2");
        game3.getJoystick().message("answer3");

        field.tick();

        asrtFl1(expectedDay0);
        asrtFl2(expectedDay0);
        asrtFl3(expectedDay0);

        // then
        /*verify(listener1).event(Events.WIN);
        verify(listener2).event(Events.LOOSE);
        verify(listener3).event(Events.LOOSE);*/

        // when
        field.tick();

        // then
        verifyNoMoreInteractions(listener1);
        verifyNoMoreInteractions(listener2);
        verifyNoMoreInteractions(listener3);
    }
}
