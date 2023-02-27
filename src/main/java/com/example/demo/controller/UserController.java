package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	public UserService service;

	@Autowired
	public JwtUtil jwtUtil;

	@PostMapping("/login")
	public Map<String, Object> login(String name, String pswd) {
		var x = new QueryWrapper<User>()
				.eq("name", name)
				.eq("pswd", pswd);
		var res = service.getOne(x, false);

		if (res == null)
			return null;
		var ret = new HashMap<String, Object>();
		ret.put("jwt", jwtUtil.createJwt(res.getId().toString(), res.getName()));
		ret.put("profile", res);
		return ret;
	}

	@PostMapping("/profile")
	public User profile(HttpServletRequest request) {
		String auth = request.getHeader("Auth");
		if (auth == null || auth.isEmpty())
			return null; //or throw?
		Claims claims = jwtUtil.parseJwt(auth);
		Integer id = Integer.valueOf(claims.getId());
		User ret = service.getById(id);
		ret.setPswd(null);
		return ret;
	}

	@PostMapping("/regis")
	public String regis(String name, String pswd) {
		long num = service.count(new QueryWrapper<User>().eq("name", name));
		if (num > 0)
			return "该用户名已被注册";
		boolean f = service.save(new User(null, name, pswd)); //auto
		if (!f)
			return "注册失败，请稍后再试";
		return "ok";
	}

	@PostMapping("/update")
	public String update(User newinfo, HttpServletRequest request) {
		String jwt = request.getHeader("Auth");
		if (jwt == null || jwt.isEmpty())
			return "请先登录"; //or throw?
		Claims claims = jwtUtil.parseJwt(jwt);
		Integer id = Integer.valueOf(claims.getId());
		newinfo.setId(id);
		boolean f = service.updateById(newinfo);
		if (!f)
			return "更新失败，请稍后再试";
		return "ok";
	}

	@PostMapping("/delete")
	public String delete(HttpServletRequest request) {
		String jwt = request.getHeader("Auth");
		if (jwt == null || jwt.isEmpty())
			return "请先登录"; //or throw?
		Claims claims = jwtUtil.parseJwt(jwt);
		Integer id = Integer.valueOf(claims.getId());
		boolean f = service.removeById(id);
		if (!f)
			return "删除失败，请稍后再试";
		return "ok";
	}

}

