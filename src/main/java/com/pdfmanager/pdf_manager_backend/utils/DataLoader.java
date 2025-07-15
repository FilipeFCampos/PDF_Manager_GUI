package com.pdfmanager.pdf_manager_backend.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdfmanager.pdf_manager_backend.files.Book;
import com.pdfmanager.pdf_manager_backend.files.ClassNote;
import com.pdfmanager.pdf_manager_backend.files.Slide;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class DataLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Book> loadBooks() {
        return loadJson("/books.json", new TypeReference<List<Book>>() {});
    }

    public static List<ClassNote> loadClassNotes() {
        return loadJson("/classnotes.json", new TypeReference<List<ClassNote>>() {});
    }

    public static List<Slide> loadSlides() {
        return loadJson("/slides.json", new TypeReference<List<Slide>>() {});
    }

    private static <T> List<T> loadJson(String path, TypeReference<List<T>> typeRef) {
        try (InputStream is = DataLoader.class.getResourceAsStream(path)) {
            if (is == null) return Collections.emptyList();
            return mapper.readValue(is, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
