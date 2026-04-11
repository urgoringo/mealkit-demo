package com.urgoringo.mealkit.infra;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class MealkitRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        // Jackson needs reflective access to deserialize RecipeIngredientDto from JSONB.
        // The class is package-private so referenced by name.
        ClassLoader loader = classLoader != null ? classLoader : getClass().getClassLoader();
        try {
            Class<?> dto = Class.forName(
                "com.urgoringo.mealkit.recipecatalog.domain.RecipeIngredientDto",
                false,
                loader
            );
            hints.reflection()
                .registerType(
                    dto,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.DECLARED_FIELDS
                );
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("RecipeIngredientDto not found on classpath", e);
        }
    }
}
