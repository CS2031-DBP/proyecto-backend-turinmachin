package com.turinmachin.unilife.post.domain;

import com.turinmachin.unilife.image.domain.ImageService;
import com.turinmachin.unilife.post.infrastructure.PostRepository;
import com.turinmachin.unilife.post.infrastructure.PostVoteRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


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

    public List<Post> getAllPosts() {
        return postRepository.findAll(Sort.by("createdAt").descending());
    }

    public List<Post> getPostsWithSpec(Specification<Post> spec) {
        return postRepository.findAll(spec, Sort.by("createdAt").descending());
    }

    public Optional<Post> getPostById(UUID id) {
        return postRepository.findById(id);
    }


}
