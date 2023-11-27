package com.j9nos;

import com.j9nos.sufficient_pattern_recognizer.SufficientPatternRecognizer;

import java.io.IOException;

public final class Main {

    private static final String SAMPLES_DIRECTORY = "src/main/resources/letters/samples";

    private Main() {
    }

    public static void main(final String[] args) throws IOException {

        final SufficientPatternRecognizer patternRecognizer =
                new SufficientPatternRecognizer(25, SAMPLES_DIRECTORY);

        patternRecognizer.recognize("src/main/resources/letters/handdrawn/unknown.jpg");
        patternRecognizer.recognize("src/main/resources/letters/handdrawn/test.jpg");

    }


}