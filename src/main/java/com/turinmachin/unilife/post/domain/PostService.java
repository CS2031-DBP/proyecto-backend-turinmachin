package com.turinmachin.unilife.post.domain;

import com.turinmachin.unilife.image.domain.ImageService;
import com.turinmachin.unilife.post.infrastructure.PostRepository;
import com.turinmachin.unilife.post.infrastructure.PostVoteRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ModelMapper modelMapper;


}
