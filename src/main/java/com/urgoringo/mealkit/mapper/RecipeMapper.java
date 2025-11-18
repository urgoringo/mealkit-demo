package com.urgoringo.mealkit.mapper;

import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.domain.Recipe;
import com.urgoringo.mealkit.persistence.RecipeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * MapStruct mapper for converting between Recipe domain model and RecipeEntity.
 */
@NullMarked
@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(source = "name", target = "title")
    Recipe toDomain(RecipeEntity entity);

    List<Recipe> toDomain(List<RecipeEntity> entities);

    @Mapping(source = "title", target = "name")
    RecipeEntity toEntity(Recipe recipe);

    default Id<Recipe> mapId(@Nullable Long id) {
        return id == null ? Id.unassigned() : Id.of(id);
    }

    default Long mapId(Id<Recipe> id) {
        return id.value();
    }
}
