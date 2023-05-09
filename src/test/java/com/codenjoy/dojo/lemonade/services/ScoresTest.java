package com.codenjoy.dojo.lemonade.services;

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

import com.codenjoy.dojo.lemonade.TestGameSettings;
import com.codenjoy.dojo.services.event.EventObject;
import com.codenjoy.dojo.services.event.ScoresMap;
import com.codenjoy.dojo.utils.scorestest.AbstractScoresTest;
import org.junit.Test;

import static com.codenjoy.dojo.lemonade.services.GameSettings.Keys.*;

public class ScoresTest extends AbstractScoresTest {

    @Override
    public GameSettings settings() {
        return new TestGameSettings();
    }

    @Override
    protected Class<? extends ScoresMap> scores() {
        return Scores.class;
    }

    @Override
    protected Class<? extends EventObject> events() {
        return Event.class;
    }

    @Override
    protected Class<? extends Enum> eventTypes() {
        return Event.Type.class;
    }

    @Test
    public void shouldCollectScores() {
        // given
        // sets SUM_OF_PROFITS scores counting mode
        settings.integer(LIMIT_DAYS, 0);

        // when then
        assertEvents("100:\n" +
                "WIN,0.3D,0.3D > +30 = 130\n" +
                "WIN,0.3D,0.3D > +30 = 160\n" +
                "WIN,0.3D,0.3D > +30 = 190\n" +
                "WIN,0.3D,0.3D > +30 = 220\n" +
                "LOSE,1D,0.3D > -100 = 120");
    }

    @Test
    public void shouldCollectScores_whenWin_andSumOfProfitsScoreMode() {
        // given
        // sets SUM_OF_PROFITS scores counting mode
        settings.integer(LIMIT_DAYS, 0)
                .integer(WIN_SCORE, 10);

        // when then
        assertEvents("100:\n" +
                "WIN,0.3D,0.3D > +30 = 130\n" +
                "WIN,0.3D,0.3D > +30 = 160");
    }

    @Test
    public void shouldCollectScores_whenWin_andLastDayAssetsScoreMode() {
        // given
        // sets LAST_DAY_ASSETS scores counting mode
        settings.integer(LIMIT_DAYS, 10)
                .integer(WIN_SCORE, 10);

        // when then
        // increased only when assetsAfter is more than scores
        assertEvents("100:\n" +
                "WIN,0.3D,0.3D > +0 = 100\n" +
                "WIN,0.3D,1.1D > +10 = 110\n" +
                "WIN,0.3D,1.1D > +0 = 110");
    }

    @Test
    public void shouldCollectScores_whenLose_andSumOfProfitsScoreMode() {
        // given
        // sets SUM_OF_PROFITS scores counting mode
        settings.integer(LIMIT_DAYS, 0)
                .integer(LOSE_PENALTY, -10);

        // when then
        assertEvents("100:\n" +
                "LOSE,1D,0.3D > -100 = 0\n" +
                "LOSE,1D,0.3D > +0 = 0");
    }

    @Test
    public void shouldCollectScores_whenLose_andLastDayAssetsScoreMode() {
        // given
        // sets LAST_DAY_ASSETS scores counting mode
        settings.integer(LIMIT_DAYS, 10)
                .integer(LOSE_PENALTY, -10);

        // when then
        assertEvents("100:\n" +
                "LOSE,1D,0.3D > +0 = 100\n" +
                "LOSE,1D,0.3D > +0 = 100");
    }

    @Test
    public void shouldCollectScores_whenBankrupt() {
        // given
        settings.integer(BANKRUPT_PENALTY, -10);

        // when then
        assertEvents("100:\n" +
                "WIN,0.3D,0.3D > +30 = 130\n" +
                "WIN,0.3D,0.3D > +30 = 160\n" +
                "WIN,0.3D,0.3D > +30 = 190\n" +
                "LOSE,1D,0.3D > -10 = 180");
    }

    @Test
    public void shouldStillZeroAfterDead() {
        // given
        // sets SUM_OF_PROFITS scores counting mode
        settings.integer(LIMIT_DAYS, 0)
                .integer(LOSE_PENALTY, -10);

        // when then
        assertEvents("0:\n" +
                "LOSE,1D,0.3D > +0 = 0");
    }

    @Test
    public void shouldCleanScore() {
        assertEvents("100:\n" +
                "WIN,0.3D,0.3D > +30 = 130\n" +
                "(CLEAN) > -130 = 0\n" +
                "WIN,0.3D,0.3D > +30 = 30");
    }
}