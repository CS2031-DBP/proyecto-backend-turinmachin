package com.turinmachin.unilife.configuration;

import com.turinmachin.unilife.common.domain.ListMapper;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.post.domain.PostVote;
import com.turinmachin.unilife.post.dto.PostResponseDto;
import com.turinmachin.unilife.user.domain.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Configuration
public class BeanUtilsConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<Post, PostResponseDto> postPropertyMapper = modelMapper.createTypeMap(Post.class,
                PostResponseDto.class);

        Converter<List<PostVote>, Short> votesToCurrentVote = v -> Optional
                .ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> (User) auth.getPrincipal())
                .map(user -> v.getSource()
                        .stream()
                        .filter(vote -> vote.getAuthor().equals(user))
                        .findFirst()
                        .map(vote -> vote.getValue().getValue())
                        .orElse((short) 0))
                .orElse(null);

        postPropertyMapper.addMappings(
                mapper -> mapper.using(votesToCurrentVote).map(Post::getVotes, PostResponseDto::setCurrentVote));

        return modelMapper;
    }

    @Bean
    public ListMapper listMapper(ModelMapper modelMapper) {
        return new ListMapper(modelMapper);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
