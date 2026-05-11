package com.snackoverflow.fridgemate.storage;

import java.io.IOException;

public interface Storage {
    AppData load() throws IOException;

    void save(AppData data) throws IOException;
}
