package com.pcdiy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcdiy.entity.KnowledgeArticle;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface KnowledgeArticleMapper extends BaseMapper<KnowledgeArticle> {
    @Update("UPDATE knowledge_articles SET views = views + 1 WHERE id = #{id}")
    void incrementViews(@Param("id") Long id);
}
