package com.lzh0108.community.dao;

import com.lzh0108.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {


    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCommentByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

}
