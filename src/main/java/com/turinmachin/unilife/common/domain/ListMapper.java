package com.turinmachin.unilife.common.domain;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.Collection;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ListMapper {

    private final ModelMapper modelMapper;

    public <T, D> Stream<D> map(Collection<T> source, Class<D> destinationType) {
        return source.stream().map(obj -> modelMapper.map(obj, destinationType));
    }

}
