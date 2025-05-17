package com.chungang.capstone.openstep.domain.Github.service;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;

@Service
public interface GitHubStatusResolverService {
    TaskStatus resolveStatus(Task task, Member member);
}