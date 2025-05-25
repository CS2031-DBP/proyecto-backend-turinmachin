package com.turinmachin.unilife.post.domain;

import com.turinmachin.unilife.image.domain.Image;
import com.turinmachin.unilife.image.domain.ImageService;
import com.turinmachin.unilife.post.dto.CreatePostDto;
import com.turinmachin.unilife.post.dto.UpdatePostDto;
import com.turinmachin.unilife.post.infrastructure.PostRepository;
import com.turinmachin.unilife.post.infrastructure.PostVoteRepository;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.exception.UserWithoutUniversityException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public Post createPost(CreatePostDto dto, User author) throws IOException {
        if (author.getUniversity() == null) {
            throw new UserWithoutUniversityException();
        }

        Post post = modelMapper.map(dto, Post.class);
        post.setAuthor(author);
        post.setUniversity(author.getUniversity());
        post.setDegree(author.getDegree());
        post.setTags(dto.getTags().stream().map(String::toLowerCase).map(String::trim).sorted().toList());

        List<Image> images = imageService.createImageBatch(dto.getImages());
        post.setImages(images);

        return postRepository.save(post);
    }

    public Post updatePost(Post post, UpdatePostDto dto) {
        post.setContent(dto.getContent());
        post.setTags(dto.getTags().stream().map(String::toLowerCase).map(String::trim).sorted().toList());

        // TODO: support updating post images

        return postRepository.save(post);
    }

    public void deletePost(Post post) {
        imageService.deleteImageBatch(post.getImages());
        postRepository.delete(post);
    }

    public Optional<PostVote> getPostVote(UUID postId, UUID userId) {
        return postVoteRepository.findById(new PostVoteId(postId, userId));
    }


}
