package com.chungang.capstone.openstep.domain.Bookmark.service;

import com.chungang.capstone.openstep.domain.Bookmark.repository.BookmarkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookmarkQueryService {
    private final BookmarkRepository bookmarkRepository;

    public Long findBookmarkIdByMemberAndRepo(Long memberId, Long repoId) {
        return bookmarkRepository.findBookmarkIdByMemberAndRepo(memberId, repoId);
    }

}
