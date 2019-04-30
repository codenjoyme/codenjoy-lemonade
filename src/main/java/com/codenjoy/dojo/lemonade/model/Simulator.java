package com.codenjoy.dojo.lemonade.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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

import java.text.DecimalFormat;
import java.util.Random;

public class Simulator {

    private final double P9 = 10;
    private final double S3 = 0.15;  // SIGN COST
    private final double S2 = 30;
    private final double A2 = 2.0;  // INITIAL GLASS OF LEMONADE COST
    private final double C9 = 0.5;
    private final double C2 = 1;

    private Random rand;

    private int D;  // Day number
    private double A;  //Assets
    private double C;  // Cost of lemonade, cents
    private StringBuffer messages;
    private int SC;  // Weather forecast
    private double R1;
    private int R2;
    private int L;  // Lemonade glasses made
    private int S;  // Signs made
    private double P;  // Lemonade price
    private int N2;  // Lemonade glasses sold
    private double M;
    private double E;
    private double P1;
    private boolean B;  // Bankrupt flag

    public Simulator(long randomSeed) {
        if (randomSeed == 0)
            randomSeed = System.currentTimeMillis();
        rand = new Random(randomSeed);
        messages = new StringBuffer();

        reset();
    }

    public int getDay() {
        return D;
    }

    public double getAssets() {
        return A;
    }

    public double getLemonadeCost() {
        return C * 0.01;
    }

    public String getWeatherForecast() {
        if (SC == 2) {
            return ("SUNNY");
        } else if (SC == 10) {
            return ("CLOUDY");
        } else if (SC == 7) {
            return ("HOT AND DRY");
        }
        return "UNKNOWN";
    }

    public int getLemonadeMade() {
        return L;
    }

    public int getSignsMade() {
        return S;
    }

    public double getLemonadePrice() {
        return P * 0.01;
    }

    public int getLemonadeSold() {
        return N2;
    }

    public double getIncome() {
        return M;
    }

    public double getExpenses() {
        return E;
    }

    public double getProfit() {
        return P1;
    }

    public boolean isBankrupt() {
        return B;
    }

    public String getMessages() {
        return messages.toString();
    }

    public void reset() {
        D = 0;
        A = A2;
        C = 2;
        SC = 0;
        R1 = 1;
        R2 = 0;
        L = 0;
        S = 0;
        P = 0;
        N2 = 0;
        M = 0;
        E = 0;
        P1 = 0;
        B = false;

        messages.setLength(0);

        // START OF GAME, TITLE PAGE
        messages.append("\n");
        messages.append("HI! WELCOME TO LEMONSVILLE, CALIFORNIA!\n");
        messages.append("\n");
        messages.append("IN THIS SMALL TOWN, YOU ARE IN CHARGE OF RUNNING YOUR OWN LEMONADE STAND.\n");
        messages.append("HOW MUCH PROFIT YOU MAKE IS UP TO YOU.\n");
        messages.append("IF YOU MAKE THE MOST MONEY, YOU'RE THE WINNER!!\n");
        messages.append("\n");
        messages.append("TO MANAGE YOUR LEMONADE STAND, YOU WILL NEED TO MAKE THESE DECISIONS EVERY DAY:\n");
        messages.append("1. HOW MANY GLASSES OF LEMONADE TO MAKE (ONLY ONE BATCH IS MADE EACH MORNING)\n");
        messages.append("2. HOW MANY ADVERTISING SIGNS TO MAKE (THE SIGNS COST FIFTEEN CENTS EACH)\n");
        messages.append("3. WHAT PRICE TO CHARGE FOR EACH GLASS\n");
        messages.append("\n");
        messages.append("YOU WILL BEGIN WITH $2.00 CASH (ASSETS).");
        messages.append("BECAUSE YOUR MOTHER GAVE YOU SOME SUGAR,\nYOUR COST TO MAKE LEMONADE IS ");
        messages.append("$0.02 (TWO CENTS A GLASS, THIS MAY CHANGE IN THE FUTURE).\n");
        messages.append("\n");
        messages.append("YOUR EXPENSES ARE THE SUM OF THE COST OF THE LEMONADE AND THE COST OF THE SIGNS.\n");
        messages.append("YOUR PROFITS ARE THE DIFFERENCE BETWEEN THE INCOME FROM SALES AND YOUR EXPENSES.\n");
        messages.append("THE NUMBER OF GLASSES YOU SELL EACH DAY DEPENDS ON THE PRICE YOU CHARGE, AND ON\n");
        messages.append("THE NUMBER OF ADVERTISING SIGNS YOU USE.\n");
        messages.append("KEEP TRACK OF YOUR ASSETS, BECAUSE YOU CAN'T SPEND MORE MONEY THAN YOU HAVE!\n");
        messages.append("\n");
    }

    public void step(int lemonadeToMake, int signsToMake, int lemonadePriceCents) {
        messages.setLength(0);

        boolean inputError = false;
        double STI = A;
        if (lemonadeToMake < 0 || lemonadeToMake > 1000) {
            messages.append("lemonadeToMake parameter should be in [0, 1000] range.\n");
            inputError = true;
        }
        if (signsToMake < 0 || signsToMake > 50) {
            messages.append("signsToMake parameter should be in [0, 50] range.\n");
            inputError = true;
        }
        if (lemonadePriceCents < 0 || lemonadePriceCents > 100) {
            messages.append("lemonadePriceCents parameter should be in [0, 100] range.\n");
            inputError = true;
        }

        A = A + .000000001; // reduce aberration
        double C1 = C * .01;  // Cost of lemonade, dollars
        if (lemonadeToMake * C1 > A) {
            messages.append("THINK AGAIN!!! YOU HAVE ONLY " + formatCurrency(A) + "\n");
            messages.append("IN CASH AND TO MAKE " + lemonadeToMake + " GLASSES OF\n");
            messages.append("LEMONADE YOU NEED " + formatCurrency(lemonadeToMake * C1) + " IN CASH.\n");
            inputError = true;
        }

        if (signsToMake * S3 > A - lemonadeToMake * C1) {
            messages.append("THINK AGAIN!!! YOU HAVE ONLY " + formatCurrency(A - lemonadeToMake * C1) + "\n");
            messages.append("IN CASH LEFT AFTER MAKING YOUR LEMONADE.\n");
            messages.append("YOU CANNOT MAKE " + signsToMake + " SIGNS.");
            inputError = true;
        }

        if (inputError)
            return;

        L = lemonadeToMake;  // How many glasses to make
        S = signsToMake;  // How many signs to make
        double G = 1;
        P = lemonadePriceCents;  // Price for a glass of lemonade

        // 1120 PRINT : IF SC = 10 AND RND (1) < .25 THEN 2300
        messages.append("\n");
        if (SC == 10 && rand.nextDouble() < 0.25) {  // THUNDERSTORM!
            //X3 = 1;
            SC = 5;
            messages.append("WEATHER REPORT: A SEVERE THUNDERSTORM HIT LEMONSVILLE EARLIER TODAY, JUST AS\n");
            messages.append("THE LEMONADE STANDS WERE BEING SET UP. UNFORTUNATELY, EVERYTHING WAS RUINED!!\n");
            G = 0;
        }

        messages.append("** LEMONSVILLE DAILY FINANCIAL REPORT, DAY " + D + " **\n");
        // CALCULATE PROFITS
        if (R2 == 2) { // IF R2 = 2 THEN 2290
            messages.append("THE STREET CREWS BOUGHT ALL YOUR LEMONADE AT LUNCHTIME!!\n");
        }
        {  // loop in lines 1185..1390
            if (R2 == 2) {
                N2 = L;
            } else {
                if (A < 0) {
                    A = 0;
                }
                double N1;
                if (P < P9) {
                    N1 = (P9 - P) / P9 * 0.8 * S2 + S2;
                } else {
                    N1 = ((Math.pow(P9, 2)) * S2 / Math.pow(P, 2));
                }
                double W = -S * C9;
                double V = 1 - (Math.exp(W) * C2);
                N2 = (int) Math.floor(R1 * (N1 + (N1 * V)));
                N2 = (int) Math.floor(N2 * G);  // N2 = INT (N2 * G(I))
                if (N2 > L) {
                    N2 = L;
                }
            }

            M = N2 * P * 0.01;
            E = S * S3 + L * (C * 0.01);  // EXPENSES
            P1 = M - E;  // PROFIT
            A = A + P1;

            messages.append("GLASSES SOLD: " + N2 + ", PRICE " + formatCurrency(P / 100.0) + " PER GLASS\n");
            messages.append("INCOME:   " + formatCurrency(M) + "\n");
            messages.append("GLASSES MADE: " + L + ", SIGNS MADE: " + S + "\n");
            messages.append("EXPENSES: " + formatCurrency(E) + "\n");
            messages.append("PROFIT:   " + formatCurrency(P1) + "\n");
            messages.append("ASSETS:   " + formatCurrency(A) + "\n");
            messages.append("\n");

            if (A <= C / 100) {
                messages.append("YOU DON'T HAVE ENOUGH MONEY LEFT TO STAY IN BUSINESS YOU'RE BANKRUPT!\n");
                B = true;
                return;
            }
        }
        R1 = 1;
        R2 = 0;

        messages.append("YOUR ASSETS: " + formatCurrency(A) + "\n");

        // WEATHER
        double SCd = rand.nextDouble();
        if (SCd < 0.6) {
            SC = 2;
        } else if (SCd < 0.8) {
            SC = 10;
        } else {
            SC = 7;
        }
        messages.append("LEMONSVILLE WEATHER REPORT:  " + getWeatherForecast() + "\n");

        // START OF NEW DAY
        D = D + 1;

        C = 2;  // Cost of lemonade, cents
        if (D > 2) {
            C = 4;
        }
        if (D > 6) {
            C = 5;
        }
        // double C1 = C * 0.01;  // Cost of lemonade, dollars
        messages.append("ON DAY " + D + ", THE COST OF LEMONADE IS " + formatCurrency(C1) + "\n");

        // CURRENT EVENTS
        if (D == 3) {
            messages.append("(YOUR MOTHER QUIT GIVING YOU FREE SUGAR)\n");
        } else if (D == 7) {
            messages.append("(THE PRICE OF LEMONADE MIX JUST WENT UP)\n");
        }
        // AFTER 2 DAYS THINGS CAN HAPPEN
        if (D > 2) {  // 2000 REM RANDOM EVENTS
            if (SC == 10) {  // 2010 IF SC = 10 THEN 2110
                int J = 30 + (int) Math.floor(rand.nextDouble() * 5) * 10;  // 30, 40, 50, 60, 70 %
                messages.append("THERE IS A " + J + "% CHANCE OF LIGHT RAIN, AND THE WEATHER IS COOLER TODAY.\n");
                R1 = 1 - J / 100.0;
                //X1 = 1;
            } else if (SC == 7) {  // 2030 IF SC = 7 THEN 2410
                //X4 = 1;
                messages.append("A HEAT WAVE IS PREDICTED FOR TODAY!");
                R1 = 2;
            } else if (rand.nextDouble() < 0.25) {  // 2040 IF RND (1) < .25 THEN 2210
                messages.append("THE STREET DEPARTMENT IS WORKING TODAY. THERE WILL BE NO TRAFFIC ON YOUR STREET.\n");
                if (rand.nextDouble() >= 0.5) {
                    R2 = 2;
                } else {
                    R1 = 0.1;
                }
                //X2 = 1;
            }
        }
    }

    private static DecimalFormat formatter = new DecimalFormat("0.00");

    private static String formatCurrency(double value) {
        return "$" + formatter.format(value);
    }
}
