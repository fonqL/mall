package com.example.demo.controller;

import com.example.demo.entity.Good;
import com.example.demo.mapper.GoodMapper;
import com.example.demo.service.OrderSerivice;
import com.example.demo.service.StorageService;
import com.example.demo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Value("${Admin.pswd}")
	public String pswd;

	@Value("${document.uploadPath}")
	public String uploadPath;

	@Autowired
	public OrderSerivice orderSerivice;

	@Autowired
	public GoodMapper goodMapper;

	@Autowired
	public JwtUtil jwtUtil;

	@Autowired
	public StorageService storageService;

	@PostMapping("/login")
	public String login(String pswd) {
		if (!pswd.equals(this.pswd))
			return null;
		return jwtUtil.createJwt("0", "Admin");
	}

	@PostMapping("/add")
	public void add(String name, int price, String info, Integer tagid,
	                MultipartFile file, HttpServletResponse response) throws IOException {
		// String jwt = request.getHeader("Auth");
		// if (jwt == null || jwt.isEmpty())
		// 	return "请先登录"; //or throw?
		// Claims claims = jwtUtil.parseJwt(jwt);

		String path = storageService.store(file);
		goodMapper.insert(name, price, info, tagid == null ? 1 : tagid.intValue(), path);
		response.sendRedirect("http://localhost:8080");
	}

	@PostMapping("/delete")
	public Object delete(int id, HttpServletRequest request) throws IOException {
		String jwt = request.getHeader("Auth");
		if (jwt == null || jwt.isEmpty())
			return "请先登录"; //or throw?
		Claims claims = jwtUtil.parseJwt(jwt);

		Good good = goodMapper.content(id);
		String path = good.getFilepath();
		Files.delete(Path.of(path));
		goodMapper.delete(id);
		return "ok";
	}

	@PostMapping("/update")
	public Object update(int id, String name, int price, String info, Integer tagid,
	                     MultipartFile file, HttpServletRequest request) {
		String jwt = request.getHeader("Auth");
		if (jwt == null || jwt.isEmpty())
			return "请先登录"; //or throw?
		Claims claims = jwtUtil.parseJwt(jwt);

		Good item = goodMapper.content(id);
		HashMap<String, String> upd = new HashMap<>();
		if (name != null && !name.equals(item.getName())) {
			upd.put("name", name);
		}
		if (price != item.getPrice()) {
			upd.put("price", String.valueOf(price));
		}
		if (info != null && !info.equals(item.getInfo())) {
			upd.put("info", info);
		}
		if (tagid != null && tagid != item.getTagid()) {
			upd.put("tagid", String.valueOf(tagid));
		}
		if (file != null) {
			String path = storageService.store(file);
			storageService.delete(item.getFilepath());
			upd.put("filepath", path);
		}
		goodMapper.update(id, upd);
		return "ok";
	}

	@PostMapping("/list")
	public Object list(Integer page, Integer pagesize,
	                   HttpServletRequest request) {
		String jwt = request.getHeader("Auth");
		if (jwt == null || jwt.isEmpty())
			return "请先登录"; //or throw?
		Claims claims = jwtUtil.parseJwt(jwt);

		return goodMapper.list(page * pagesize, pagesize);
	}

	@PostMapping("/getFile")
	public Object getFile(int id, HttpServletRequest request) {
		String jwt = request.getHeader("Auth");
		if (jwt == null || jwt.isEmpty())
			return "请先登录"; //or throw?
		Claims claims = jwtUtil.parseJwt(jwt);

		Good good = goodMapper.content(id);
		Resource file = storageService.loadAsResource(good.getFilepath());
		String ext = file.getFilename().substring(file.getFilename().lastIndexOf('.'));
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + good.getName() + ext + "\"")
				.body(file);
	}

	@PostMapping("/listOrder")
	public Object listOrder(HttpServletRequest request) {
		String jwt = request.getHeader("Auth");
		if (jwt == null || jwt.isEmpty())
			return "请先登录"; //or throw?
		Claims claims = jwtUtil.parseJwt(jwt);

		return orderSerivice.list();
	}
}
