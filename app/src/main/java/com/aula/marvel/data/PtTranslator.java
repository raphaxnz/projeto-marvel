package com.aula.marvel.data;

import android.os.SystemClock;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tradução inglês → português no próprio aparelho (ML Kit). A Comic Vine só tem conteúdo em inglês.
 * O modelo (~30 MB) é baixado uma vez; enquanto não estiver pronto, o texto original é mantido.
 * Chamar translate() fora da main thread.
 */
public final class PtTranslator {
    private static final String TAG = "PtTranslator";
    private static final long MODEL_WAIT_SECONDS = 20;
    private static final long TRANSLATE_WAIT_SECONDS = 10;
    private static final long RETRY_MS = 30_000;

    private static volatile PtTranslator instance;

    private final Translator translator;
    private final DownloadConditions conditions = new DownloadConditions.Builder().build();
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private volatile Task<Void> download;
    private volatile long retryAfter;

    private PtTranslator() {
        translator = Translation.getClient(new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.PORTUGUESE)
                .build());
        download = translator.downloadModelIfNeeded(conditions);
    }

    /** Também inicia o download do modelo, para ele estar pronto quando as telas precisarem. */
    public static PtTranslator get() {
        if (instance == null) {
            synchronized (PtTranslator.class) {
                if (instance == null) instance = new PtTranslator();
            }
        }
        return instance;
    }

    /** true quando o modelo já está no aparelho (antes disso, translate() devolve o original). */
    public boolean isReady() {
        Task<Void> task = download;
        return task.isComplete() && task.isSuccessful();
    }

    /** Traduz mantendo as quebras de linha. Em falha, devolve o texto original. */
    public String translate(String text) {
        if (text == null || text.trim().isEmpty()) return text;
        String cached = cache.get(text);
        if (cached != null) return cached;

        String[] lines = text.split("\n", -1);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) out.append('\n');
            if (lines[i].trim().isEmpty()) { out.append(lines[i]); continue; }
            String translated = translateLine(lines[i]);
            if (translated == null) return text;
            out.append(translated);
        }
        String result = out.toString();
        cache.put(text, result);
        return result;
    }

    private String translateLine(String line) {
        try {
            if (!modelReady()) return null;
            return Tasks.await(translator.translate(line), TRANSLATE_WAIT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.w(TAG, "falha ao traduzir", e);
            return null;
        }
    }

    private boolean modelReady() throws Exception {
        long now = SystemClock.elapsedRealtime();
        Task<Void> task = download;
        if (task.isComplete() && !task.isSuccessful() && now >= retryAfter) {
            Log.w(TAG, "download do modelo falhou; tentando de novo", task.getException());
            retryAfter = now + RETRY_MS;
            task = download = translator.downloadModelIfNeeded(conditions);
        }
        if (!task.isComplete()) {
            if (now < retryAfter) return false;
            try {
                Tasks.await(task, MODEL_WAIT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                // Não trava as próximas traduções esperando o mesmo download.
                retryAfter = SystemClock.elapsedRealtime() + RETRY_MS;
                Log.w(TAG, "modelo de tradução ainda baixando");
                return false;
            }
        }
        return task.isSuccessful();
    }
}
