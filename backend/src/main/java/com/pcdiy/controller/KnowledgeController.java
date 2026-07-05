package com.pcdiy.controller;

import com.pcdiy.dto.ApiResponse;
import com.pcdiy.entity.KnowledgeArticle;
import com.pcdiy.service.KnowledgeArticleService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeArticleService articleService;

    public KnowledgeController(KnowledgeArticleService articleService) {
        this.articleService = articleService;
    }

    /** 获取文章列表 */
    @GetMapping
    public ApiResponse<List<KnowledgeArticle>> list(@RequestParam(required = false) String category) {
        return ApiResponse.success(articleService.getByCategory(category));
    }

    /** 获取文章详情（含阅读计数） */
    @GetMapping("/{id}")
    public ApiResponse<KnowledgeArticle> detail(@PathVariable Long id) {
        return ApiResponse.success(articleService.getWithViews(id));
    }

    /** 获取分类列表 */
    @GetMapping("/categories")
    public ApiResponse<List<String>> categories() {
        return ApiResponse.success(articleService.getCategories());
    }
}
