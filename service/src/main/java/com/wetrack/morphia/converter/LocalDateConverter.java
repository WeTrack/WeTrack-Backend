package com.wetrack.morphia.converter;

import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Converter used to save {@link LocalDate} into MongoDB as String
 */
public class LocalDateConverter extends TypeConverter implements SimpleValueConverter {
    private static final Logger LOG = LoggerFactory.getLogger(LocalDateConverter.class);

    public LocalDateConverter() {
        super(LocalDate.class, LocalDateTime.class);
    }

    @Override
    public Object decode(Class<?> targetClass, Object fromDBObject, MappedField optionalExtraInfo) {
        if (fromDBObject == null)
            return null;

        Date date = (Date) fromDBObject;
        if (targetClass.isAssignableFrom(LocalDate.class))
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        else if (targetClass.isAssignableFrom(LocalDateTime.class))
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        else
            throw new IllegalArgumentException("This converter does not support the given target type "
                    + targetClass.getName());
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        if (value == null)
            return null;

        if (value instanceof LocalDate) {
            LocalDate localDate = (LocalDate) value;
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else if (value instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) value;
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } else
            throw new IllegalArgumentException("This converter does not support encoding the given value of type "
                    + value.getClass().getName());
    }
}
