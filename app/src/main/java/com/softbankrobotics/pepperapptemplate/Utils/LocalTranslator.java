package com.softbankrobotics.pepperapptemplate.Utils;

import androidx.annotation.NonNull;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class LocalTranslator {

    private Translator englishLatvianTranslator;

    public LocalTranslator() {
        Initialize();
    }

    public Translator getObserver() {
        return englishLatvianTranslator;
    }

    private void Initialize()
    {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.LATVIAN)
                        .build();
        englishLatvianTranslator =
                Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        englishLatvianTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
    }

    public void translateTextAndSpeak(String word, QiContext context) {
        // Testing translate functions
        // If a method is overridden and you're using actions, make sure they're async!
        englishLatvianTranslator.translate(word)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                Phrase phrase = new Phrase(translatedText);
                                Future<Say> sayFuture = SayBuilder.with(context)
                                        .withPhrase(phrase)
                                        .buildAsync();
                                sayFuture.thenConsume(speakFuture -> {
                                    if (speakFuture.isSuccess()) {
                                        Say say = speakFuture.get();
                                        say.run();
                                    } else if (speakFuture.isCancelled()) {
                                        // Handle cancelled state.
                                    } else {
                                        // Handle error state.
                                    }
                                });

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
    }

    public String translateText(String word) {
        final List<String> returnedText = new ArrayList<>();

        englishLatvianTranslator.translate(word)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                returnedText.add(translatedText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
        if (returnedText.size() > 0)
            return returnedText.get(0);
        else
            return "";
    }

}
