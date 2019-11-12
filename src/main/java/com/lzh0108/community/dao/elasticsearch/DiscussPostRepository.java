package com.lzh0108.community.dao.elasticsearch;

import com.lzh0108.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

// 泛型中声明处理的实体类是什么，实体类当中的主键是什么类型
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {

}
