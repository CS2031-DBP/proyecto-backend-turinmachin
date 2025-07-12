package com.turinmachin.unilife.user.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.turinmachin.unilife.post.domain.Post;
import com.turinmachin.unilife.post.infrastructure.PostRepository;
import com.turinmachin.unilife.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserStreakService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void handlePostCreated(final Post post) {
        postRepository.flush();
        final Post updatedPost = postRepository.findById(post.getId()).orElseThrow();

        final User author = post.getAuthor();
        final LocalDate last = author.getLastStreakDate();
        final LocalDate postDate = updatedPost.getCreatedAt().atOffset(ZoneOffset.UTC).toLocalDate();

        if (last == null || postDate.isAfter(last.plusDays(1))) {
            author.setStreakValue(1);
        } else if (postDate.isEqual(last.plusDays(1))) {
            author.setStreakValue(author.getStreakValue() + 1);
        }

        if (last == null || postDate.isAfter(last)) {
            author.setLastStreakDate(postDate);
        }

        userRepository.save(author);
    }

    public void handlePostDeleted(final Post post) {
        final User author = post.getAuthor();
        final LocalDate postDate = post.getCreatedAt().atOffset(ZoneOffset.UTC).toLocalDate();

        if (author.getStreak() == 0)
            return;

        final LocalDate last = author.getLastStreakDate();

        if (last == null || !postDate.isEqual(last))
            return;

        final Instant start = postDate.atStartOfDay().atOffset(ZoneOffset.UTC).toInstant();
        final Instant end = postDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).toInstant().minusNanos(1);
        if (!postRepository.existsByActiveTrueAndAuthorIdAndCreatedAtBetween(author.getId(), start, end)) {
            author.setStreakValue(author.getStreakValue() - 1);
            author.setLastStreakDate(null);
            userRepository.save(author);
        }

    }

}
