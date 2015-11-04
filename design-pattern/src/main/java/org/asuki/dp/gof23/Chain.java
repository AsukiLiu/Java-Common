package org.asuki.dp.gof23;

import static com.google.common.base.Optional.absent;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

public class Chain {

    static abstract class BaseValidator<T> {

        private Optional<BaseValidator<T>> nextValidator = absent();

        public final void validate(T toValidate,
                ValidationResult validationResult) {

            validateSepcific(toValidate, validationResult);

            if (nextValidator.isPresent()) {
                nextValidator.get().validate(toValidate, validationResult);
            }
        }

        protected BaseValidator<T> and(BaseValidator<T> nextValidator) {
            this.nextValidator = Optional.of(nextValidator);
            return this;
        }

        protected abstract void validateSepcific(T toValidate,
                ValidationResult validationResult);
    }

    static class ValidatorA extends BaseValidator<String> {
        @Override
        protected void validateSepcific(String toValidate,
                ValidationResult validationResult) {

            if (toValidate.length() > 5) {
                validationResult.addViolation("too long");
            }
        }
    }

    static class ValidatorB extends BaseValidator<String> {
        @Override
        protected void validateSepcific(String toValidate,
                ValidationResult validationResult) {

            if (toValidate.contains("@")) {
                validationResult.addViolation("invalid character");
            }
        }
    }

    static class ValidationResult {
        private List<String> violations = new ArrayList<>();

        public void addViolation(String violation) {
            violations.add(violation);
        }

        public boolean isValid() {
            return violations.isEmpty();
        }

        @Override
        public String toString() {
            return violations.toString();
        }
    }

    static class ValidationService {

        private static BaseValidator<String> validator;

        static {
            validator = getValidationChain();
        }

        public static ValidationResult validate(String toValidate) {
            ValidationResult result = new ValidationResult();
            validator.validate(toValidate, result);
            return result;
        }

        private static BaseValidator<String> getValidationChain() {
            return new ValidatorA().and(new ValidatorB());
        }
    }

}
