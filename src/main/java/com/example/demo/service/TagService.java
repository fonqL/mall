package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Tag;
import com.example.demo.mapper.TagMapper;
import org.springframework.stereotype.Service;

@Service
public class TagService extends ServiceImpl<TagMapper, Tag> {
}
