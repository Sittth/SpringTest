package spring.ru.springtest.client;

import spring.ru.springtest.models.enums.FailureType;

import java.util.function.Predicate;

public class TransientFailurePredicate implements Predicate<Throwable> {

    @Override
    public boolean test(Throwable throwable) {
        return FailureClassifier.classify(throwable) == FailureType.TRANSIENT;
    }
}