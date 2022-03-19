package net.darmo_creations.mccode.interpreter.types;

import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.Objects;

/**
 * A range is an iterable object that generates integers between two bounds.
 */
@Immutable
public class Range implements Iterable<Long>, Cloneable {
  private final long start; // Included
  private final long end; // Excluded
  private final long step;

  /**
   * Create a range generator.
   *
   * @param start Range’s start value (included).
   * @param end   Range’s end value (excluded).
   * @param step  Range’s step; cannot be 0.
   */
  public Range(final long start, final long end, final long step) {
    this.start = start;
    this.end = end;
    if (step == 0) {
      throw new IllegalArgumentException("range step cannot be 0");
    }
    this.step = step;
  }

  /**
   * Return range’s start value.
   */
  public long getStart() {
    return this.start;
  }

  /**
   * Return range’s end value.
   */
  public long getEnd() {
    return this.end;
  }

  /**
   * Return range’s step.
   */
  public long getStep() {
    return this.step;
  }

  @Override
  public Iterator<Long> iterator() {
    return new Iterator<>() {
      private long i = Range.this.start;

      @Override
      public boolean hasNext() {
        return Range.this.step > 0 && this.i < Range.this.end - Range.this.step + 1
            || Range.this.step < 0 && this.i > Range.this.end + Range.this.step + 1;
      }

      @Override
      public Long next() {
        long i = this.i;
        this.i += Range.this.step;
        return i;
      }
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    Range integers = (Range) o;
    return this.start == integers.start && this.end == integers.end && this.step == integers.step;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.start, this.end, this.step);
  }

  @Override
  public Range clone() {
    try {
      return (Range) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public String toString() {
    return String.format("Range{start=%d, end=%d, step=%d}", this.start, this.end, this.step);
  }
}
