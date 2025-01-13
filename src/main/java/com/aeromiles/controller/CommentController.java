package com.aeromiles.controller;

import com.aeromiles.model.Comment;
import com.aeromiles.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("")
    public List<Comment> getComments() {
        return commentService.getComments();
    }

    @GetMapping("/error")
    public String error() {
        return "An error occurred!";
    }
}   
