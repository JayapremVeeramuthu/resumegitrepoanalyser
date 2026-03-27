package com.devlens.devlensai.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devlens.devlensai.entity.Resume;
import com.devlens.devlensai.repository.ResumeRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ResumeRepository resumeRepository;

    // ✅ GET ALL RESUMES
    @GetMapping("/resumes")
    public List<Resume> getAllResumes() {
        return resumeRepository.findAll();
    }

    // ✅ TEST ENDPOINT
    @GetMapping("/test")
    public String test() {
        return "Admin API Working 🚀";
    }
}