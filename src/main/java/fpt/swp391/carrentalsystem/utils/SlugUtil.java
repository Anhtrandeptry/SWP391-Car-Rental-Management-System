package fpt.swp391.carrentalsystem.utils;

import java.text.Normalizer;

public class SlugUtil {

    public static String slugify(String input) {
        if (input == null) return "blog";
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String slug = normalized
                .replaceAll("[^a-zA-Z0-9\\-]", "")
                .replaceAll("\\-+", "-")
                .toLowerCase();
        return slug.isBlank() ? "blog" : slug;
    }
}