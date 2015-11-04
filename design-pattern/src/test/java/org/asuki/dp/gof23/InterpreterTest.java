package org.asuki.dp.gof23;

import static org.asuki.dp.gof23.Interpreter.Operator.toOperator;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Stack;

import org.asuki.dp.gof23.Interpreter.Expression;
import org.asuki.dp.gof23.Interpreter.MinusExpression;
import org.asuki.dp.gof23.Interpreter.NumberExpression;
import org.asuki.dp.gof23.Interpreter.Operator;
import org.asuki.dp.gof23.Interpreter.PlusExpression;
import org.testng.annotations.Test;

public class InterpreterTest {

    @Test
    public void test() {
        String target = "3 2 - 1 +";
        Stack<Expression> stack = new Stack<>();

        String[] splits = target.split(" ");
        for (String s : splits) {

            Operator operator = toOperator(s);

            if (operator == null) {
                stack.push(new NumberExpression(s));
                continue;
            }

            Expression rightExpression = stack.pop();
            Expression leftExpression = stack.pop();

            Expression operatorExpression = getOperatorExpression(operator,
                    leftExpression, rightExpression);

            int result = operatorExpression.interpret();
            stack.push(new NumberExpression(result));
        }

        assertThat(stack.pop().interpret(), is((3 - 2) + 1));
    }

    private static Expression getOperatorExpression(Operator operator,
            Expression left, Expression right) {

        switch (operator) {
        case MINUS:
            return new MinusExpression(left, right);
        case PLUS:
            return new PlusExpression(left, right);
        default:
            break;
        }

        return null;
    }

}
