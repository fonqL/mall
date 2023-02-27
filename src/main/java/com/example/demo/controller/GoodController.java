package com.example.demo.controller;

import com.example.demo.entity.GoodDetail;
import com.example.demo.entity.GoodSummary;
import com.example.demo.entity.Tag;
import com.example.demo.mapper.GoodMapper;
import com.example.demo.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/good")
public class GoodController {

	@Autowired
	public GoodMapper goodMapper;

	@Autowired
	public TagService tagService;

	@GetMapping("/listTag")
	public List<Tag> listTag() {
		return tagService.list();
	}

	@GetMapping("/listGood")
	public List<GoodSummary> listGood(Integer page, Integer pagesize) {
		return goodMapper.summaries(page * pagesize, pagesize);
	}

	@GetMapping("/detail")
	public GoodDetail detail(int id) {
		return goodMapper.detail(id);
	}

}
