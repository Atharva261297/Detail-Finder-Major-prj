package com.destro.linkcalculator.writer;

import java.io.PrintWriter;
import java.io.Writer;

public class CopyPrintWriter extends PrintWriter {

    private final StringBuilder copy = new StringBuilder();

    public CopyPrintWriter(final Writer writer) {
        super(writer);
    }

    @Override
    public void write(final int c) {
        copy.append((char) c); // It is actually a char, not an int.
        super.write(c);
    }

    @Override
    public void write(final char[] chars, final int offset, final int length) {
        copy.append(chars, offset, length);
        super.write(chars, offset, length);
    }

    @Override
    public void write(final String string, final int offset, final int length) {
        copy.append(string, offset, length);
        super.write(string, offset, length);
    }

    public String getCopy() {
        return copy.toString();
    }

}