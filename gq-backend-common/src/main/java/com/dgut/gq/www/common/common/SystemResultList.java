package com.dgut.gq.www.common.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 封装返回值集合集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemResultList<T> {

    private List<T> list;

    private  Integer count;
}
