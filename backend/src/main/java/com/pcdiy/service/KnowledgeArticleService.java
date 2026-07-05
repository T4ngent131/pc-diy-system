package com.pcdiy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pcdiy.entity.KnowledgeArticle;
import java.util.List;

public interface KnowledgeArticleService extends IService<KnowledgeArticle> {
    KnowledgeArticle getWithViews(Long id);
    List<KnowledgeArticle> getByCategory(String category);
    List<String> getCategories();
}
