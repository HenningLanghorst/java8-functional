package de.henninglanghorst.functional.util;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represent object which can contain either a value of type {@link A} or a value of type {@link B}.
 *
 * @author Henning Langhorst
 */
public abstract class Either<A, B> {


    public static <A, B> Either<A, B> left(A value) {
        return new Left<>(value);
    }

    public static <A, B> Either<A, B> right(B value) {
        return new Right<>(value);
    }

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract Optional<A> left();

    public abstract Optional<B> right();

    public abstract void handleResult(Consumer<A> leftConsumer, Consumer<B> rightConsumer);

    private static class Left<A, B> extends Either<A, B> {

        private final A value;

        public Left(final A value) {
            this.value = value;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public Optional<A> left() {
            return Optional.of(value);
        }

        @Override
        public Optional<B> right() {
            return Optional.empty();
        }

        @Override
        public void handleResult(final Consumer<A> leftConsumer, final Consumer<B> rightConsumer) {
            leftConsumer.accept(value);
        }

        @Override
        public String toString() {
            return "Left(" + value + ")";
        }
    }

    private static class Right<A, B> extends Either<A, B> {

        private final B value;

        public Right(final B value) {
            this.value = value;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public Optional<A> left() {
            return Optional.empty();
        }

        @Override
        public Optional<B> right() {
            return Optional.of(value);
        }

        @Override
        public void handleResult(final Consumer<A> leftConsumer, final Consumer<B> rightConsumer) {
            rightConsumer.accept(value);
        }

        @Override
        public String toString() {
            return "Right(" + value + ")";
        }

    }

}
