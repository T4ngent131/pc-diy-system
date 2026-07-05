package com.pcdiy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pcdiy.entity.KnowledgeArticle;
import com.pcdiy.mapper.KnowledgeArticleMapper;
import com.pcdiy.service.KnowledgeArticleService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeArticleServiceImpl extends ServiceImpl<KnowledgeArticleMapper, KnowledgeArticle> implements KnowledgeArticleService {

    @Override
    public KnowledgeArticle getWithViews(Long id) {
        baseMapper.incrementViews(id);
        return getById(id);
    }

    @Override
    public List<KnowledgeArticle> getByCategory(String category) {
        if ("all".equals(category) || category == null) {
            return list(new LambdaQueryWrapper<KnowledgeArticle>()
                    .eq(KnowledgeArticle::getStatus, 1)
                    .orderByDesc(KnowledgeArticle::getCreatedAt));
        }
        return list(new LambdaQueryWrapper<KnowledgeArticle>()
                .eq(KnowledgeArticle::getCategory, category)
                .eq(KnowledgeArticle::getStatus, 1)
                .orderByDesc(KnowledgeArticle::getCreatedAt));
    }

    @Override
    public List<String> getCategories() {
        return list(new LambdaQueryWrapper<KnowledgeArticle>()
                        .select(KnowledgeArticle::getCategory)
                        .eq(KnowledgeArticle::getStatus, 1)
                        .groupBy(KnowledgeArticle::getCategory))
                .stream()
                .map(KnowledgeArticle::getCategory)
                .collect(Collectors.toList());
    }
}
