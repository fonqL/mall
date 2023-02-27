package com.example.demo.mapper;

import com.example.demo.entity.Good;
import com.example.demo.entity.GoodDetail;
import com.example.demo.entity.GoodSummary;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoodMapper {
	List<GoodSummary> summaries(int off, int len);

	GoodDetail detail(int id);

	Good content(int id);

	void delete(int id);

	void insert(String name, int price, String info, int tagid, String filepath);

	void update(int id, Map<String, String> vars);

	List<GoodDetail> list(int off, int len);
}
