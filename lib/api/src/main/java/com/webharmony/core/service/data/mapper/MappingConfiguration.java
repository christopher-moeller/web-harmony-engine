package com.webharmony.core.service.data.mapper;

import com.webharmony.core.api.rest.model.utils.BaseDto;
import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import lombok.Getter;

public class MappingConfiguration<D extends BaseDto, E extends AbstractBaseEntity> {

    @Getter
    private NewEntityInstanceCreator<E> newEntityInstanceCreator;
    @Getter
    private NewDtoInstanceCreator<D> newDtoInstanceCreator;
    private ToEntityMapper<D, E> toEntityMapper;
    private ToEntityMapper<D, E> extendedToEntityMapper;
    private ToDtoMapper<D, E> toDtoMapper;
    private ToDtoMapper<D, E> extendedToDtoMapper;
    @Getter
    private final Class<E> entityClass;
    @Getter
    private final Class<D> dtoClass;

    @Getter
    private boolean mapNestedFields = true;

    private MappingConfiguration(Class<E> entityClass, Class<D> dtoClass) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public ToDtoMapper<D, E> getEffectiveToDtoMapper() {
        return (entity, dto, context) -> {
          D resultDto = dto;
          if(this.toDtoMapper != null)
              resultDto = this.toDtoMapper.toDto(entity, resultDto, context);

          if(this.extendedToDtoMapper != null)
              resultDto = this.extendedToDtoMapper.toDto(entity, resultDto, context);

          return resultDto;
        };
    }

    public ToEntityMapper<D, E> getEffectiveToEntityMapper() {
        return (dto, entity, context) -> {
            E resultEntity = entity;
            if(this.toEntityMapper != null)
                resultEntity = this.toEntityMapper.toEntity(dto, resultEntity, context);

            if(this.extendedToEntityMapper != null)
                resultEntity = this.extendedToEntityMapper.toEntity(dto, resultEntity, context);

            return resultEntity;
        };
    }

    public static <D extends BaseDto, E extends AbstractBaseEntity> MappingConfiguration<D, E> of(Class<E> entityClass, Class<D> dtoClass) {
        return new MappingConfiguration<>(entityClass, dtoClass);
    }

    public MappingConfiguration<D, E> withNewEntityInstanceCreator(NewEntityInstanceCreator<E> newEntityInstanceCreator) {
        this.newEntityInstanceCreator = newEntityInstanceCreator;
        return this;
    }

    public MappingConfiguration<D, E> withMapNestedFields(boolean mapNestedFields) {
        this.mapNestedFields = mapNestedFields;
        return this;
    }

    public MappingConfiguration<D, E> withNewDtoInstanceCreator(NewDtoInstanceCreator<D> newDtoInstanceCreator) {
        this.newDtoInstanceCreator = newDtoInstanceCreator;
        return this;
    }

    public MappingConfiguration<D, E> withToEntityMapper(ToEntityMapper<D, E> toEntityMapper) {
        this.toEntityMapper = toEntityMapper;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public MappingConfiguration<D, E> withExtendedToEntityMapper(ToEntityMapper<D, E> toEntityMapper) {
        this.extendedToEntityMapper = toEntityMapper;
        return this;
    }

    public MappingConfiguration<D, E> withToDtoMapper(ToDtoMapper<D, E> toDtoMapper) {
        this.toDtoMapper = toDtoMapper;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public MappingConfiguration<D, E> withExtendedToDtoMapper(ToDtoMapper<D, E> toDtoMapper) {
        this.extendedToDtoMapper = toDtoMapper;
        return this;
    }

    public interface EntityDtoMapper<E, D> extends NewEntityInstanceCreator<E>, NewDtoInstanceCreator<D>, ToEntityMapper<D, E>, ToDtoMapper<D, E> {

    }


    public interface NewEntityInstanceCreator<E> {
        E createNewEntityInstance(MappingContext mappingContext);
    }

    public interface NewDtoInstanceCreator<D> {
        D createNewDtoInstance(MappingContext mappingContext);
    }

    public interface ToEntityMapper<D, E> {
        E toEntity(D dto, E target, MappingContext mappingContext);
    }

    public interface ToDtoMapper<D, E> {
        D toDto(E entity, D target, MappingContext mappingContext);
    }

}
