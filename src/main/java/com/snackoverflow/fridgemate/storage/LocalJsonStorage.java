package com.snackoverflow.fridgemate.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class LocalJsonStorage implements Storage {
    private final Path file;
    private final Gson gson;

    public LocalJsonStorage(Path file) {
        this.file = file;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    @Override
    public AppData load() throws IOException {
        if (!Files.exists(file)) {
            return new AppData();
        }

        try (var reader = Files.newBufferedReader(file)) {
            AppData data = gson.fromJson(reader, AppData.class);
            return data == null ? new AppData() : data;
        } catch (JsonParseException ex) {
            throw new IOException("Saved data file is corrupted", ex);
        }
    }

    @Override
    public void save(AppData data) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (var writer = Files.newBufferedWriter(file)) {
            gson.toJson(data, writer);
        }
    }

    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString());
        }
    }
}
