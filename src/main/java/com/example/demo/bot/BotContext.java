package com.example.demo.bot;

import com.example.demo.entity.User;

/**
 * Created by Thealeshka on 01.11.2019 inside the package - com.telegram.bot.bot
 */


public class BotContext {
    private final Bot bot;
    private final User user;
    private final String input;


    public BotContext(Bot bot, User user, String input) {
        this.bot = bot;
        this.user = user;
        this.input = input;
    }

    public static BotContext of(Bot bot, User user, String text) {
        return new BotContext(bot, user, text);
    }

    public Bot getBot() {
        return bot;
    }

    public User getUser() {
        return user;
    }

    public String getInput() {
        return input;
    }
}
