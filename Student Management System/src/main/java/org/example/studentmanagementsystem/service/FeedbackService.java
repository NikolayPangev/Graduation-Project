package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Feedback;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public void save(Feedback feedback) {
        this.feedbackRepository.save(feedback);
    }

    public List<Feedback> findFeedbackByStudent(Student student) {
        return feedbackRepository.findByStudent(student);
    }
}
