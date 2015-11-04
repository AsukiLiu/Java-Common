package org.asuki.dp.gof23;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Interpreter {

    interface Expression {
        int interpret();
    }

    @AllArgsConstructor
    static class MinusExpression implements Expression {

        private final Expression leftExpression;
        private final Expression rightExpression;

        @Override
        public int interpret() {
            return leftExpression.interpret() - rightExpression.interpret();
        }

    }

    @AllArgsConstructor
    static class PlusExpression implements Expression {

        private final Expression leftExpression;
        private final Expression rightExpression;

        @Override
        public int interpret() {
            return leftExpression.interpret() + rightExpression.interpret();
        }

    }

    @AllArgsConstructor
    static class NumberExpression implements Expression {

        private final int number;

        public NumberExpression(String s) {
            this.number = Integer.parseInt(s);
        }

        @Override
        public int interpret() {
            return number;
        }

    }

    enum Operator {
        MINUS("-"), PLUS("+");

        @Getter
        private String meaning;

        Operator(String meaning) {
            this.meaning = meaning;
        }

        public static boolean contains(String s) {
            for (Operator operator : Operator.values()) {
                if (operator.getMeaning().equals(s)) {
                    return true;
                }
            }

            return false;
        }

        public static Operator toOperator(String s) {
            for (Operator operator : Operator.values()) {
                if (operator.getMeaning().equals(s)) {
                    return operator;
                }
            }

            return null;
        }
    }

}
