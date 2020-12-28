package io.automatiko.examples.vacation.requests;

import io.automatiko.engine.api.Functions;

public class VacationFunctions implements Functions {

    public static boolean isNotEligible(Request request, Vacation vacation) {
        if (vacation.eligible.intValue() <= request.length + vacation.used) {
            return true;
        }

        return false;
    }

    public static String vacationUsed(Vacation vacation) {
        return "Used days " + vacation.used;
    }
}
