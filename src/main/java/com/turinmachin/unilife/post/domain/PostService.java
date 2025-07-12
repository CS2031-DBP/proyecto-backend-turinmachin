package com.turinmachin.unilife.post.domain;

import com.turinmachin.unilife.common.exception.ConflictException;
import com.turinmachin.unilife.fileinfo.domain.FileInfo;
import com.turinmachin.unilife.fileinfo.domain.FileInfoService;
import com.turinmachin.unilife.perspective.domain.PerspectiveService;
import com.turinmachin.unilife.perspective.exception.ToxicContentException;
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

    private final PerspectiveService perspectiveService;

    public Page<Post> getPostsWithSpec(final Specification<Post> spec, Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        return postRepository.findAll(spec, pageable);
    }

    public Optional<Post> getActivePostById(final UUID id) {
        return postRepository.findByIdAndActiveTrue(id);
    }

    public Page<Post> search(final String query, final UUID authorId, final UUID universityId, final UUID degreeId,
            final List<String> tags,
            final Pageable pageable) {
        return postRepository.search(query, authorId, universityId, degreeId, tags, pageable);
    }

    public Post createPost(final CreatePostDto dto, final User author) throws IOException {
        if (author.getUniversity() == null) {
            throw new UserWithoutUniversityException();
        }

        if (perspectiveService.isToxic(dto.getContent())) {
            throw new ToxicContentException();
        }

        final Post post = modelMapper.map(dto, Post.class);
        post.setAuthor(author);
        post.setUniversity(author.getUniversity());
        post.setDegree(author.getDegree());
        post.setTags(dto.getTags().stream().map(String::toLowerCase).map(String::trim).sorted().toList());

        final List<FileInfo> files = fileInfoService.createFileBatch(dto.getFiles());
        post.setFiles(files);

        return postRepository.save(post);
    }

    public Post updatePost(final Post post, final UpdatePostDto dto) {
        post.setContent(dto.getContent());

        final List<String> tags = post.getTags();
        tags.clear();
        tags.addAll(dto.getTags().stream().map(String::toLowerCase).map(String::trim).sorted().toList());

        return postRepository.save(post);
    }

    public Post deactivatePost(final Post post) {
        fileInfoService.triggerFileBatchDeletion(post.getFiles());

        post.setActive(false);
        return postRepository.save(post);
    }

    public Optional<PostVote> getPostVote(final UUID postId, final UUID userId) {
        return postVoteRepository.findById(new PostVoteId(postId, userId));
    }

    public PostVote setPostVote(final Post post, final User user, final VoteType voteType) {
        final PostVoteId voteId = new PostVoteId(post.getId(), user.getId());

        final PostVote vote = postVoteRepository.findById(voteId)
                .orElseGet(() -> {
                    final PostVote newVote = new PostVote();
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

    public void removePostVote(final PostVote vote) {
        postVoteRepository.delete(vote);
    }

    public Page<Post> getPostsUpvotedBy(UUID userId, Pageable pageable) {
        return postRepository.findUpvotedBy(userId, pageable);
    }

}
