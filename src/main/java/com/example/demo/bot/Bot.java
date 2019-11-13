package com.example.demo.bot;

import com.example.demo.entity.User;
import com.example.demo.repositories.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Created by Thealeshka on 10.11.2019 inside the package - com.example.demo.bot
 */

@Component
public class Bot extends TelegramLongPollingBot {
    private String botName;
    private String botToken;
    private BotContext context;
    private BotState state;

    @Autowired
    private UserJpaRepository userJpaRepository;


    {
        initBotReferences();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            getTestMessage(update);
        } else if (update.hasCallbackQuery()) {
            System.out.println("111111111111111111111111111111");
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void initBotReferences() {
        String botNameInitializer = "";
        String botTokenInitializer = "";

        try {
            botNameInitializer = Files.readAllLines(Path.of("config/botToken.txt")).stream().filter(n -> n.contains("name"))
                    .map(s -> s.substring(s.indexOf("=") + 1)).collect(Collectors.joining());
            botTokenInitializer = Files.readAllLines(Path.of("config/botToken.txt")).stream().filter(n -> n.contains("token"))
                    .map(s -> s.substring(s.indexOf("=") + 1)).collect(Collectors.joining());
        } catch (IOException e) {
            e.printStackTrace();
        }
        botName = botNameInitializer;
        botToken = botTokenInitializer;
    }

    private User getUser(Update update) {
        User user = userJpaRepository.findByChatId(update.getMessage().getChatId());
        if (user == null) {
            state = BotState.getInstance();
            user = userJpaRepository.save(new User().setChatId(update.getMessage().getChatId())
                    .setStateId(state.ordinal()));
            context = BotContext.of(this, user, update.getMessage().getText());
            state.enter(context);
        } else {
            context = BotContext.of(this, user, update.getMessage().getText());
            state = BotState.byId(user.getStateId());
        }
        state.handleInput(context);
        return user;
    }

    private void getTestMessage(Update update) {
        User user = getUser(update);
        do {
            state = state.nextState();
            System.out.println(context);
            state.enter(context);
        } while (!state.isInputNeeded());
        user.setStateId(state.ordinal());
        user.setGroupInUni(context.getUser().getGroupInUni());
        userJpaRepository.save(user);
    }
}
