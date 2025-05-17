package com.chungang.capstone.openstep.domain.Task.repository;

import com.chungang.capstone.openstep.domain.Task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
