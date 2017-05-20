package de.henninglanghorst.functional.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represent object which can contain either a value of type {@link A} or a value of type {@link B}.
 *
 * @author Henning Langhorst
 */
public interface Either<A, B> {


    static <A, B> Either<A, B> left(A value) {
        return new Left<>(value);
    }

    static <A, B> Either<A, B> right(B value) {
        return new Right<>(value);
    }

    boolean isLeft();

    boolean isRight();

    Optional<A> left();

    Optional<B> right();

    void handle(Consumer<A> leftConsumer, Consumer<B> rightConsumer);
}

class Left<A, B> implements Either<A, B> {

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
    public void handle(final Consumer<A> leftConsumer, final Consumer<B> rightConsumer) {
        leftConsumer.accept(value);
    }

    @Override
    public String toString() {
        return "Left(" + value + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Left<?, ?> left = (Left<?, ?>) o;
        return Objects.equals(value, left.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

class Right<A, B> implements Either<A, B> {

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
    public void handle(final Consumer<A> leftConsumer, final Consumer<B> rightConsumer) {
        rightConsumer.accept(value);
    }

    @Override
    public String toString() {
        return "Right(" + value + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Right<?, ?> right = (Right<?, ?>) o;
        return Objects.equals(value, right.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
