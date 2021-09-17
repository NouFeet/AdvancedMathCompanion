/*
 * Copyright 2021 Nikita Kuprins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nikitakuprins.mathCompanion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Calculations {

    private static final List<Character> mathSigns = Arrays.asList('+', '-', '*', 'x', '/', '÷');
    private static final Pattern validFormat = Pattern.compile("^-?\\d+( [+\\-*/] -?\\d+)+$");
    private static boolean isRecursive = false;

    private Calculations() {}

    public static int getExpressionTypeId(String str) {
        if (countOfDigits(str) >= 30) {
            return 4; // */+- long
        }

        Set<Character> signs = getExpressionSigns(str);
        if (!signs.contains('/') && !signs.contains('*')) {
            return 1; // +-
        }
        if (!signs.contains('+') && !signs.contains('-')) {
            return 3; // */
        }

        return 2; // */+-
    }

    public static long countOfDigits(String str) {
        return str.chars()
                .filter(Character::isDigit)
                .count();
    }

    public static Set<Character> getExpressionSigns(String str) {
        String[] array = str.split(" ");
        return Arrays.stream(array)
                .filter(s -> s.length() == 1)
                .map(s -> s.charAt(0))
                .filter(mathSigns::contains)
                .collect(Collectors.toSet());
    }

    public static boolean isValidFormat(String str) {
        Matcher matcher = validFormat.matcher(str);
        return matcher.matches();
    }

    public static BigDecimal calculate(BigDecimal num1, BigDecimal num2, String operation) {
        switch (operation) {
            case "+":
                return num1.add(num2);
            case "-":
                return num1.subtract(num2);
            case "x": case "*":
                return num1.multiply(num2);
            case "÷": case "/":
                return num1.divide(num2, 4, RoundingMode.HALF_UP);
            default:
                return BigDecimal.ZERO;
        }
    }

    // Splits expression by math order
    // Example: 2 * 3 | - | 1 / 3 |
    // 1) 2 * 3
    // 2) 1 / 3
    // 3) -
    private static String[] splitByOperationsOrder(String expression) {
        if (isRecursive) {
            return expression.split(" ");
        } else {
            return expression.split("(?<=[^/*])\\s(?=[^/*])");
        }
    }

    public static BigDecimal calculate(String expression) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal temp;
        String operation = "+";
        String[] array = splitByOperationsOrder(expression);


        for (String s : array) {
            if (s.length() == 1 && mathSigns.contains(s.charAt(0))) {
                operation = s;
            } else {
                if (s.chars().anyMatch(item -> item == ' ')) {
                    isRecursive = true;
                    temp = calculate(s);
                } else {
                    temp = new BigDecimal(s);
                }
                result = calculate(result, temp, operation);
            }
        }

        isRecursive = false;
        return result;
    }
}
