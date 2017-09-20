package newtech.audiolibrary.adapters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import newtech.audiolibrary.bean.Chapter;

/**
 * Created by MartireAn on 20/09/2017.
 */

public class ChapterTypeAdapterFactory {
    private Set<String> serializedChapters = new HashSet<>();

    public JsonSerializer<Chapter> getTypeAdapter(Chapter chapter, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject el = new JsonObject();
        el.addProperty("chapterTitle", chapter.getChapterTitle());
        el.addProperty("url", chapter.getUrl());
        el.addProperty("currentDuration", chapter.getCurrentDuration());
        el.addProperty("totalDuration", chapter.getTotalDuration());

        //mark as serialized
        serializedChapters.add(chapter.getUrl());

        if (chapter.getBook() != null) {
            JsonObject book = new JsonObject();
            book.addProperty("appDir", chapter.getBook().getAppDir());
            book.addProperty("providerName", chapter.getBook().getProviderName());
            book.addProperty("bookTitle", chapter.getBook().getBookTitle());
            book.addProperty("imageResId", chapter.getBook().getImageResId());

            if (chapter.getBook().getChapters() != null) {
                JsonArray innerChapters = new JsonArray();

                for (Chapter innerChapter : chapter.getBook().getChapters()) {
                    //check if already serialized
                    if (!serializedChapters.contains(innerChapter.getUrl())) {
                        JsonObject chapterJsonObj = new JsonObject();
                        chapterJsonObj.addProperty("chapterTitle", innerChapter.getChapterTitle());
                        chapterJsonObj.addProperty("url", innerChapter.getUrl());
                        chapterJsonObj.addProperty("currentDuration", innerChapter.getCurrentDuration());
                        chapterJsonObj.addProperty("totalDuration", innerChapter.getTotalDuration());

                        innerChapters.add(chapterJsonObj);
                        JsonElement chapterSerialized = context.serialize(chapterJsonObj);

                        serializedChapters.add(innerChapter.getUrl());
                    }
                }

                book.add("chapters", innerChapters);
                el.add("book", book);
            }
        }

        //TODO
        return null;
    }
}
