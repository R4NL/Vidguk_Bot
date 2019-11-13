package com.example.demo.bot.servise;

import com.example.demo.bot.BotContext;
import com.example.demo.servises.WeekDay;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Thealeshka on 13.11.2019 inside the package - com.example.demo.bot.servise
 */


public class BotServise {
    public static class Schedule {
        public static String today(String uni, String group) {
            try {
                return oneDay(getWeek(uni, group), WeekDay.today());
            } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
                return e.getMessage();
            }
        }

        public static String tomorrow(String uni, String group) {
            try {
                return oneDay(getWeek(uni, group), WeekDay.tomorrow());
            } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
                return e.getMessage();
            }
        }

        public static String week(String uni, String group) {
            try {
                String res = getWeek(uni, group);
                if (res.equals("")) {
                    return "No Classes found";
                } else {
                    return res;
                }
            } catch (IllegalArgumentException e) {
                return e.getMessage();
            }
        }

        private static String getFromFile(String uni, String group) {
            String url = "data/" + uni + "/" + group + "/" + WeekDay.getWeekNumToPackage();
            try {
                Map<String, List<Path>> fileNameMap = Files.walk(Path.of(url)).filter(Files::isRegularFile).collect(Collectors.groupingBy(n -> n.getFileName()
                        .toString().replace(".txt", "")));
                String result = Arrays.stream(WeekDay.values()).map(n -> {
                    try {
                        return n + "\n" + Files.readString(fileNameMap.get(n + "").get(0)) + "\n\n";
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "";
                    } catch (NullPointerException e) {
                        return "";
                    }
                }).filter(n -> (!n.equals("") && n != null)).reduce((s, s1) -> s + s1).get();
                return result;
            } catch (IOException e) {
                throw new IllegalArgumentException("Incorrect University or group");
            }
        }

        private static String getWeek(String uni, String group) throws IllegalArgumentException {
            if (uni == null || uni.equalsIgnoreCase("null") || uni.equals("")) {
                throw new IllegalArgumentException("Enter University, please");
            } else if (group == null || group.equalsIgnoreCase("null") || group.equals("")) {
                throw new IllegalArgumentException("Enter group, please");
            } else {
                return getFromFile(uni, group);
            }
        }

        private static String oneDay(String fullWeek, WeekDay dayStart) throws IllegalArgumentException {
            try {
                if (WeekDay.getDay(WeekDay.values().length) == dayStart) {
                    return fullWeek.substring(fullWeek.indexOf(dayStart.name()));
                } else {
                    return fullWeek.substring(fullWeek.indexOf(dayStart.name()), fullWeek.indexOf(WeekDay.getDay(dayStart.ordinal() + 2).name()));
                }
            } catch (StringIndexOutOfBoundsException e) {
                throw new StringIndexOutOfBoundsException("No Classes found");
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    public static class KeyBord {
        private static ReplyKeyboardMarkup replyKeyboardMarkup;

        static {
            replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
        }

        public static void waitEnter(BotContext context) {
            KeyboardRow keyRowOne = new KeyboardRow();
            KeyboardRow keyRowTwo = new KeyboardRow();
            keyRowOne.add("Today");
            keyRowOne.add("Tomorrow");
            keyRowOne.add("Week");
            keyRowTwo.add("Group");
            keyRowTwo.add("University");
            keyRowTwo.add("Language");
            replyKeyboardMarkup.setKeyboard(List.of(keyRowOne, keyRowTwo));
            sendMessageButtons(context, "what to do next");
        }

        public static void sendMessageButtons(BotContext context, String text) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(context.getUser().getChatId())
                    .setText(text).setReplyMarkup(replyKeyboardMarkup);
            try {
                context.getBot().execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        public static void setLanguage(BotContext context) {
            KeyboardRow keyboardButtons = new KeyboardRow();
            listLanguage().forEach(n -> keyboardButtons.add(n));
            replyKeyboardMarkup.setKeyboard(List.of(keyboardButtons));
            sendMessageButtons(context, "language");
        }

        public static void setUni(BotContext context) {
            uniList();
            sendMessageButtons(context, "Enter your Uni, please:");
        }

        public static boolean checkUniName(String groupName) {
            try {
                groupName += "_U";
                String finalGroupName = groupName;
                return Files.walk(Path.of("data/")).filter(Files::isDirectory).map(Path::getFileName).map(Path::toString)
                        .filter(n -> !n.equals("data")).map(n -> n.equalsIgnoreCase(finalGroupName))
                        .reduce((aBoolean, aBoolean2) -> aBoolean || aBoolean2).get();
            } catch (IOException e) {
                return false;
            }
        }

        private static List<String> listLanguage() {
            try {
                return Files.walk(Path.of("config/language_pack")).filter(Files::isRegularFile).map(Path::getFileName).map(Path::toString)
                        .map(n -> n.substring(0, n.lastIndexOf("."))).collect(Collectors.toList());
            } catch (IOException e) {
                return List.of("eng");
            }
        }

        private static void uniList() {
            List<String> uniName = new ArrayList<>();
            try {
                uniName = Files.walk(Path.of("data")).filter(Files::isDirectory).filter(n -> n.getFileName().toString()
                        .contains("_U")).map(Path::getFileName).map(Path::toString)
                        .map(n -> n.replace("_U", "")).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
            list(uniName);
        }

        public static void setGroup(BotContext context, String message) {
            List<String> name = new ArrayList<>();
            try {
                name = Files.walk(Path.of("data/" + context.getUser().getUni()))
                        .filter(n -> !n.getFileName().toString().contains("week")).filter(Files::isDirectory)
                        .filter(n -> !n.getFileName().toString().contains("_U")).map(Path::getFileName).map(Path::toString)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
            name.forEach(System.out::println);
            list(name);
            sendMessageButtons(context, message);
        }

        public static boolean checkGroupName(String groupName, BotContext context) {
            try {
                List<String> name = Files.walk(Path.of("data/" + context.getUser().getUni() + "/")).filter(Files::isDirectory).map(Path::getFileName).map(Path::toString)
                        .filter(n -> !n.equals("data")).filter(n -> !n.contains("week")).collect(Collectors.toList());
                System.out.println("files");
                name.forEach(System.out::println);
                return name.contains(groupName);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        private static void list(List<String> name) {
            List<KeyboardRow> list = new ArrayList<>();
            list.add(new KeyboardRow());
            int counter = 0;
            while (name.size() > 0) {
                if (list.get(counter).size() == 5) {
                    list.add(new KeyboardRow());
                    counter++;
                }
                list.get(counter).add(name.remove(0));
            }
            replyKeyboardMarkup.setKeyboard(list);
        }
    }

    public static void sendMessage(BotContext context, String text) {
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(context.getUser().getChatId())
                .setText(text);
        try {
            context.getBot().execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
