package com.turinmachin.unilife.post.domain;

import com.azure.ai.inference.ChatCompletionsAsyncClient;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;
import com.turinmachin.unilife.common.exception.ConflictException;
import com.turinmachin.unilife.fileinfo.domain.FileInfo;
import com.turinmachin.unilife.fileinfo.domain.FileInfoService;
import com.turinmachin.unilife.perspective.domain.PerspectiveService;
import com.turinmachin.unilife.perspective.exception.ToxicContentException;
import com.turinmachin.unilife.post.dto.CreatePostDto;
import com.turinmachin.unilife.post.dto.UpdatePostDto;
import com.turinmachin.unilife.post.exception.TagGenerationException;
import com.turinmachin.unilife.post.infrastructure.PostRepository;
import com.turinmachin.unilife.post.infrastructure.PostVoteRepository;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserStreakService;
import com.turinmachin.unilife.user.exception.UserWithoutUniversityException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final UserStreakService userStreakService;
    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final FileInfoService fileInfoService;
    private final ModelMapper modelMapper;
    private final PerspectiveService perspectiveService;
    private final ChatCompletionsAsyncClient client;
    private final String defaultModel;

    @Value("${ai.tag.limit}")
    private int tagLimit;

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

        Post post = modelMapper.map(dto, Post.class);
        post.setAuthor(author);
        post.setUniversity(author.getUniversity());
        post.setDegree(author.getDegree());
        post.setTags(dto.getTags().stream().map(String::toLowerCase).map(String::trim).sorted().toList());

        final List<FileInfo> files = fileInfoService.createFileBatch(dto.getFiles());
        post.setFiles(files);

        post = postRepository.save(post);
        userStreakService.handlePostCreated(post);
        return post;
    }

    public Post updatePost(final Post post, final UpdatePostDto dto) {
        post.setContent(dto.getContent());

        final List<String> tags = post.getTags();
        tags.clear();
        tags.addAll(dto.getTags().stream().map(String::toLowerCase).map(String::trim).sorted().toList());

        return postRepository.save(post);
    }

    public Post deletePost(Post post) {
        fileInfoService.triggerFileBatchDeletion(post.getFiles());

        post.setActive(false);
        post = postRepository.save(post);

        userStreakService.handlePostDeleted(post);
        return post;
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

        final short originalVoteValue = Optional.ofNullable(vote.getValue())
                .map(pv -> pv.getValue())
                .orElse((short) 0);

        vote.setValue(voteType);
        post.setScore(post.getScore() - originalVoteValue + voteType.getValue());
        postRepository.save(post);
        return postVoteRepository.save(vote);
    }

    public void removePostVote(final PostVote vote) {
        final Post post = vote.getPost();

        post.setScore(post.getScore() - vote.getValue().getValue());
        postRepository.save(post);
        postVoteRepository.delete(vote);
    }

    public Page<Post> getPostsUpvotedBy(final UUID userId, final Pageable pageable) {
        return postRepository.findUpvotedBy(userId, pageable);
    }

    @Cacheable("AiTags")
    public List<String> generateTags(final String content) {

        final List<ChatRequestMessage> messages = new ArrayList<>();
        messages.add(new ChatRequestSystemMessage("Extrae " + tagLimit + " etiquetas claras, concisas y relevantes en minúsculas para la siguiente publicación. Devuélvelas como una lista separada por comas. No incluyas hashtags ni texto adicional. Envíalas en orden de relevancia. Si el contenido es ofensivo, inapropiado u tóxico, responde con una cadena vacía."));
        messages.add(new ChatRequestUserMessage(content));

        final ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
        options.setModel(defaultModel);

        final ChatCompletions completions = client.complete(options).block();

        final String response = Optional.ofNullable(completions)
                .map(ChatCompletions::getChoices)
                .filter(choices -> !choices.isEmpty())
                .map(choices -> choices.getFirst().getMessage().getContent())
                .orElseThrow(TagGenerationException::new);

        if (response == null || response.isEmpty()) {
            throw new TagGenerationException();
        }

        return Arrays.stream(response.split(","))
                .map(String::trim)
                .map(s -> s.replaceAll("[^a-zA-Z0-9áéíóúñÁÉÍÓÚÑ]", "").toLowerCase())
                .filter(s -> !s.isBlank())
                .distinct()
                .limit(tagLimit)
                .toList();
    }

}
