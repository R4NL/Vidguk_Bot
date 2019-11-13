package com.example.demo.bot;

import com.example.demo.bot.servise.BotServise;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public enum BotState {
    Start {
        @Override
        public void enter(BotContext context) {
            BotServise.sendMessage(context, fileTextReader("hello", "eng"));
        }

        @Override
        public BotState nextState() {
            return Wait;
        }
    },

    EnterLanguage {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            BotServise.KeyBord.setLanguage(context);
        }

        @Override
        public void handleInput(BotContext context) {

            if (checkLanguage(context.getInput())) {
                context.getUser().setLanguage(context.getInput());
                BotServise.sendMessage(context, fileTextReader("languageSet", context.getInput()));
                next = Wait;
            } else {
                next = Wait;
                BotServise.sendMessage(context, fileTextReader("noSuchLanguage", "eng"));
            }
        }

        @Override
        public BotState nextState() {
            return Wait;
        }

    },

    EnterUni {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            BotServise.KeyBord.setUni(context);
        }

        @Override
        public void handleInput(BotContext context) {
            if (BotServise.KeyBord.checkUniName(context.getInput())) {
                context.getUser().setUni(context.getInput() + "_U");
                next = context.getUser().getGroupInUni() == null ? EnterGroup : Wait;
            } else {
                next = EnterUni;
                BotServise.sendMessage(context, "you enter wrong Uni name");
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    EnterGroup() {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            if (context.getUser().getUni() == null) {
                next = EnterUni;
            } else {
                BotServise.KeyBord.setGroup(context, "Enter your group, please:");
            }
        }

        @Override
        public void handleInput(BotContext context) {
            if (BotServise.KeyBord.checkGroupName(context.getInput(), context)) {
                context.getUser().setGroupInUni(context.getInput());
                next = EndInputGroup;
            } else {
                next = EnterGroup;
                BotServise.sendMessage(context, "you enter wrong group name");
            }


        }


        @Override
        public BotState nextState() {
            return next;
        }
    },

    EndInputGroup(false) {
        @Override
        public void enter(BotContext context) {
            try {
                BotServise.sendMessage(context, Files.readString(Path.of("config/instruction.txt")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public BotState nextState() {
            return Wait;
        }
    },

    Wait {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            BotServise.KeyBord.waitEnter(context);
        }

        @Override
        public void handleInput(BotContext context) {
            next = Wait;
            switch (context.getInput()) {
                case "/today", "Today", "today" -> today(context);
                case "/tomorrow", "Tomorrow", "tomorrow" -> tomorrow(context);
                case "/week", "Week", "week" -> week(context);
                case "/university", "University", "university" -> next = EnterUni;
                case "/group", "Group", "group" -> next = (context.getUser().getUni() == null) ? EnterUni : EnterGroup;
                case "/language", "Language", "language" -> next = EnterLanguage;
                default -> unKnown(context);
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }

        private void today(BotContext context) {
            BotServise.sendMessage(context, BotServise.Schedule.today(context.getUser().getUni(), context.getUser().getGroupInUni()));
        }

        private void tomorrow(BotContext context) {
            BotServise.sendMessage(context, BotServise.Schedule.tomorrow(context.getUser().getUni(), context.getUser().getGroupInUni()));
        }

        private void unKnown(BotContext context) {
            BotServise.sendMessage(context, "Unknown command!");
        }

        private void week(BotContext context) {
            BotServise.sendMessage(context, BotServise.Schedule.week(context.getUser().getUni(), context.getUser().getGroupInUni()));
        }
    };


    private static List<BotState> states;
    private final boolean inputNeeded;


    BotState() {
        this.inputNeeded = true;
    }

    BotState(boolean inputNeeded) {
        this.inputNeeded = inputNeeded;
    }

    public static BotState getInstance() {
        return byId(0);
    }

    public static BotState byId(int i) {
        if (states == null) {
            states = Arrays.asList(BotState.values());
        }
        if (i >= states.size()) {
            throw new IllegalArgumentException("No such state.");
        }
        return states.get(i);
    }

    public static String languagePackChoose(String language) {
        if (checkLanguage(language)) {
            return language;
        } else {
            return "eng";
        }
    }

    private static boolean checkLanguage(String language) {
        try {
            return Files.walk(Path.of("config/language_pack/")).filter(Files::isRegularFile).map(Path::getFileName)
                    .map(n -> n.toString()).map(s -> s.substring(0, s.lastIndexOf(".")))
                    .map(n -> n.equalsIgnoreCase(language)).reduce((aBoolean, aBoolean2) -> aBoolean || aBoolean2).get();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String fileTextReader(String key, String language) {
        String res;
        try {
            res = Files.readAllLines(Path.of("config/language_pack/" + language + ".txt")).stream()
                    .filter(s -> s.substring(0, s.indexOf("=")).equalsIgnoreCase(key)).collect(Collectors.joining());
            res = res.substring(res.indexOf("=") + 1);
        } catch (IOException e) {
            res = "0000xxxx0000" + e.getMessage();
        }
        return res;
    }


    public boolean isInputNeeded() {
        return inputNeeded;
    }

    public void handleInput(BotContext context) {

    }

    public abstract void enter(BotContext context);

    public abstract BotState nextState();
}
