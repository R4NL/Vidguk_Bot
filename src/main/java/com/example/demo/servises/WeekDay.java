package com.example.demo.servises;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public enum WeekDay implements Comparable<WeekDay>{
    Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday ;

    public static WeekDay today() {
        return getDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }

    public static WeekDay tomorrow() {
        return getDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 1);
    }

    public static WeekDay getDay(int day) {
        switch (day) {
            case 1:
                return WeekDay.Sunday;
            case 2:
                return WeekDay.Monday;
            case 3:
                return WeekDay.Tuesday;
            case 4:
                return WeekDay.Wednesday;
            case 5:
                return WeekDay.Thursday;
            case 6:
                return WeekDay.Friday;
            case 7:
                return WeekDay.Saturday;
        }
        return Sunday;
    }



    public static String getWeekNumToPackage() {
        int weekNow = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        String input = "";
        try {
            input = Files.readString(Path.of("config/startSemester.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String format = "yyyy/MM/dd";

        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = df.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        if (((weekNow - week) % 2) == 0) return "weekOne";
        else return "weekTwo";
    }



}
