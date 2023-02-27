package com.example.demo.controller;

import com.example.demo.entity.Good;
import com.example.demo.entity.Order;
import com.example.demo.mapper.GoodMapper;
import com.example.demo.service.OrderSerivice;
import com.example.demo.service.StorageService;
import com.example.demo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	public OrderSerivice orderSerivice;

	@Autowired
	public JwtUtil jwtUtil;

	@Autowired
	public GoodMapper goodMapper;

	@Autowired
	public StorageService storageService;

	@PostMapping("/buy")
	public Object buy(Integer goodid, HttpServletRequest request) {
		String jwt = request.getHeader("Auth");
		if (jwt == null || jwt.isEmpty())
			return "login";
		Claims claims = jwtUtil.parseJwt(jwt);
		int userid = Integer.parseInt(claims.getId());
		orderSerivice.save(new Order(null, goodid, userid, 0));
		Good x = goodMapper.content(goodid);
		Resource file = storageService.loadAsResource(x.getFilepath());
		String ext = file.getFilename().substring(file.getFilename().lastIndexOf('.'));
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + x.getName() + ext + "\"")
				.body(file);
	}

}
