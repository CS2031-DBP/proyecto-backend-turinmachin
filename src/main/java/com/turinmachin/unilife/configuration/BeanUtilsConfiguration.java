package com.turinmachin.unilife.configuration;

import java.util.List;
import java.util.Optional;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.turinmachin.unilife.comment.domain.Comment;
import com.turinmachin.unilife.comment.dto.CreateCommentDto;
import com.turinmachin.unilife.common.domain.ListMapper;
import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.post.domain.PostVote;
import com.turinmachin.unilife.post.dto.PostResponseDto;
import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.university.dto.CreateUniversityDto;
import com.turinmachin.unilife.user.domain.User;

@Configuration
public class BeanUtilsConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

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

        modelMapper.typeMap(Post.class, PostResponseDto.class)
                .addMappings(mapper -> mapper.using(votesToCurrentVote).map(Post::getVotes,
                        PostResponseDto::setCurrentVote));

        modelMapper.typeMap(CreateCommentDto.class, Comment.class)
                .addMappings(mapper -> mapper.skip(Comment::setParent));

        modelMapper.typeMap(CreateUniversityDto.class, University.class)
                .addMappings(mapper -> mapper.skip(University::setDegrees));

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
