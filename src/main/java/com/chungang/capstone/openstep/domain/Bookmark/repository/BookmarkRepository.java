package com.chungang.capstone.openstep.domain.Bookmark.repository;

import com.chungang.capstone.openstep.domain.Bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
