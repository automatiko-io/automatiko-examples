package io.automatiko.examples.support.incidents.service;

import java.util.List;
import java.util.Random;

import io.automatiko.engine.api.Functions;
import io.automatiko.examples.support.incidents.Comment;

public class SupportCaseFunctions implements Functions {

    public static final String RESOLVED_STATUS = "resolved";
    public static final String CLOSED_STATUS = "closed";

    public static boolean isResolved(String status) {
        if (RESOLVED_STATUS.equalsIgnoreCase(status) || CLOSED_STATUS.equalsIgnoreCase(status)) {
            return true;
        }

        return false;
    }

    public static boolean hasMention(List<Comment> comments) {

        if (!comments.isEmpty()) {
            Comment lastComment = comments.get(comments.size() - 1);
            if (lastComment.getText().contains("@reporter") || lastComment.getText().contains("@support")) {
                return true;
            }
        }
        return false;
    }

    public static String createCaseKey() {
        Random r = new Random();
        int numbers = 100000 + (int) (r.nextFloat() * 899900);
        return "SUPPORT-" + String.valueOf(numbers);
    }
}
