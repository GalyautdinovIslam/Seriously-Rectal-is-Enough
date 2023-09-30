package ru.itis.oncall.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DutyDateParser {

    private DutyDateParser() {
    }

    public static long parse(String date) throws ParseException {
        return new SimpleDateFormat("dd/MM/yyyy").parse(date).getTime() / 1000;
    }
}
