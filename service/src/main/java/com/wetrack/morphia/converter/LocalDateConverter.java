package com.wetrack.morphia.converter;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converter used to save {@link LocalDate} into MongoDB as String
 */
public class LocalDateConverter extends TypeConverter implements SimpleValueConverter {
    private static final Logger LOG = LoggerFactory.getLogger(LocalDateConverter.class);

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");

    public LocalDateConverter() {
        super(LocalDate.class);
    }

    @Override
    public Object decode(Class<?> targetClass, Object fromDBObject, MappedField optionalExtraInfo) {
        if (fromDBObject == null)
            return null;

        LOG.debug("Received DB object `{}`", fromDBObject);
        return LocalDate.parse((String) fromDBObject, formatter);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        if (value == null)
            return null;

        LocalDate localDate = (LocalDate) value;
        return localDate.toString(formatter);
    }
}
