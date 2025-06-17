package com.turinmachin.unilife.post.domain;

import com.turinmachin.unilife.common.exception.ConflictException;
import com.turinmachin.unilife.fileinfo.domain.FileInfo;
import com.turinmachin.unilife.fileinfo.domain.FileInfoService;
import com.turinmachin.unilife.post.dto.CreatePostDto;
import com.turinmachin.unilife.post.dto.UpdatePostDto;
import com.turinmachin.unilife.post.infrastructure.PostRepository;
import com.turinmachin.unilife.post.infrastructure.PostVoteRepository;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.exception.UserWithoutUniversityException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final PostVoteRepository postVoteRepository;

    private final FileInfoService fileInfoService;

    private final ModelMapper modelMapper;

    public Page<Post> getPostsWithSpec(Specification<Post> spec, Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        return postRepository.findAll(spec, pageable);
    }

    public Optional<Post> getActivePostById(UUID id) {
        return postRepository.findByIdAndActiveTrue(id);
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

        List<FileInfo> files = fileInfoService.createFileBatch(dto.getFiles());
        post.setFiles(files);

        return postRepository.save(post);
    }

    public Post updatePost(Post post, UpdatePostDto dto) {
        post.setContent(dto.getContent());

        List<String> tags = post.getTags();
        tags.clear();
        tags.addAll(dto.getTags().stream().map(String::toLowerCase).map(String::trim).sorted().toList());

        return postRepository.save(post);
    }

    public Post deactivatePost(Post post) {
        fileInfoService.triggerFileBatchDeletion(post.getFiles());

        post.setActive(false);
        return postRepository.save(post);
    }

    public Optional<PostVote> getPostVote(UUID postId, UUID userId) {
        return postVoteRepository.findById(new PostVoteId(postId, userId));
    }

    public PostVote setPostVote(Post post, User user, VoteType voteType) {
        PostVoteId voteId = new PostVoteId(post.getId(), user.getId());

        PostVote vote = postVoteRepository.findById(voteId)
                .orElseGet(() -> {
                    PostVote newVote = new PostVote();
                    newVote.setId(voteId);
                    newVote.setPost(post);
                    newVote.setAuthor(user);
                    return newVote;
                });

        if (vote.getValue() == voteType) {
            throw new ConflictException("Vote already present");
        }

        vote.setValue(voteType);
        return postVoteRepository.save(vote);
    }

    public void removePostVote(PostVote vote) {
        postVoteRepository.delete(vote);
    }

}
