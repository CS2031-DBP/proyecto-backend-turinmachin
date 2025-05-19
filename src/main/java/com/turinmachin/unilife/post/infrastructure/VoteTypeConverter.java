package com.turinmachin.unilife.post.infrastructure;

import com.turinmachin.unilife.post.domain.VoteType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class VoteTypeConverter implements AttributeConverter<VoteType, Short> {

    @Override
    public Short convertToDatabaseColumn(VoteType voteType) {
        return voteType.getValue();
    }

    @Override
    public VoteType convertToEntityAttribute(Short dbValue) {
        return VoteType.fromValue(dbValue);
    }

}
