package com.j9nos.sufficient_pattern_recognizer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SufficientPatternRecognizer {
    private static final int BLACK_PIXEL_LIMIT = 128;
    private final int boundary;
    private final HashMap<String, ArrayList<Integer>> sampleData;

    public SufficientPatternRecognizer(final int boundary, final String samplesDirectory) {
        this.boundary = boundary;
        this.sampleData = bulkProcess(samplesDirectory);
    }

    private boolean black(final Color color) {
        return Math.sqrt(Math.pow(Color.BLACK.getRed() - color.getRed(), 2)
                + Math.pow(Color.BLACK.getGreen() - color.getGreen(), 2)
                + Math.pow(Color.BLACK.getBlue() - color.getBlue(), 2))
                < BLACK_PIXEL_LIMIT;
    }


    private BufferedImage scale(final BufferedImage image) {
        final BufferedImage scaledImage = new BufferedImage(boundary, boundary, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = scaledImage.createGraphics();
        graphics.drawImage(image.getScaledInstance(boundary, boundary, Image.SCALE_SMOOTH), 0, 0, null);
        graphics.dispose();
        return scaledImage;
    }


    private BufferedImage load(final String url) throws IOException {
        return scale(ImageIO.read(new File(url)));
    }

    private ArrayList<Integer> loneProcess(final String url) throws IOException {
        final BufferedImage image = load(url);
        final int w = image.getWidth();
        final int h = image.getHeight();
        final ArrayList<Integer> pixels = new ArrayList<>();
        int pixelCounter = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (black(new Color(image.getRGB(x, y)))) {
                    pixels.add(pixelCounter);
                }
                pixelCounter++;
            }
        }
        return pixels;
    }

    private HashMap<String, ArrayList<Integer>> bulkProcess(final String directory) {
        final HashMap<String, ArrayList<Integer>> processed = new HashMap<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory), "*.jpg")) {
            for (final Path path : directoryStream) {
                final String fileName = String.valueOf(path.getFileName()).split("\\.")[0];
                processed.put(fileName, loneProcess(String.valueOf(path.toFile())));
            }
        } catch (final IOException ignored) {
        }
        return processed;
    }


    private int intersection(final ArrayList<Integer> list1, final ArrayList<Integer> list2) {
        int counter = 0;
        for (final int e : list1) {
            if (list2.contains(e)) {
                counter++;
            }
        }
        return counter;
    }

    public void recognize(final String url) throws IOException {
        final ArrayList<Integer> unknown = loneProcess(url);
        final TreeMap<Integer, ArrayList<String>> tree = new TreeMap<>(Collections.reverseOrder());
        for (final Map.Entry<String, ArrayList<Integer>> entry : sampleData.entrySet()) {
            final int intersection = intersection(unknown, entry.getValue());
            tree.computeIfAbsent(intersection, k -> new ArrayList<>());
            tree.get(intersection).add(entry.getKey());
        }
        System.out.println(tree);
    }


}
