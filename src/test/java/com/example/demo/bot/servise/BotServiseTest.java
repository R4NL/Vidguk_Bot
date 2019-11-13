package com.example.demo.bot.servise;

import org.junit.jupiter.api.Test;

class BotServiseTest {
    @Test
    void todayTest() {
        System.out.println(BotServise.Schedule.today("NTUU \"KPI\"", "io91mn"));
        System.out.println(BotServise.Schedule.tomorrow("NTUU \"KPI\"", ""));
        System.out.println(BotServise.Schedule.week("NTUU \"KPI\"", "io-91mp"));
    }

}