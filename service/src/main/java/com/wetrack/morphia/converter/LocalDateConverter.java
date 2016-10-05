package com.wetrack.morphia.converter;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converter used to save {@link LocalDate} into MongoDB as String
 */
public class LocalDateConverter extends TypeConverter {
    private static final Logger LOG = LoggerFactory.getLogger(LocalDateConverter.class);

    private static final DateTimeFormatter formatter = ISODateTimeFormat.localDateParser();

    public LocalDateConverter() {
        super(LocalDate.class);
    }

    @Override
    public Object decode(Class<?> targetClass, Object fromDBObject, MappedField optionalExtraInfo) {
        if (fromDBObject == null)
            return null;

        return LocalDate.parse((String) fromDBObject, formatter);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        if (value == null)
            return null;

        LocalDate dateTime = (LocalDate) value;

        return formatter.print(dateTime);
    }
}
