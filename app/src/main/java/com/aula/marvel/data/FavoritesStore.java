package com.aula.marvel.data;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

/**
 * Personagens favoritados, salvos no aparelho. Trocar o tema recria a tela; sem isso o
 * coração voltava a ficar desmarcado.
 */
public final class FavoritesStore {
    private static final String PREFS = "marvel_preferences";
    private static final String KEY = "favorite_heroes";

    private final SharedPreferences prefs;

    public FavoritesStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isFavorite(long heroId) {
        return prefs.getStringSet(KEY, new HashSet<>()).contains(String.valueOf(heroId));
    }

    /** Inverte o estado e devolve se ficou favoritado. */
    public boolean toggle(long heroId) {
        // O Set devolvido pelo SharedPreferences não pode ser alterado: trabalha numa cópia.
        Set<String> ids = new HashSet<>(prefs.getStringSet(KEY, new HashSet<>()));
        String id = String.valueOf(heroId);
        boolean nowFavorite = !ids.remove(id);
        if (nowFavorite) ids.add(id);
        prefs.edit().putStringSet(KEY, ids).apply();
        return nowFavorite;
    }
}
