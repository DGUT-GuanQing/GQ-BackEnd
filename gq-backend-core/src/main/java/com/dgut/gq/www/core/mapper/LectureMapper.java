package com.dgut.gq.www.core.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dgut.gq.www.core.common.model.entity.Lecture;
import org.apache.ibatis.annotations.Mapper;

/**
 * 讲座mapper
 *
 * @author hyj
 * @version 1.0
 * @since 2022-10-1
 */
@Mapper
public interface LectureMapper extends BaseMapper<Lecture> {


    public int countTicket(String id);

}
