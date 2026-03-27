package com.devlens.devlensai.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devlens.devlensai.entity.Resume;
import com.devlens.devlensai.repository.ResumeRepository;
import com.devlens.devlensai.service.ResumeService;

@RestController
@RequestMapping("/candidate")
@CrossOrigin   // 🔥 IMPORTANT (frontend connection)
public class ResumeController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private ResumeRepository resumeRepository; // 🔥 ADD THIS

    @PostMapping("/upload")
    public Object uploadResume(@RequestParam("file") MultipartFile file) {

        try {

            // ❌ Empty check
            if (file.isEmpty()) {
                return "❌ File is empty!";
            }

            // 📁 Create folder
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 🛡 Safe filename
            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.contains("..")) {
                return "❌ Invalid file name!";
            }

            // 💾 Save file
            String filePath = UPLOAD_DIR + fileName;
            file.transferTo(new File(filePath));

            // 🔥 TEXT EXTRACTION
            String content = resumeService.extractText(filePath);
            String lower = content.toLowerCase();

            // 🔥 SKILLS LIST
            String[] skillsList = {
                "figma", "ui", "ux", "user experience", "user interface",
                "design", "wireframe", "prototype",
                "photoshop", "illustrator",
                "html", "css", "javascript",
                "java", "spring", "react"
            };

            List<String> matchedSkills = new ArrayList<>();

            for (String skill : skillsList) {
                if (lower.contains(skill)) {
                    matchedSkills.add(skill);
                }
            }

            // 🔥 FALLBACK
            if (matchedSkills.isEmpty()) {
                matchedSkills.add("general skills");
                matchedSkills.add("communication");
            }

            // 🔥 SCORE
            int score = (matchedSkills.size() * 100) / skillsList.length;

            // 🔥 SUGGESTIONS
            List<String> suggestions = new ArrayList<>();
            if (score < 50) {
                suggestions.add("Add more relevant technical skills");
                suggestions.add("Improve project descriptions");
            } else if (score < 80) {
                suggestions.add("Good profile, add advanced tools or certifications");
            } else {
                suggestions.add("Excellent resume, ready for top roles");
            }

            // 🔥 SAVE TO DATABASE (THIS WAS MISSING)
            Resume resume = new Resume();
            resume.setFileUrl(fileName);
            resume.setExtractedSkills(String.join(",", matchedSkills));
            resume.setExperienceYears(0);

            resumeRepository.save(resume);

            // 🔥 RESPONSE
            Map<String, Object> response = new HashMap<>();
            response.put("score", score);
            response.put("skills", matchedSkills);
            response.put("suggestions", suggestions);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }
}