package com.snakegame.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * FileLineIterator provides a robust line-by-line file reader adhering to the
 * Java {@link Iterator} interface contract.
 *
 * It streams data lazily rather than loading entire files into memory,
 * ensuring low memory overhead when parsing saved game state files.
 */
public class FileLineIterator implements Iterator<String> {

    private BufferedReader bufferedReader;
    private String currentLine;

    /**
     * Constructs an iterator from an existing BufferedReader.
     *
     * @param reader The BufferedReader instance
     * @throws IllegalArgumentException if reader is null
     */
    public FileLineIterator(BufferedReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("BufferedReader cannot be null");
        }
        this.bufferedReader = reader;
        try {
            this.currentLine = reader.readLine();
            if (this.currentLine == null) {
                closeReader();
            }
        } catch (IOException e) {
            this.currentLine = null;
            closeReader();
        }
    }

    /**
     * Convenience constructor that opens a FileReader from the specified path.
     *
     * @param filePath Path to the target text file
     * @throws IllegalArgumentException if filePath is null or the file cannot be opened
     */
    public FileLineIterator(String filePath) {
        this(fileToReader(filePath));
    }

    /**
     * Helper factory method creating a BufferedReader from a file path.
     *
     * @param filePath Path to file
     * @return Initialized BufferedReader
     * @throws IllegalArgumentException if path is invalid or file is not found
     */
    public static BufferedReader fileToReader(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        try {
            return new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found: " + filePath, e);
        }
    }

    @Override
    public boolean hasNext() {
        if (currentLine == null && bufferedReader != null) {
            closeReader();
        }
        return currentLine != null;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            closeReader();
            throw new NoSuchElementException("No more lines to read");
        }

        String lineToReturn = currentLine;
        try {
            currentLine = bufferedReader.readLine();
            if (currentLine == null) {
                closeReader();
            }
        } catch (IOException e) {
            currentLine = null;
            closeReader();
        }

        return lineToReturn;
    }

    private void closeReader() {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException ignored) {
            }
            bufferedReader = null;
        }
    }
}
